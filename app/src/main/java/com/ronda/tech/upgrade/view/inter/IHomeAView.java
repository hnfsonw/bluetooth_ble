package com.ronda.tech.upgrade.view.inter;

import com.ronda.tech.upgrade.model.ResponseInfoModel;

public interface IHomeAView {
    void verisonInfosRespones(ResponseInfoModel infoBean);
    void fails(String msg);
    void upgradeFileInfos(ResponseInfoModel infoModel);
}
