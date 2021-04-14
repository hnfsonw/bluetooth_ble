package com.ronda.tech.upgrade.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.presenter.impl.ForgetPasswoldAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.IForgetPasswoldAPresenter;
import com.ronda.tech.upgrade.utils.ExecutorService;
import com.ronda.tech.upgrade.view.inter.IForgetPasswoldAView;

public class ForgetPasswoldActivity extends BaseActivity implements IForgetPasswoldAView, View.OnClickListener {

    private IForgetPasswoldAPresenter mIForgetPasswoldAPresenter;
    private EditText etAcount,etCheckCode,etNewPsw,etNewPswSure;
    private Button btnCheckCode,btnCommit;
    private LinearLayout llBack;
    private TextView tvTitle;
    private Context mContext;
    private MyHandler myHandler;

    @Override
    protected void init() {
        mContext = this;
        initView();
        mIForgetPasswoldAPresenter = new ForgetPasswoldAPresenterImpl(this);
        myHandler = new MyHandler();
    }

    private void initView() {
        etAcount = findViewById(R.id.forget_psw_etv_acount);
        etCheckCode = findViewById(R.id.forget_psw_etv_psw);
        etNewPsw = findViewById(R.id.reset_psw_etv_pswone);
        etNewPswSure = findViewById(R.id.reset_psw_etv__pswtwo);
        llBack = findViewById(R.id.home_ll_back);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvTitle = findViewById(R.id.home_tv_top_title);
        tvTitle.setText("忘记密码");
        btnCheckCode = findViewById(R.id.forget_psw_btn_check_code);
        btnCheckCode.setOnClickListener(this);
        btnCommit = findViewById(R.id.forget_psw_btn_commit);
        btnCommit.setOnClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_forget_passwold;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_ll_back:
                finish();
                break;
            case R.id.forget_psw_btn_check_code:
                if (TextUtils.isEmpty(etAcount.getText().toString())||etAcount.getText().toString().length() != 11){
                    showToast("请输入正确的手机号码");
                    return;
                }
                showLoading("",mContext);
                mIForgetPasswoldAPresenter.getCheckCode();
                break;
            case R.id.forget_psw_btn_commit:
                if (TextUtils.isEmpty(etAcount.getText().toString())||etAcount.getText().toString().length() != 11){
                    showToast("请输入正确的手机号码");
                    return;
                }

                if (TextUtils.isEmpty(etCheckCode.getText().toString())){
                    showToast("请输入验证码");
                    return;
                }

                if (TextUtils.isEmpty(etNewPsw.getText().toString()) || TextUtils.isEmpty(etNewPswSure.getText().toString())){
                    showToast("请设置新密码");
                }

                if (!etNewPsw.getText().toString().equals(etNewPswSure.getText().toString())){
                    showToast("两次输入的密码不一致");
                    return;
                }

                showLoading("",mContext);
                mIForgetPasswoldAPresenter.summit();
                break;
        }
    }

    @Override
    public String getInputInfo() {
        return etAcount.getText().toString()+":"+etCheckCode.getText().toString()+":"+etNewPswSure.getText().toString();
    }

    @Override
    public void getCheckCodeSuccessful() {
        dismissLoading();
        btnCheckCode.setClickable(false);
        //验证码获取成功后，开始倒计时
        ExecutorService.createExecutorService(1).execute(new Runnable() {
            @Override
            public void run() {
                for (int i=60;i>=0;i--){
                    Message message = myHandler.obtainMessage();
                    message.what = 0x11;
                    message.arg1 = i;
                    myHandler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void getcheckCodeFail(String failMsg) {
        dismissLoading();
        showToast("获取验证码失败:"+failMsg);
    }

    @Override
    public void summitSuccessful() {
        dismissLoading();
        showToast("重置成功");
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void summitFail(String msg) {
        dismissLoading();
        showToast("提交失败:"+msg);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0x11:
                    if (msg.arg1 == 0){
                        btnCheckCode.setText("获取验证码");
                        btnCheckCode.setClickable(true);
                    }else {
                        btnCheckCode.setText(msg.arg1+"s");
                    }
                    break;
            }
        }
    }
}
