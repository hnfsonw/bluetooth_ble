package com.ronda.tech.upgrade.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.model.inter.IProgressListener;
import com.ronda.tech.upgrade.utils.DfuUtils;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.view.activity.UpgradeActivity;
import com.ronda.tech.upgrade.view.activity.UpgradeFailActivity;
import com.ronda.tech.upgrade.view.activity.UpgradeSuccessfulActivity;

import no.nordicsemi.android.dfu.DfuProgressListener;

public class MyAsyncTask extends AsyncTask<String,Integer,String> implements DfuProgressListener {
    private static final String TAG = "MyAsyncTask";
    private ProgressBar bar;
    private Context mContext;
    private int progress;

    public MyAsyncTask(Context context, ProgressBar bar){
        this.bar = bar;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setProgress(0);
    }

    @Override
    protected String doInBackground(String... strings) {
        String parm1 = strings[0];
        String parm2 = strings[1];
        LogUtils.e(TAG,"parm1:"+parm1+"  parm2:"+parm2);
        initDfu(parm1,parm2);
        while (progress != 100){
            publishProgress(progress);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bar.setProgress(values[0],true);
        }else {
            bar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    private void initDfu(String mac, String fileName) {
        DfuUtils.getInstance().setmDfuProgressListener(mContext,this);//升级状态回调
        DfuUtils.getInstance().startUpdate(mContext,mac,"DfuTarg", Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR + fileName);
    }

    @Override
    public void onDeviceConnecting(@NonNull String s) {
        LogUtils.e(TAG,"onDeviceConnecting:"+s);
    }

    @Override
    public void onDeviceConnected(@NonNull String s) {
        LogUtils.e(TAG,"onDeviceConnected:"+s);
    }

    @Override
    public void onDfuProcessStarting(@NonNull String s) {
        LogUtils.e(TAG,"onDfuProcessStarting:"+s);
    }

    @Override
    public void onDfuProcessStarted(@NonNull String s) {
        LogUtils.e(TAG,"onDfuProcessStarted:"+s);
    }

    @Override
    public void onEnablingDfuMode(@NonNull String s) {
        LogUtils.e(TAG,"onEnablingDfuMode:"+s);
    }

    @Override
    public void onProgressChanged(@NonNull String s, int i, float v, float v1, int i1, int i2) {
        LogUtils.e(TAG,"onProgressChanged:"+s+" i:"+i+" v:"+v+" v1:"+v1+" i1:"+i1+" i2:"+i2);
        progress = i;
    }

    @Override
    public void onFirmwareValidating(@NonNull String s) {
        LogUtils.e(TAG,"onFirmwareValidating:"+s);
    }

    @Override
    public void onDeviceDisconnecting(String s) {
        LogUtils.e(TAG,"onDeviceDisconnecting:"+s);
    }

    @Override
    public void onDeviceDisconnected(@NonNull String s) {
        LogUtils.e(TAG,"onDeviceDisconnected:"+s);
    }

    @Override
    public void onDfuCompleted(@NonNull String s) {
        LogUtils.e(TAG,"onDfuCompleted:"+s);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(mContext, UpgradeSuccessfulActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onDfuAborted(@NonNull String s) {
        LogUtils.e(TAG,"onDfuAborted:"+s);
    }

    @Override
    public void onError(@NonNull String s, int i, int i1, String s1) {
        LogUtils.e(TAG,"onError:"+s);
        Intent intent = new Intent(mContext, UpgradeFailActivity.class);
        mContext.startActivity(intent);
    }
}
