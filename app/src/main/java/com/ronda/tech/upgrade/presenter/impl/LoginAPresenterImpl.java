package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.LoginAModelImpl;
import com.ronda.tech.upgrade.model.inter.ILoginAModel;
import com.ronda.tech.upgrade.presenter.inter.ILoginAPresenter;
import com.ronda.tech.upgrade.presenter.callback.LoginCallback;
import com.ronda.tech.upgrade.view.inter.ILoginAView;

public class LoginAPresenterImpl implements ILoginAPresenter {
    private ILoginAView mILoginAView;
    private ILoginAModel mILoginAModel;

    public LoginAPresenterImpl(ILoginAView aILoginAView) {
        mILoginAView = aILoginAView;
        mILoginAModel = new LoginAModelImpl();
    }

    @Override
    public void login() {
        String[] loginInfoArr = mILoginAView.getloginInfo().split(":");
        mILoginAModel.loginNow(loginInfoArr[0], loginInfoArr[1], new LoginCallback() {
            @Override
            public void loginSuccess() {
                mILoginAView.loginSuccessful();
            }

            @Override
            public void loginFail(String reson) {
                mILoginAView.loginFail(reson);
            }
        });
    }
}
