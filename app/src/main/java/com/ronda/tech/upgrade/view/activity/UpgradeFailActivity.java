package com.ronda.tech.upgrade.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.presenter.impl.UpgradeFailAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeFailAPresenter;
import com.ronda.tech.upgrade.view.inter.IUpgradeFailAView;

public class UpgradeFailActivity extends BaseActivity implements IUpgradeFailAView, View.OnClickListener {

    private static final String TAG = "UpgradeFailActivity";
    private IUpgradeFailAPresenter mIUpgradeFailAPresenter;
    private Context mContext;
    private Button btnRetry,btnBack,btnScanNewDevice;

    @Override
    protected void init() {
        mContext = this;
        initView();
        mIUpgradeFailAPresenter = new UpgradeFailAPresenterImpl(this);
    }

    private void initView() {
        TextView tv = findViewById(R.id.home_tv_top_title);
        tv.setText("升级失败");
        btnBack = findViewById(R.id.upgrade_fail_btn_back);
        btnBack.setOnClickListener(this);
        btnRetry = findViewById(R.id.upgrade_fail_btn_retry);
        btnRetry.setOnClickListener(this);
        btnScanNewDevice = findViewById(R.id.upgrade_fail_btn_scan_new_device);
        btnScanNewDevice.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_upgrade_fail;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upgrade_fail_btn_back:
                finish();
                break;
            case R.id.upgrade_fail_btn_retry:

                break;
            case R.id.upgrade_fail_btn_scan_new_device:
                Intent intent = new Intent(mContext,ScanCodeActivity.class);
                intent.putExtra("from","upgrade");
                startActivity(intent);
                finish();
                break;
        }
    }
}
