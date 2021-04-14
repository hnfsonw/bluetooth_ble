package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.UpgradeFailAModelImpl;
import com.ronda.tech.upgrade.model.inter.IUpgradeFailAModel;
import com.ronda.tech.upgrade.presenter.inter.IUpgradeFailAPresenter;
import com.ronda.tech.upgrade.view.inter.IUpgradeFailAView;

public class UpgradeFailAPresenterImpl extends BaseImpl implements IUpgradeFailAPresenter {
    private IUpgradeFailAView mIUpgradeFailAView;
    private IUpgradeFailAModel mIUpgradeFailAModel;

    public UpgradeFailAPresenterImpl(IUpgradeFailAView aIUpgradeFailAView) {
        mIUpgradeFailAView = aIUpgradeFailAView;
        mIUpgradeFailAModel = new UpgradeFailAModelImpl();
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {

    }

    @Override
    protected void onRequestFailure(String e) {

    }
}
