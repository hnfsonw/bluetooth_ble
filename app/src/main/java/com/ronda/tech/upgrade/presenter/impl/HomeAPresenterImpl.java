package com.ronda.tech.upgrade.presenter.impl;

import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.impl.HomeAModelImpl;
import com.ronda.tech.upgrade.model.inter.IHomeAModel;
import com.ronda.tech.upgrade.presenter.callback.DownloadCallback;
import com.ronda.tech.upgrade.presenter.callback.HomeCallback;
import com.ronda.tech.upgrade.presenter.inter.IHomeAPresenter;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.view.inter.IHomeAView;

public class HomeAPresenterImpl implements IHomeAPresenter {
    private IHomeAView mIHomeAView;
    private IHomeAModel mIHomeAModel;

    public HomeAPresenterImpl(IHomeAView aIHomeAView) {
        mIHomeAView = aIHomeAView;
        mIHomeAModel = new HomeAModelImpl();
    }

    @Override
    public void getLocalVerisons() {
//        mIHomeAModel.getLocalVerisons(new HomeCallback() {
//            @Override
//            public void getVerisonSuccessful(String deviceVerison, String cpuVerison, String systemVersion, String fileName) {
//                mIHomeAView.verisonInfosRespones(deviceVerison,cpuVerison,systemVersion,fileName);
//            }
//
//            @Override
//            public void getVerisonFails(String msg) {
//                mIHomeAView.fails(msg);
//            }
//        });
    }

    @Override
    public void downloadFile() {
        mIHomeAModel.downloadUpdateFile(new HomeCallback() {
            @Override
            public void getVerisonSuccessful(ResponseInfoModel responseInfo) {
                mIHomeAView.upgradeFileInfos(responseInfo);
            }

            @Override
            public void getVerisonFails(String msg) {
                mIHomeAView.fails(msg);
            }
        });
    }

    @Override
    public void getMacFromService(String cabinet_id) {
        mIHomeAModel.getMacFromService(cabinet_id, new HomeCallback() {
            @Override
            public void getVerisonSuccessful(ResponseInfoModel responseInfo) {
                mIHomeAView.verisonInfosRespones(responseInfo);
            }

            @Override
            public void getVerisonFails(String msg) {
                mIHomeAView.fails(msg);
            }
        });
    }

}
