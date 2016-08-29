package com.war3.comm.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.imageloader.ImgDisplayOption;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;
import com.umeng.common.ui.presenter.impl.LoginSimplify;
import com.umeng.common.ui.util.CommonLoginStrategy;
import com.war3.comm.R;

public class WelcomeActivity extends Activity {

    private CommunitySDK mCommSDK = null;
    private LinearLayout mControlsView;
    private ImageView mContentImageView;
    private String title;
    private String url;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    ImageLoader.getInstance().displayImage((String) msg.obj, mContentImageView);
                    mControlsView.setVisibility(View.VISIBLE);
                    mContentImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(WelcomeActivity.this, WebViewActivity.class);
                            intent.putExtra("title", title);
                            intent.putExtra("url", url);
                            intent.putExtra("shareContent", shareContent);
                            handler.removeCallbacks(runnable);
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            startActivity(intent);
                            finish();
                        }
                    });
                    handler.postDelayed(runnable, 3000);
                    break;
            }
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!WelcomeActivity.this.isFinishing()) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    };
    private String shareContent;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setCatchUncaughtExceptions(false);
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        useCustomLogin();

        mContentImageView = (ImageView) findViewById(R.id.fullscreen_imageview);
        mControlsView = (LinearLayout) findViewById(R.id.fullscreen_content_controls);

        if (NetworkUtils.isConnectedToNetwork(this)) {
            AVQuery<AVObject> avQuery = new AVQuery<>("Guide");
            AVQuery<AVObject> name = avQuery.limit(1);
            name.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    String showAd = avObject.get("show").toString();
                    if("0".equals(showAd)){
                        return;
                    }else {
                        title = avObject.get("name").toString();
                        url = avObject.get("url").toString();
                        shareContent = avObject.get("share_content").toString();
                        Message message = new Message();
                        message.obj = avObject.get("imageUrl").toString();
                        message.what = 1000;
                        handler.removeCallbacks(runnable);
                        handler.sendMessageDelayed(message, 1500);
                    }
                }
            });
            handler.postDelayed(runnable, 3000);
        } else {
            handler.postDelayed(runnable, 3000);
        }
        findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                if (!WelcomeActivity.this.isFinishing()) {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
    protected void useCustomLogin() {
        // 管理器
        LoginSDKManager.getInstance().addAndUse(new LoginSimplify());
        CommConfig.getConfig().setLoginResultStrategy(new CommonLoginStrategy());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
