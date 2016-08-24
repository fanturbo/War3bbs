package com.war3.comm.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.war3.comm.R;
import com.war3.comm.utils.HttpUtil;
import com.war3.comm.utils.HttpUtil.OnResponse;

public class WelcomeActivity extends Activity {

    private LinearLayout mControlsView;
    private WebView mContentView;
    private ImageView mContentImageView;
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mContentImageView = (ImageView) findViewById(R.id.fullscreen_imageview);
        mControlsView = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
        mContentView = (WebView) findViewById(R.id.fullscreen_content);
        mContentView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mContentImageView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
        if (NetworkUtils.isConnectedToNetwork(this)) {
            AVQuery<AVObject> avQuery = new AVQuery<>("Guide");
            AVQuery<AVObject> name = avQuery.limit(1);
            name.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    title = avObject.get("name").toString();
                    url = avObject.get("url").toString();
                    mContentView.loadUrl(avObject.get("imageUrl").toString());
                    mControlsView.setVisibility(View.VISIBLE);
                    mContentView.setVisibility(View.VISIBLE);
                    mContentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(WelcomeActivity.this, WebViewActivity.class);
                            intent.putExtra("title", title);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }
            },3000);
        } else {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
        findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
