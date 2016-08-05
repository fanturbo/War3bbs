package com.war3.comm;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.comm.core.constants.Constants;
import com.umeng.commm.ui.beans.War3er;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONObject;

/**
 * 微社区Demo的Application，如果需要自行处理友盟消息推送过来的信息，那么需要在Application中进行设置。
 */
public class CommunityApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlatformConfig.setWeixin("wx96110a1e3af63a39", "c60e3d3ff109a5d17013df272df99199");
        //新浪微博
        PlatformConfig.setSinaWeibo("275392174", "d96fb6b323c60a42ed9f74bfab1b4f7a");
        PlatformConfig.setQQZone("1104606393", "X4BAsJAVKtkDQ1zQ");
        PushAgent.getInstance(this).setDebugMode(true);
        PushAgent.getInstance(this).setMessageHandler(new UmengMessageHandler() {
            @Override
            public void dealWithNotificationMessage(Context arg0, UMessage msg) {
                // 调用父类方法,这里会在通知栏弹出提示信息
                super.dealWithNotificationMessage(arg0, msg);
                Log.e("", "### 自行处理推送消息");
            }
        });
        PushAgent.getInstance(this).setNotificationClickHandler(new UHandler() {
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                com.umeng.comm.core.utils.Log.d("notifi", "getting message");
                try {
                    JSONObject jsonObject = uMessage.getRaw();
                    String feedid = "";
                    if (jsonObject != null) {
                        com.umeng.comm.core.utils.Log.d("json", jsonObject.toString());
                        JSONObject extra = uMessage.getRaw().optJSONObject("extra");
                        feedid = extra.optString(Constants.FEED_ID);
                    }
                    Class myclass = Class.forName(uMessage.activity);
                    Intent intent = new Intent(context, myclass);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.FEED_ID, feedid);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    com.umeng.comm.core.utils.Log.d("class", e.getMessage());
                }
            }
        });
        PushAgent.getInstance(this).enable(new IUmengRegisterCallback() {

            @Override
            public void onRegistered(final String registrationId) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        //onRegistered方法的参数registrationId即是device_token
                        Log.d("device_token", registrationId);
                    }
                });
            }
        });
        CrashReport.initCrashReport(getApplicationContext(), "900044281", false);
        initDefaultCatch();
        AVUser.alwaysUseSubUserClass(War3er.class);
        AVOSCloud.initialize(this, "lEosXaVHjTFifebFQYQojDdv-gzGzoHsz", "qgFkDIsVBzMEPLo8vodz1oDP");
    }

    private void initDefaultCatch() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                CrashReport.postCatchedException(ex);
            }
        });
    }
// 如果发现Method Over 65K的错误的话就反注释这段代码
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
