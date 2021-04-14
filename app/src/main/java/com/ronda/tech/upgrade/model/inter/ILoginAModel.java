package com.ronda.tech.upgrade.model.inter;

import com.ronda.tech.upgrade.presenter.callback.LoginCallback;

public interface ILoginAModel {
    void loginNow(String acount, String psw, LoginCallback callback);
}
