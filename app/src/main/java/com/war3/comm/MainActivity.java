
package com.war3.comm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.Topic;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.impl.CommunityFactory;

import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.login.Loginable;
import com.umeng.comm.core.nets.Request;
import com.umeng.comm.core.nets.responses.AlbumResponse;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.core.nets.responses.TopicResponse;
import com.umeng.comm.core.nets.responses.UsersResponse;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.sdkmanager.LoginSDKManager;

import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.commm.ui.fragments.CommunityMainFragment;

import com.umeng.common.ui.util.CommonLoginStrategy;
import com.umeng.common.ui.widgets.CommunityViewPager;
import com.umeng.community.login.UMAuthService;
import com.umeng.community.login.UMLoginServiceFactory;
import com.umeng.community.share.UMShareServiceFactory;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.handler.QZoneSsoHandler;
import com.umeng.socialize.handler.UMQQSsoHandler;
import com.umeng.socialize.handler.UMWXHandler;
import com.war3.comm.custom.SimpleLoginImpl;
import com.war3.comm.custom.UILImageLoader;


public class MainActivity extends AppCompatActivity {

    CommunitySDK mCommSDK = null;
    String topicId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        CommunityViewPager viewPager = (CommunityViewPager) findViewById(R.id.viewPager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(
                getSupportFragmentManager()) {


            @Override
            public int getCount() {
                return 2;
            }

            public SparseArray<Fragment> caches = new SparseArray<Fragment>();
            @Override
            public Fragment getItem(int arg0) {
                Fragment f = caches.get(arg0);
                if (f == null) {
                    switch (arg0) {
                        case 0:
                            f = new CommunityMainFragment();
                            ((CommunityMainFragment)f).setBackButtonVisibility(View.GONE);
                            break;
                        case 1:
                            f = new BlankFragment();
                            break;
                        case 2:;
                            break;
                        case 3:
                            break;
                    }
                    caches.put(arg0, f);
                }
                return f;
            }
        };
        //设置ViewPager的Adapter
        viewPager.setAdapter(adapter);
        /**如果使用android6.0适配，需要加入以下代码，获取对应权限*/
        String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE};
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(mPermissionList,100);
        }
        // =================== 自定义设置部分 =================
        // 在初始化CommunitySDK之前配置推送和登录等组件
