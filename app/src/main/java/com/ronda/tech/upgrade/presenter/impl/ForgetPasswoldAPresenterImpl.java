package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.ForgetPasswoldAModelImpl;
import com.ronda.tech.upgrade.model.inter.IForgetPasswoldAModel;
import com.ronda.tech.upgrade.presenter.callback.CheckCodeCallback;
import com.ronda.tech.upgrade.presenter.callback.ForgetPswCallback;
import com.ronda.tech.upgrade.presenter.inter.IForgetPasswoldAPresenter;
import com.ronda.tech.upgrade.view.inter.IForgetPasswoldAView;

public class ForgetPasswoldAPresenterImpl implements IForgetPasswoldAPresenter {
    private IForgetPasswoldAView mIForgetPasswoldAView;
    private IForgetPasswoldAModel mIForgetPasswoldAModel;

    public ForgetPasswoldAPresenterImpl(IForgetPasswoldAView aIForgetPasswoldAView) {
        mIForgetPasswoldAView = aIForgetPasswoldAView;
        mIForgetPasswoldAModel = new ForgetPasswoldAModelImpl();
    }

    @Override
    public void getCheckCode() {
        String phone = mIForgetPasswoldAView.getInputInfo().split(":")[0];
        mIForgetPasswoldAModel.getCheckCode(phone, new CheckCodeCallback() {
            @Override
            public void getCheckCodeSuccess() {
                mIForgetPasswoldAView.getCheckCodeSuccessful();
            }

            @Override
            public void getCheckCodeFail(String reson) {
                mIForgetPasswoldAView.getcheckCodeFail(reson);
            }
        });
    }

    @Override
    public void summit() {
        String infoArr[] = mIForgetPasswoldAView.getInputInfo().split(":");
        mIForgetPasswoldAModel.summit(infoArr[0], infoArr[1],infoArr[2], new ForgetPswCallback() {
            @Override
            public void summitSuccess() {
                mIForgetPasswoldAView.summitSuccessful();
            }

            @Override
            public void summitFail(String reson) {
                mIForgetPasswoldAView.summitFail(reson);
            }
        });
    }
}
