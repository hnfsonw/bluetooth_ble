package com.ronda.tech.upgrade.model.inter;

import com.ronda.tech.upgrade.presenter.callback.CheckCodeCallback;
import com.ronda.tech.upgrade.presenter.callback.ForgetPswCallback;

public interface IForgetPasswoldAModel {
    void getCheckCode(String phone, CheckCodeCallback callback);
    void summit(String phone, String checkCode, String psw, ForgetPswCallback callback);
}
