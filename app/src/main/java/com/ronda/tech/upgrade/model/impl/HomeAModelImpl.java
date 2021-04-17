package com.ronda.tech.upgrade.model.impl;

import com.google.gson.Gson;
import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.base.MyApplication;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.inter.IHomeAModel;
import com.ronda.tech.upgrade.presenter.callback.DownloadCallback;
import com.ronda.tech.upgrade.presenter.callback.HomeCallback;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;
import java.util.HashMap;
import retrofit2.Call;

public class HomeAModelImpl extends BaseImpl implements IHomeAModel {
    private static final String TAG = "HomeAModelImpl";
    private Call<ResponseInfoModel> mLoginCall;
    private HomeCallback homeCallback;

    @Override
    public void getLocalVerisons(HomeCallback callback) {
        getRemoteSystemVersion(callback);
        getLoocalDeviceVerison(callback);
    }

    @Override
    public void downloadUpdateFile(HomeCallback callback) {
        homeCallback = callback;

        ResponseInfoModel bean = new ResponseInfoModel();
        ResponseInfoModel.Data beanData = bean.new Data();
        bean.setSessionflag(1);
        beanData.setToken_code((String) SharedPreferencesUtils.getParam(MyApplication.getContext(),"token_code",""));
        beanData.setOpen_method("runFactoryBinpackcList");

        bean.setData(beanData);
        bean.setMethod("ysnetwork.shop.com.factory.request");

        Gson gson = new Gson();
        String str = gson.toJson(bean);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("data",str);
        mLoginCall = iWebService.acountLogin(hashMap);
        mLoginCall.enqueue(webCallback);
    }


    @Override
    public void getMacFromService(String id, HomeCallback callback) {
        homeCallback = callback;

        LogUtils.e(TAG,"id:"+id);
        ResponseInfoModel bean = new ResponseInfoModel();
        ResponseInfoModel.Data beanData = bean.new Data();
        bean.setSessionflag(1);
        beanData.setCabinet_id(id);
        beanData.setToken_code((String) SharedPreferencesUtils.getParam(MyApplication.getContext(),"token_code",""));
        beanData.setOpen_method("runFactoryCabinetInfo");

        bean.setData(beanData);
        bean.setMethod("ysnetwork.shop.com.factory.request");

        Gson gson = new Gson();
        String str = gson.toJson(bean);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("data",str);
        mLoginCall = iWebService.acountLogin(hashMap);
        mLoginCall.enqueue(webCallback);
    }

    /**
     * 读取本地设备的版本信息
     * @param callback
     */
    private void getLoocalDeviceVerison(HomeCallback callback) {

    }

    /**
     * 获取远程系统的版本信息
     * @param callback
     */
    private void getRemoteSystemVersion(HomeCallback callback) {
        homeCallback = callback;
        HashMap<String,String> hashMap = new HashMap<>();
//        hashMap.put("hnf","");
//        mLoginCall = iWebService.remoteVersion(hashMap);
//        mLoginCall.enqueue(webCallback);
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {
        if (homeCallback != null){
            homeCallback.getVerisonSuccessful(body);
        }
    }

    @Override
    protected void onRequestFailure(String e) {
        homeCallback.getVerisonFails(e);
    }
}
