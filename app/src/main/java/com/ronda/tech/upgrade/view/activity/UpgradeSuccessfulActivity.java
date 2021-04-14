package com.ronda.tech.upgrade.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.presenter.impl.UpgradeSuccessfulAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeSuccessfulAPresenter;
import com.ronda.tech.upgrade.view.inter.IUpgradeSuccessfulAView;

public class UpgradeSuccessfulActivity extends BaseActivity implements IUpgradeSuccessfulAView, View.OnClickListener {


    private static final String TAG = "UpgradeSuccessfulActivi";
    private IUpgradeSuccessfulAPresenter mIUpgradeSuccessfulAPresenter;
    private Context mContext;
    private Button btnBack,btnScanNewDevice;


    @Override
    protected void init() {
        mContext = this;
        initView();
        mIUpgradeSuccessfulAPresenter = new UpgradeSuccessfulAPresenterImpl(this);

    }

    private void initView() {
        TextView tv = findViewById(R.id.home_tv_top_title);
        tv.setText("升级成功");
        btnBack = findViewById(R.id.upgrade_success_btn_back);
        btnBack.setOnClickListener(this);
        btnScanNewDevice = findViewById(R.id.upgrade_success_btn_scan_new);
        btnScanNewDevice.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_upgrade_successful;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upgrade_success_btn_back:
                Intent orderIntent = new Intent("com.dfu.successful.flag");
                sendBroadcast(orderIntent);
                finish();
                break;
            case R.id.upgrade_success_btn_scan_new:
                Intent intent = new Intent(mContext,ScanCodeActivity.class);
                intent.putExtra("from","upgrade");
                startActivity(intent);
                finish();
                break;
        }
    }
}
