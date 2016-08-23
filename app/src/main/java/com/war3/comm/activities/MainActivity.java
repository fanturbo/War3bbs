
package com.war3.comm.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioGroup;

import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.impl.CommunityFactory;

import com.umeng.comm.core.sdkmanager.LoginSDKManager;

import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.commm.ui.fragments.CommunityMainFragment;

import com.umeng.common.ui.presenter.impl.LoginSimplify;
import com.umeng.common.ui.util.CommonLoginStrategy;
import com.umeng.common.ui.widgets.CommunityViewPager;
import com.war3.comm.R;
import com.war3.comm.base.BaseActivity;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;


public class MainActivity extends BaseActivity {

    CommunitySDK mCommSDK = null;
    String topicId = "";
    private RadioGroup rg; // 主页下面RadioGroup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setCatchUncaughtExceptions(false);
        XiaomiUpdateAgent.update(this);
        // 1、初始化友盟微社区
        mCommSDK = CommunityFactory.getCommSDK(this);
        useCustomLogin();
        CommunityViewPager viewPager = (CommunityViewPager) findViewById(R.id.viewPager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(
                getSupportFragmentManager()) {


            @Override
            public int getCount() {
                return 1;
            }

            public SparseArray<Fragment> caches = new SparseArray<Fragment>();

            @Override
            public Fragment getItem(int arg0) {
                Fragment f = caches.get(arg0);
                if (f == null) {
                    switch (arg0) {
                        case 0:
                            f = new CommunityMainFragment();
                            ((CommunityMainFragment) f).setBackButtonVisibility(View.GONE);
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
      /*  String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE};
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(mPermissionList,100);
        }*/
    }

    protected void useCustomLogin() {
        // 管理器
        LoginSDKManager.getInstance().addAndUse(new LoginSimplify());
        CommConfig.getConfig().setLoginResultStrategy(new CommonLoginStrategy());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareSDKManager.getInstance().getCurrentSDK().onActivityResult(this, requestCode, resultCode, data);
    }
}
