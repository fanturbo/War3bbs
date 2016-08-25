package com.war3.comm.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.war3.comm.R;
import com.war3.comm.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    private TextView tvCourseTitle;
    private WebView home_slider_webView;
    private String url = "";
    private String name = "";
    private String shareContent = "";
    private ProgressDialog progressDialog;
    private ImageView ivShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        tvCourseTitle = (TextView) findViewById(R.id.tv_course_title);
        ivShare = (ImageView) findViewById(R.id.share);
        ivShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initShare();
                //Share
            }
        });
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WebViewActivity.this.finish();
            }
        });
        home_slider_webView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("url");
            name = intent.getStringExtra("title");
            shareContent = intent.getStringExtra("shareContent");
        }
        tvCourseTitle.setText(name);
        initwebview(url);
    }

    private void initShare() {
        UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA platform) {
                if (platform.name().equals("WEIXIN_FAVORITE")) {
                    Toast.makeText(WebViewActivity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WebViewActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(WebViewActivity.this, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(WebViewActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };

        new ShareAction(this)
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .withTitle(name)
                .withText(shareContent)
                .withMedia(new UMImage(this, "http://ac-leosxavh.clouddn.com/QCSxjZi1QKAmPJi7byZ2FkaW0EttwbnLdXQsGriR.png"))
                .withTargetUrl(url)
                .setCallback(umShareListener)
                .open();
    }

    private void initwebview(String url) {
        // 设置webview的展示形式
        progressDialog = new ProgressDialog(WebViewActivity.this);
        // 设置ProgressDialog的显示样式，ProgressDialog.STYLE_SPINNER代表的是圆形进度条
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("拼命加载中...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        home_slider_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tvCourseTitle.setText(title);
            }
        });
        home_slider_webView.getSettings().setUseWideViewPort(true);
        home_slider_webView.getSettings().setLoadWithOverviewMode(true);
        home_slider_webView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
//		home_slider_webView.getSettings().setUserAgentString(MyApplication.getUserAgent());
        home_slider_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        home_slider_webView.getSettings().setAppCacheEnabled(true);
        home_slider_webView.getSettings().setDomStorageEnabled(true);
        home_slider_webView.getSettings().setDatabaseEnabled(true);
        home_slider_webView.getSettings().setJavaScriptEnabled(true);
        home_slider_webView.addJavascriptInterface(this, "war3bbs");

        home_slider_webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(WebViewActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
            }
        });
        home_slider_webView.loadUrl(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
