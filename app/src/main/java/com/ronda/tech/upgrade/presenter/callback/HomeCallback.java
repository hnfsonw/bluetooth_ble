package com.ronda.tech.upgrade.presenter.callback;

import com.ronda.tech.upgrade.model.ResponseInfoModel;

public interface HomeCallback {
    void  getVerisonSuccessful(ResponseInfoModel responseInfo);
    void getVerisonFails(String msg);
}
