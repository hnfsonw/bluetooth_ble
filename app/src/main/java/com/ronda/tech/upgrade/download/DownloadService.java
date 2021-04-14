package com.ronda.tech.upgrade.download;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;

import androidx.annotation.Nullable;

import com.ronda.tech.upgrade.base.MyApplication;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;
import com.ronda.tech.upgrade.view.activity.HomeActivity;

import java.io.File;

public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";
    private String mDownloadFileName;

    public DownloadService() {
        super("DownLoadService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUrl = intent.getExtras().getString("file_url");
        mDownloadFileName = intent.getExtras().getString("file_name");

        LogUtils.d(TAG, "download_url --" + downloadUrl);
        LogUtils.d(TAG, "download_file --" + mDownloadFileName);

        final File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR + mDownloadFileName);
        int range = (int) SharedPreferencesUtils.getParam(MyApplication.getContext(),"range",0);

        if (file.exists()) {
//            if (range == file.length()){
//                LogUtils.d(TAG,"文件下载完成");
//            }else {
//                startDownload(file.length(),mDownloadFileName,downloadUrl);
//            }
            //后台接口没有做断点续传功能，文件也比较小
            file.delete();
            startDownload(0,mDownloadFileName,downloadUrl);
        }else {
            startDownload(0,mDownloadFileName,downloadUrl);
        }
    }

    private void startDownload(long range, String mDownloadFileName, String downloadUrl) {
        RetrofitHttp.getInstance().downloadFile(mDownloadFileName,Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR ,downloadUrl, range, new DownloadCallBack() {
            @Override
            public void onProgress(int progress) {
                Message message = new Message();
                message.what = 0x111;
                message.arg1 = progress;
                HomeActivity.getNetRequestHandler().sendMessage(message);
            }

            @Override
            public void onCompleted(String md5) {
                //后台接口没做md5校验接口
                LogUtils.d(TAG, "下载完成");
            }

            @Override
            public void onError(String msg) {
                LogUtils.e(TAG, "下载发生错误--" + msg);
            }
        });
    }
}