//        useSocialLogin();
//        // 使用自定义的ImageLoader
//         useMyImageLoader();
//        // 使用自定义的登录系统
//        useCustomLogin();
//        initPlatforms(this);
        // 设置地理位置SDK
    }


    private void initPlatforms(FragmentActivity activity) {
//        // 添加QQ
//        UMQQSsoHandler qqHandler = new UMQQSsoHandler(activity, "1104606393",
//                "X4BAsJAVKtkDQ1zQ");
//        qqHandler.addToSocialSDK();
//        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1104606393",
//                "X4BAsJAVKtkDQ1zQ");
//        qZoneSsoHandler.addToSocialSDK();
//        // 添加微信平台
//        UMWXHandler wechatHandler = new UMWXHandler(activity, "wx96110a1e3af63a39",
//                "c60e3d3ff109a5d17013df272df99199");
//        wechatHandler.addToSocialSDK();
//        // 添加微信朋友圈平台
//        UMWXHandler circleHandler = new UMWXHandler(activity, "wx96110a1e3af63a39",
//                "c60e3d3ff109a5d17013df272df99199");
//        circleHandler.setToCircle(true);
//        circleHandler.addToSocialSDK();
//
//        UMShareServiceFactory.getSocialService().getConfig()
//                .setPlatforms(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
//        UMShareServiceFactory.getSocialService().getConfig()
//                .setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
    }

    /**
     * 自定义自己的登录系统
     */
    protected void useSocialLogin() {

//        // 用户自定义的登录
//        UMAuthService mLogin = UMLoginServiceFactory.getLoginService("umeng_login_impl");
//        String appId = "1104606393";
//        String appKey = "X4BAsJAVKtkDQ1zQ";
//        // SSO 设置
//        // mLogin.getConfig().setSsoHandler(new SinaSsoHandler());
//        new UMQQSsoHandler(this, appId, appKey).addToSocialSDK();
//
//        String wxappId = "wx96110a1e3af63a39";
//        String wxappSecret = "c60e3d3ff109a5d17013df272df99199";
//        new UMWXHandler(getApplicationContext(), wxappId,
//                wxappSecret).addToSocialSDK();
//
//        // 将登录实现注入到sdk中,key为umeng_login
//        LoginSDKManager.getInstance().addAndUse(mLogin);

    }

    protected void useCustomLogin() {
        // 管理器
        LoginSDKManager.getInstance().addAndUse(new SimpleLoginImpl());
    }

    /**
     * 自定义自己的ImageLoader
     */
    protected void useMyImageLoader() {
        //
        final String imageLoadKey = UILImageLoader.class.getSimpleName();
        // 使用第三方ImageLoader库,添加到sdk manager中, 并且使用useThis来使用该加载器.
        ImageLoaderManager manager = ImageLoaderManager.getInstance();
        manager.addImpl(imageLoadKey, new UILImageLoader(this));
        manager.useThis(imageLoadKey);
    }

    /**
     * 一些常用的接口以及获取推荐的数据接口
     */
    void someMethodsDemo() {
        // 主动登录
        mCommSDK.login(getApplicationContext(), new LoginListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int stCode, CommUser userInfo) {

            }
        });

        // 获取登录SDK Manager
        LoginSDKManager manager = LoginSDKManager.getInstance();
        Loginable currentLoginable = manager.getCurrentSDK();
        // 是否登录
        //currentLoginable.isLogined(getApplicationContext());

        // 未登录下获取话题
        mCommSDK.fetchTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(TopicResponse response) {
                for (Topic item : response.result) {
                    Log.e("", "### topic id : " + item.id + ", name = " + item.name);
                    topicId = item.id;
                }

            }
        });

        // 未登录情况下获取某个话题下的feed
        mCommSDK.fetchTopicFeed(topicId, new
                FetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        Log.e("", "### 未登录下获取到某个topic下的feed : " + response.result.size());
                        for (FeedItem item : response.result) {
                            Log.e("", "### topic feed id : " + item.id + ", name = " +
                                    item.text);
                        }

                    }

                    @Override
                    public void onStart() {
                    }
                });

        // 推荐的feed
        mCommSDK.fetchRecommendedFeeds(new FetchListener<FeedsResponse>() {

            @Override
            public void onComplete(FeedsResponse response) {
                Log.e("", "### 推荐feed  code : " + response.errCode + ", msg = " + response.errMsg);
                for (FeedItem item : response.result) {
                    Log.e("", "### 推荐feed id : " + item.id + ", name = " + item.text);
                }
            }

            @Override
            public void onStart() {

            }
        });

        // 获取推荐的话题
        mCommSDK.fetchRecommendedTopics(new FetchListener<TopicResponse>() {

            @Override
            public void onComplete(TopicResponse response) {
                Log.e("", "### 推荐的话题 : ");
                for (Topic item : response.result) {
                    Log.e("", "### 话题 : " + item.name);
                }
            }

            @Override
            public void onStart() {

            }
        });

        // 获取某个话题活跃的用户
        mCommSDK.fetchActiveUsers("541fe6f40bbbaf4f41f7aa3f", new FetchListener<UsersResponse>() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(UsersResponse response) {
                Log.e("", "### 某个话题的活跃用户 : ");
                for (CommUser user : response.result) {
                    Log.e("", "### 活跃用户 : " + user.name);
                }
            }
        });

        // 获取某用户的相册,也就是发布feed上传的所有图片
        mCommSDK.fetchAlbums(CommConfig.getConfig().loginedUser.id,
                new FetchListener<AlbumResponse>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(AlbumResponse response) {
                        Log.e("", "### response size : " + response.result.size());
                    }
                });

        // 搜索周边的feed
        mCommSDK.searchFeedNearby(116.3758540000f, 39.9856970000f,
                new FetchListener<FeedsResponse>() {

                    @Override
                    public void onComplete(FeedsResponse response) {
                        Log.e("", "### 周边的feed : " + response.result.size());
                    }

                    @Override
                    public void onStart() {

                    }

                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareSDKManager.getInstance().getCurrentSDK().onActivityResult(this,requestCode,resultCode,data);
    }
}
