package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.UpgradeAModelImpl;
import com.ronda.tech.upgrade.model.inter.IUpgradeAModel;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeAPresenter;
import com.ronda.tech.upgrade.view.inter.IUpgradeAView;

public class UpgradeAPresenterImpl extends BaseImpl implements IUpgradeAPresenter {
    private IUpgradeAView mIUpgradeAView;
    private IUpgradeAModel mIUpgradeAModel;

    public UpgradeAPresenterImpl(IUpgradeAView aIUpgradeAView) {
        mIUpgradeAView = aIUpgradeAView;
        mIUpgradeAModel = new UpgradeAModelImpl();
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {

    }

    @Override
    protected void onRequestFailure(String e) {

    }
}
