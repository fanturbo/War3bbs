package com.umeng.common.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.Source;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.constants.StringUtil;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.responses.SimpleResponse;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.colortheme.ColorQueque;
import com.umeng.common.ui.dialogs.CustomCommomDialog;
import com.umeng.community.login.*;
import com.umeng.message.proguard.T;
import com.umeng.socialize.Config;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangfei on 16/5/5.
 */
public class LoginSimplifyActivity extends BaseActivity implements View.OnClickListener {
    EditText nameEd;
    EditText secretEd;
    TextView forgetBtn;
    TextView loginBtn;
    TextView registerBtn;
    ImageView showBtn;
    ImageView mQQImageView;
    ImageView mWXImageView;
    ImageView mSinaImageView;
    public static LoginListener mLoginListener;
    private boolean isHidde = true;
    Dialog dialog;
    String uid = "";
    private UMShareAPI mShareAPI = null;

    public LoginSimplifyActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(ResFinder.getLayout("umeng_comm_default_login"));
        this.CheckThirdParty();
        this.nameEd = (EditText) this.findViewById(ResFinder.getId("umeng_login_num"));
        this.secretEd = (EditText) this.findViewById(ResFinder.getId("umeng_login_secret"));
        this.forgetBtn = (TextView) this.findViewById(ResFinder.getId("umeng_forget_secret"));
        this.loginBtn = (TextView) this.findViewById(ResFinder.getId("umeng_simplify_login"));
        this.registerBtn = (TextView) this.findViewById(ResFinder.getId("umeng_simplify_register"));
        this.showBtn = (ImageView) this.findViewById(ResFinder.getId("umeng_secret_style"));
        this.showBtn.setOnClickListener(this);
        this.forgetBtn.setOnClickListener(this);
        this.loginBtn.setOnClickListener(this);
        this.registerBtn.setOnClickListener(this);
        this.findViewById(ResFinder.getId("umeng_login_close")).setOnClickListener(this);
        this.dialog = new CustomDialog(this, ResFinder.getString("umeng_comm_text_waitting"));
        this.mSinaImageView = (ImageView) this.findViewById(ResContainer.getResourceId(this, "id", "sina_platform_btn"));
        this.mSinaImageView.setOnClickListener(this);
        this.mQQImageView = (ImageView) this.findViewById(ResContainer.getResourceId(this, "id", "qq_platform_btn"));
        this.mQQImageView.setOnClickListener(this);
        this.mQQImageView.setBackgroundResource(ResContainer.getResourceId(this, "drawable", "umeng_comm_qq_bt"));
        this.mWXImageView = (ImageView) this.findViewById(ResContainer.getResourceId(this, "id", "weixin_platform_btn"));
        this.mWXImageView.setImageResource(ResContainer.getResourceId(this, "drawable", "umeng_comm_wechat_bt"));
        this.mShareAPI = UMShareAPI.get(this);
        this.mWXImageView.setOnClickListener(this);
        Config.dialog = this.initProcessDialog();
        Config.dialogSwitch = true;
    }

    private Dialog initProcessDialog() {
        CustomDialog dialog = new CustomDialog(this, ResFinder.getString("umeng_socialize_text_waitting"));
        return dialog;
    }

    public void CheckThirdParty() {
        String classPath = "com.umeng.socialize.common.QueuedWork";
        Class clz = null;

        try {
            clz = Class.forName(classPath);
        } catch (ClassNotFoundException var4) {
            ;
        } catch (IllegalArgumentException var5) {
            ;
        }

        if (clz != null) {
            this.findViewById(ResFinder.getId("layout5")).setVisibility(View.VISIBLE);
            this.findViewById(ResFinder.getId("layout4")).setVisibility(View.VISIBLE);
        }

    }

    public void onClick(View v) {
        String intent;
        if (v.getId() == ResFinder.getId("umeng_forget_secret")) {
            intent = this.nameEd.getText().toString();
            if (TextUtils.isEmpty(intent)) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_empty");
                return;
            }

            if (!intent.contains("@")) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_illuid");
                return;
            }

            CommunitySDKImpl.getInstance().forgetPWD(intent, new Listeners.FetchListener<SimpleResponse>() {
                public void onStart() {
                    LoginSimplifyActivity.this.dialog.show();
                }

                @Override
                public void onComplete(SimpleResponse response) {
                    LoginSimplifyActivity.this.dialog.dismiss();
                    Log.e("xxxxxx", "errorcode=" + response.errCode);
                    if (response.errCode == 0) {
                        ToastMsg.showShortMsgByResName("umeng_comm_forget_success");
                    } else if (response.errCode == 10002) {
                        ToastMsg.showShortMsgByResName("umeng_comm_name_lost");
                    } else {
                        ToastMsg.showShortMsgByResName("umeng_comm_http_req_failed");
                    }

                }
            });
        } else if (v.getId() == ResFinder.getId("umeng_simplify_login")) {
            intent = this.nameEd.getText().toString();
            String secret = this.secretEd.getText().toString();
            if (TextUtils.isEmpty(intent)) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_empty");
                return;
            }

            if (!intent.contains("@")) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_illuid");
                return;
            }

            if (TextUtils.isEmpty(secret)) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_secret_empty");
                return;
            }

            if (!StringUtil.isWordAndNum(secret) || secret.length() < 6 || secret.length() > 18) {
                ToastMsg.showShortMsgByResName("umeng_comm_login_illpassword");
                return;
            }

            CommUser user = new CommUser();
            user.id = intent;
            CommunitySDKImpl.getInstance().loginToWsq(this, user, new LoginListener() {
                public void onStart() {
                    LoginSimplifyActivity.this.dialog.show();
                }

                public void onComplete(int stCode, CommUser userInfo) {
                    LoginSimplifyActivity.this.dialog.dismiss();
                    if (stCode == 0) {
                        LoginSimplifyActivity.mLoginListener.onComplete(stCode, userInfo);
                        LoginSimplifyActivity.this.finish();
                    }

                }
            }, secret);
        } else if (v.getId() == ResFinder.getId("umeng_simplify_register")) {
            Intent intent1 = new Intent(this, RegisterActivity.class);
            RegisterActivity.mRegisterListener = mLoginListener;
            intent1.setFlags(268435456);
            this.startActivity(intent1);
            this.finish();
        } else if (v.getId() == ResFinder.getId("umeng_login_close")) {
            if (this.getCurrentFocus() != null) {
                ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 2);
            }

            this.finish();
        } else if (v.getId() == ResFinder.getId("umeng_secret_style")) {
            if (this.isHidde) {
                this.showBtn.setImageDrawable(ResFinder.getDrawable("umeng_com_showpassword"));
                this.secretEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                this.isHidde = false;
            } else {
                this.showBtn.setImageDrawable(ResFinder.getDrawable("umeng_comm_hidepassword"));
                this.secretEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.isHidde = true;
            }
        } else if (v.getId() == ResContainer.getResourceId(this, "id", "sina_platform_btn")) {
            Toast.makeText(this, "暂时不支持新浪微博", Toast.LENGTH_SHORT).show();
//            this.login(this, SHARE_MEDIA.SINA);
        } else if (v.getId() == ResContainer.getResourceId(this, "id", "qq_platform_btn")) {
            this.login(this, SHARE_MEDIA.QQ);
        } else if (v.getId() == ResContainer.getResourceId(this, "id", "weixin_platform_btn")) {
            this.login(this, SHARE_MEDIA.WEIXIN);
        }

    }

    private void login(final Context context, final SHARE_MEDIA platform) {
        this.mShareAPI.doOauthVerify(this, platform, new UMAuthListener() {
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                LoginSimplifyActivity.this.uid = (String) map.get("uid");
                if (TextUtils.isEmpty(LoginSimplifyActivity.this.uid)) {
                    LoginSimplifyActivity.this.uid = (String) map.get("openid");
                }

                if (share_media == SHARE_MEDIA.WEIXIN) {
                    Constants.WX_UID = (String) map.get("unionid");
                }

                LoginSimplifyActivity.this.fetchLoginedUserInfo(context, platform, LoginSimplifyActivity.mLoginListener);
            }

            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                if (LoginSimplifyActivity.mLoginListener != null) {
                    Bundle data = new Bundle();
                    data.putString("msg", throwable.getMessage());
                    LoginSimplifyActivity.mLoginListener.onComplete(0, new CommUser());
                }

            }

            public void onCancel(SHARE_MEDIA share_media, int i) {
                if (LoginSimplifyActivity.mLoginListener != null) {
                    LoginSimplifyActivity.mLoginListener.onComplete(1, new CommUser());
                }

            }
        });
    }

    private void fetchLoginedUserInfo(final Context context, final SHARE_MEDIA platform, final LoginListener listener) {
        final CustomDialog progressDialog = new CustomDialog(this, ResFinder.getString("umeng_socialize_load_userinfo"));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOwnerActivity(this);
        SocializeUtils.safeShowDialog(progressDialog);
        this.mShareAPI.getPlatformInfo((Activity) context, platform, new UMAuthListener() {
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                com.umeng.socialize.utils.Log.d("login", "logged in");
                LoginSimplifyActivity.this.showMapInfo(map);
                SocializeUtils.safeCloseDialog(progressDialog);
                LoginSimplifyActivity.this.finish();
                if (listener != null) {
                    com.umeng.socialize.utils.Log.d("login", "logged in");
                    CommUser user = LoginSimplifyActivity.this.createNewUser(map, platform);
                    listener.onComplete(200, user);
                }

            }

            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                String msg = ResContainer.getString(context, "umeng_socialize_fetch_info_failed");
                Toast.makeText(context, msg + " : " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                SocializeUtils.safeCloseDialog(progressDialog);
            }

            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d("loggin", "log in cancel");
            }
        });
    }

    private CommUser createNewUser(Map<String, String> info, SHARE_MEDIA platform) {
        com.umeng.socialize.utils.Log.d("userinfo", info.toString());
        CommUser currentUser = new CommUser();
        if (info != null && info.size() != 0) {
            currentUser.id = String.valueOf(info.get("uid"));
            if (platform == SHARE_MEDIA.SINA) {
                currentUser.source = Source.SINA;
                currentUser.iconUrl = this.getString(info, "profile_image_url");
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                currentUser.source = Source.WEIXIN;
                currentUser.id = this.uid;
                currentUser.iconUrl = this.getString(info, "headimgurl");
            } else if (platform == SHARE_MEDIA.QQ) {
                currentUser.source = Source.QQ;
                currentUser.id = this.uid;
                currentUser.iconUrl = this.getString(info, "profile_image_url");
            }

            Log.e("xxxxxx", "info = " + info);
            Log.d("", "### login source : " + currentUser.source == null ? "selfAccount" : currentUser.source.toString());
            currentUser.name = this.getString(info, "screen_name");
            if (TextUtils.isEmpty(currentUser.name)) {
                currentUser.name = this.getString(info, "nickname");
            }

            if (TextUtils.isEmpty(currentUser.name)) {
                currentUser.name = this.getString(info, "name");
            }

            Log.e("xxxxxxxx", "info=" + info);
            currentUser.gender = this.getGender(info);
            return currentUser;
        } else {
            return currentUser;
        }
    }

    private void showMapInfo(Map<String, String> info) {
        Set entries = info.entrySet();
        Iterator var3 = entries.iterator();

        while (var3.hasNext()) {
            Map.Entry entry = (Map.Entry) var3.next();
            Log.d("", "###" + (String) entry.getKey() + "=" + (String) entry.getValue());
        }

    }

    private String getString(Map<String, String> info, String key) {
        return info.containsKey(key) ? (String) info.get(key) : "";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private CommUser.Gender getGender(Map<String, String> info) {
        if (!info.containsKey("sex") && !info.containsKey("gender")) {
            return CommUser.Gender.MALE;
        } else {
            String gender = null;
            if (info.containsKey("sex")) {
                gender = ((String) info.get("sex")).toString();
            } else {
                gender = ((String) info.get("gender")).toString();
            }

            return !gender.equals("1") && !"男".equals(gender) ? (!gender.equals("0") && !"女".equals(gender) ? CommUser.Gender.MALE : CommUser.Gender.FEMALE) : CommUser.Gender.MALE;
        }
    }

    protected void onDestroy() {
        mLoginListener = null;
        super.onDestroy();
    }
}
