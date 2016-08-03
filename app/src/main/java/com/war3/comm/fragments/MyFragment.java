package com.war3.comm.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.Constants;
import com.umeng.commm.ui.activities.FindActivity;
import com.war3.comm.R;

public class MyFragment extends Fragment {

    private String mContainerClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mContainerClass = getActivity().getClass().getName();
//        gotoFindActivity(CommConfig.getConfig().loginedUser);
        //TODO 将FindActivity中的东西移到这儿来
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
    /**
     * 跳转到发现Activity</br>
     *
     * @param user
     */
    public void gotoFindActivity(CommUser user) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        Intent intent = new Intent(getActivity(), FindActivity.class);
        if (user == null) {// 来自开发者外部调用的情况
            intent.putExtra(Constants.TAG_USER, CommConfig.getConfig().loginedUser);
        } else {
            intent.putExtra(Constants.TAG_USER, user);
        }
        intent.putExtra(Constants.TYPE_CLASS, mContainerClass);
        getActivity().startActivity(intent);
    }
}
