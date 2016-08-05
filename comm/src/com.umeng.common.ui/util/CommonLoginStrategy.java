package com.umeng.common.ui.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.login.AbsLoginResultStrategy;
import com.umeng.comm.core.nets.responses.LoginResponse;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.common.ui.activities.SettingActivity;

/**
 * Created by wangfei on 16/5/10.
 */
public class CommonLoginStrategy extends AbsLoginResultStrategy {

    @Override
    public void onLoginResult(Context context, CommUser user, LoginResponse response, int loginStyle) {
        boolean isUserNameInvalid = isUserNameInvalid(response);
        if (isUserNameInvalid && !(user.source == Source.WEIXIN || user.source == Source.SINA || user.source == Source.QQ)) {
            return;
        }
        // 第一次登录( 注册 )
        if (response.isFirstTimeLogin
                && response.errCode == ErrorCode.NO_ERROR) {
            AVObject testObject = new AVObject("War3er");
            testObject.put("uid", ContantsHelper.id);
            testObject.put("name", user.name);
            testObject.put("source", user.source == null ? "" : user.source.toString());
            testObject.put("secret", Constants.USER_PASSWORD);
            testObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Log.i("status", "save success");
                    } else {
                        Log.i("status", e.toString());
                    }
                }
            });
            gotoUpdateUserPage(context, user, isUserNameInvalid, loginStyle);
        }
    }

    /**
     * 跳转到修改用户信息页面</br>
     *
     * @param context
     * @param user
     * @param isUserNameInvalid
     */
    private void gotoUpdateUserPage(Context context, CommUser user, boolean isUserNameInvalid, int loginStyle) {
//        Class<?> settingClass = CommonUtils.getClassByName(context, "SettingActivity");
//        Class<?> settingClass = null;
//        Class<?> settingClassdiscuss = null;
//        Class<?> settingClasswb = null;
//        try {
//            settingClasswb = Class.forName("com.umeng.comm.ui.activities.SettingActivity");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//            try {
//                settingClassdiscuss = Class.forName("com.umeng.commm.ui.activities.SettingActivity");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        if (Constants.VERSION == 0){
//            if (settingClassdiscuss!=null){
//                settingClass = settingClassdiscuss;
//            }else {
//                settingClass = settingClasswb;
//            }
//        }else if (Constants.VERSION == 1){
//            settingClass =settingClassdiscuss;
//        }else if (Constants.VERSION == 2){
//            settingClass = settingClasswb;
//        }
//        Intent intent = new Intent(context, settingClass);
//        intent.putExtra(Constants.USER_SETTING, true);
//        intent.putExtra(Constants.REGISTER_USERNAME_INVALID, isUserNameInvalid);
//        intent.putExtra(Constants.USER, isUserNameInvalid ? user
//                : CommConfig.getConfig().loginedUser);
//        context.startActivity(intent);


        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra(Constants.USER_SETTING, true);
        intent.putExtra(Constants.REGISTER_USERNAME_INVALID, isUserNameInvalid);
        intent.putExtra(Constants.USER, isUserNameInvalid ? user
                : CommConfig.getConfig().loginedUser);
        intent.putExtra(Constants.LOGIN_STYLE, loginStyle);
        context.startActivity(intent);
    }

    protected boolean isUserNameInvalid(LoginResponse response) {
        return CommonUtils.checkUserNameStatus(response.errCode);
    }

}