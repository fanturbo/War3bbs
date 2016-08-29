package com.war3.comm.receiver;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.war3.comm.utils.SharedPreferencesUtils;
import com.war3.comm.utils.ToastUtil;


public class DownLoadBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("NewApi")
    public void onReceive(Context context, Intent intent) {
        long myDwonloadID = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String plato = SharedPreferencesUtils.getString(context, "plato", "");
        if ("".equals(plato)) {
            ToastUtil.show(context, "下载新版本失败，请稍后重试");
            return;
        }
        long refernece = Long.parseLong(plato);
        if (refernece == myDwonloadID) {
            try {
                String serviceString = Context.DOWNLOAD_SERVICE;
                DownloadManager dManager = (DownloadManager) context
                        .getSystemService(serviceString);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager
                        .getUriForDownloadedFile(myDwonloadID);
                install.setDataAndType(downloadFileUri,
                        "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } catch (Exception e) {
                e.printStackTrace();
               ToastUtil.show(context, "安装APP失败");
            }
        }
    }
}
