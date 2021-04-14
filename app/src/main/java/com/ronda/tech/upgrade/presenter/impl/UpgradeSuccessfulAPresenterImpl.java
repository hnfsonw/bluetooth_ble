package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.UpgradeSuccessfulAModelImpl;
import com.ronda.tech.upgrade.model.inter.IUpgradeSuccessfulAModel;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeSuccessfulAPresenter;
import com.ronda.tech.upgrade.view.inter.IUpgradeSuccessfulAView;

public class UpgradeSuccessfulAPresenterImpl extends BaseImpl implements IUpgradeSuccessfulAPresenter {
    private IUpgradeSuccessfulAView mIUpgradeSuccessfulAView;
    private IUpgradeSuccessfulAModel mIUpgradeSuccessfulAModel;

    public UpgradeSuccessfulAPresenterImpl(IUpgradeSuccessfulAView aIUpgradeSuccessfulAView) {
        mIUpgradeSuccessfulAView = aIUpgradeSuccessfulAView;
        mIUpgradeSuccessfulAModel = new UpgradeSuccessfulAModelImpl();
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {

    }

    @Override
    protected void onRequestFailure(String e) {

    }
}
