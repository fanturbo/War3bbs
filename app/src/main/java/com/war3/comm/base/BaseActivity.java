package com.war3.comm.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;


/**
 * Created by snail on 16/8/3.
 */
public class BaseActivity extends FragmentActivity {

    private String mPageName;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageName = this.getClass().getName();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this); // 统计时长
    }
}
