package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.ScanCodeAModelImpl;
import com.ronda.tech.upgrade.model.inter.IScanCodeAModel;
import com.ronda.tech.upgrade.presenter.inter.IScanCodeAPresenter;
import com.ronda.tech.upgrade.view.inter.IScanCodeAView;

public class ScanCodeAPresenterImpl extends BaseImpl implements IScanCodeAPresenter {
    private IScanCodeAView mIScanCodeAView;
    private IScanCodeAModel mIScanCodeAModel;

    public ScanCodeAPresenterImpl(IScanCodeAView aIScanCodeAView) {
        mIScanCodeAView = aIScanCodeAView;
        mIScanCodeAModel = new ScanCodeAModelImpl();
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {

    }

    @Override
    protected void onRequestFailure(String e) {

    }
}
