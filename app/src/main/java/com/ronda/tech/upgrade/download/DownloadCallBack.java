package com.ronda.tech.upgrade.download;

/**
 * 创建时间：2018/3/24
 */

public interface DownloadCallBack {

    void onProgress(int progress);

    void onCompleted(String md5);

    void onError(String msg);

}
