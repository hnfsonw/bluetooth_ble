package com.ronda.tech.upgrade.model.inter;

import com.ronda.tech.upgrade.presenter.callback.DownloadCallback;
import com.ronda.tech.upgrade.presenter.callback.HomeCallback;

public interface IHomeAModel {
    void getLocalVerisons(HomeCallback callback);
    void downloadUpdateFile(HomeCallback callback);
    void getMacFromService(String id,HomeCallback callback);
}