package com.ronda.tech.upgrade.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.base.BaseActivity;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.presenter.impl.LoginAPresenterImpl;
import com.ronda.tech.upgrade.presenter.inter.ILoginAPresenter;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;
import com.ronda.tech.upgrade.view.inter.ILoginAView;

public class LoginActivity extends BaseActivity implements ILoginAView, View.OnClickListener {

    private ILoginAPresenter mILoginAPresenter;
    private EditText etAcount;
    private EditText etPassWorld;
    private CheckBox cbRemeberPsw;
    private TextView tvForgetPsw;
    private Button btnLogin;
    private Context mContext;

    @Override
    protected void init() {
        mContext = this;
        mILoginAPresenter = new LoginAPresenterImpl(this);
        initView();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_login;
    }

    private void initView() {
        etAcount = findViewById(R.id.login_etv_aount);
        etPassWorld = findViewById(R.id.login_etv_psw);
        cbRemeberPsw = findViewById(R.id.login_cb_remaber);
        cbRemeberPsw.setOnClickListener(this);
        tvForgetPsw = findViewById(R.id.login_tv_forget_psw);
        tvForgetPsw.setOnClickListener(this);
        btnLogin = findViewById(R.id.login_btn_login);
        btnLogin.setOnClickListener(this);

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null){
            boolean isExit = intent.getBooleanExtra("isExit",false);
            if (isExit){
                this.finish();
            }
        }
    }

    private void initData() {
        boolean isRememberPsw = (boolean) SharedPreferencesUtils.getParam(this,Constants.CHECKBOX_STATE,false);
        cbRemeberPsw.setChecked(isRememberPsw);
        if (isRememberPsw){
            etAcount.setText(SharedPreferencesUtils.getParam(this,Constants.REMEMBER_ACOUNT,"").toString());
            etPassWorld.setText(SharedPreferencesUtils.getParam(this,Constants.REMEMBER_PASSWORLD,"").toString());
            showLoading("正在登录……",this);
            acountCheck();
        }else {
            boolean isLogin = (boolean) SharedPreferencesUtils.getParam(this, Constants.LOGIN_SUCCESS_LASTTIME,false);
            if (isLogin&&isRememberPsw){
                toHomeActivity();
            }
        }
    }


    @Override
    public String getloginInfo() {
        return etAcount.getText().toString()+":"+etPassWorld.getText().toString();
    }

    @Override
    public void loginSuccessful() {
        dismissLoading();
        SharedPreferencesUtils.setParam(this,Constants.LOGIN_SUCCESS_LASTTIME,true);
        toHomeActivity();
    }

    @Override
    public void loginFail(String failMsg) {
        dismissLoading();
        showToast(failMsg);
    }

    /**
     * 跳转主页
     */
    private void toHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_cb_remaber:
                SharedPreferencesUtils.setParam(mContext,Constants.CHECKBOX_STATE,cbRemeberPsw.isChecked());
                SharedPreferencesUtils.setParam(mContext,Constants.REMEMBER_ACOUNT,etAcount.getText().toString());
                SharedPreferencesUtils.setParam(mContext,Constants.REMEMBER_PASSWORLD,etPassWorld.getText().toString());
                break;
            case R.id.login_tv_forget_psw:
                Intent intent = new Intent(mContext,ForgetPasswoldActivity.class);
                startActivity(intent);
                break;
            case R.id.login_btn_login:
                showLoading(mContext.getString(R.string.login_loading),mContext);
                acountCheck();
                break;
                default:
                    break;
        }
    }

    private void acountCheck() {
        if (TextUtils.isEmpty(etAcount.getText()) || TextUtils.isEmpty(etPassWorld.getText())) {
            loginFail(mContext.getString(R.string.input_complete));
        }else {
            mILoginAPresenter.login();
        }
    }
}
