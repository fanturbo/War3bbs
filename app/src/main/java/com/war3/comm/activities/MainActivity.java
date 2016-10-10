
package com.war3.comm.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.umeng.comm.core.sdkmanager.ImageLoaderManager;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.commm.ui.fragments.CommunityMainFragment;
import com.umeng.common.ui.widgets.CommunityViewPager;
import com.war3.comm.R;
import com.war3.comm.base.BaseActivity;
import com.war3.comm.custom.UILImageLoader;
import com.war3.comm.utils.DialogHelp;
import com.war3.comm.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.Calendar;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useMyImageLoader();
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
        String[] mPermissionList = new String[]{Manifest.permission.CHANGE_CONFIGURATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_SETTINGS,Manifest.permission.VIBRATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE};
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(mPermissionList,100);
        }
        //检查更新
        if (!(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "").equals(SharedPreferencesUtils.getString(MainActivity.this,
                "dayofyear", "0"))) {
            SharedPreferencesUtils.saveString(MainActivity.this,
                    "dayofyear", Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "");
            checkUpdate();
        }
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
    public void checkUpdate() {
        AVQuery<AVObject> avQuery = new AVQuery<>("Update");
        AVQuery<AVObject> name = avQuery.limit(1);
        name.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                String versionCode = avObject.get("versionCode").toString();
                final String updateUrl = avObject.get("updateUrl").toString();
                final String updateString = avObject.get("updateString").toString();
                int currentVersionCode = getVersionCode(MainActivity.this);
                if (currentVersionCode < Integer.parseInt(versionCode)) {
                    DialogHelp.getConfirmDialog(MainActivity.this, updateString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (updateUrl != null) {
                                downLoadApk(MainActivity.this, updateUrl);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String str = "market://details?id=" + getPackageName();
                            Intent localIntent = new Intent("android.intent.action.VIEW");
                            localIntent.setData(Uri.parse(str));
                            startActivity(localIntent);
                        }
                    }).show();
                }
            }
        });

    }

    /*
     * 从服务器中下载APK
     */
    protected static void downLoadApk(Context context, final String mUpgradeUrl) {

        // Check if download manager is available:
        int state = context.getPackageManager().getApplicationEnabledSetting(
                "com.android.providers.downloads");
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {

            // Cannot download using download manager
            String packageName = "com.android.providers.downloads";
            try {
                // Open the specific App Info page:
                Intent settingIntent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settingIntent.setData(Uri.parse("package:" + packageName));
                context.startActivity(settingIntent);

            } catch (ActivityNotFoundException e) {
                // Open the generic Apps page:
                Intent settingIntent = new Intent(
                        android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                context.startActivity(settingIntent);
            }
        } else {
            DownloadManager dManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(mUpgradeUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            // 判断下权限
            File skRoot = Environment.getExternalStorageDirectory();
            String downloadPath = skRoot + "/Download";
            File fileDir = new File(downloadPath);
            if (Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                if (fileDir.exists() && fileDir.isDirectory()) {
                } else {
                    fileDir.mkdirs();
                }
            }
            request.setDestinationInExternalPublicDir(fileDir.getName(), "war3社区");
            request.setDescription("war3社区新版本下载");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            long refernece = dManager.enqueue(request);
            // 把当前下载的ID保存起来
            SharedPreferencesUtils.saveString(context, "plato", refernece + "");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareSDKManager.getInstance().getCurrentSDK().onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * 获取当前程序的版本号
     *
     * @return
     * @throws Exception
     */
    public int getVersionCode(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
