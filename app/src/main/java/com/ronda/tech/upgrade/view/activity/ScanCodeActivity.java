package com.ronda.tech.upgrade.view.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.utils.LogUtils;

import java.util.List;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.EasyPermissions;

public class ScanCodeActivity extends BaseActivity implements QRCodeView.Delegate, View.OnClickListener ,EasyPermissions.PermissionCallbacks{

    private static final String TAG = "ScanCodeActivity";

    private ZXingView mZXingView;
    private LinearLayout llback;
    private String fromWho;

    @Override
    protected void init() {
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        BGAQRCodeUtil.setDebug(false);

        llback = findViewById(R.id.home_ll_back);
        llback.setVisibility(View.VISIBLE);
        llback.setOnClickListener(this);
        TextView tv = findViewById(R.id.home_tv_top_title);
        tv.setText("扫描设备");

        fromWho = getIntent().getStringExtra("from");
        EasyPermissions.requestPermissions(this,"启用摄像头用于二维码扫描",0, Manifest.permission.CAMERA);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }



    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("result",result);
        if (fromWho.equals("home")){
            setResult(RESULT_OK,intent);
        }else {
            startActivity(intent);
        }

        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case 0:
                LogUtils.d(TAG,"相机权限已授权");
                mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
                mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        switch (requestCode){
            case 0:
                LogUtils.e(TAG,"相机权限申请被拒绝了");
                EasyPermissions.requestPermissions(this,"请允许摄像头权限，仅用于二维码扫描",0, Manifest.permission.CAMERA);
                break;
        }
    }
}
