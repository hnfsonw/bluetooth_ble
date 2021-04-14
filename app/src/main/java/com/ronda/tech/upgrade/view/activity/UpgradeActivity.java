package com.ronda.tech.upgrade.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.model.MyAsyncTask;
import com.ronda.tech.upgrade.model.inter.IProgressListener;
import com.ronda.tech.upgrade.presenter.impl.UpgradeAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeAPresenter;
import com.ronda.tech.upgrade.utils.DfuUtils;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.view.inter.IUpgradeAView;

import no.nordicsemi.android.dfu.DfuProgressListener;

public class UpgradeActivity extends BaseActivity implements IUpgradeAView,DfuProgressListener {
    private static final String TAG = "UpgradeActivity";
    private IUpgradeAPresenter mIUpgradeAPresenter;

    private ProgressBar progressBar;
    private Button btnCancle;

    private LinearLayout llBack;
    private MyAsyncTask myAsyncTask;
    private String currentDeviceMac;
    private String fileName;
    private IProgressListener progressListener;

    public void setProgressListener(IProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected void init() {
        currentDeviceMac = getIntent().getStringExtra("deviceMac");
        fileName = getIntent().getStringExtra("fileName");
        initView();
        mIUpgradeAPresenter = new UpgradeAPresenterImpl(this);
        myAsyncTask = new MyAsyncTask(this,progressBar);
        myAsyncTask.execute(currentDeviceMac,fileName);
    }

    private void initView() {
        progressBar = findViewById(R.id.upgrade_progress_Bar);
        btnCancle = findViewById(R.id.upgrade_btn_cacle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAsyncTask.cancel(true);
                finish();
            }
        });

        llBack = findViewById(R.id.home_ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv = findViewById(R.id.home_tv_top_title);
        tv.setText("升 级");
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_upgrade;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (myAsyncTask != null){
            myAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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
    }

    @Override
    public void onDfuAborted(@NonNull String s) {
        LogUtils.e(TAG,"onDfuAborted:"+s);
    }

    @Override
    public void onError(@NonNull String s, int i, int i1, String s1) {
        LogUtils.e(TAG,"onError:"+s);
    }
}
