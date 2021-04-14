package com.ronda.tech.upgrade.model.impl;

import com.google.gson.Gson;
import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.base.MyApplication;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.inter.ILoginAModel;
import com.ronda.tech.upgrade.presenter.callback.LoginCallback;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.MD5Util;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;

import java.util.HashMap;

import retrofit2.Call;

public class LoginAModelImpl extends BaseImpl implements ILoginAModel {
    private static final String TAG = "LoginAModelImpl";
    private Call<ResponseInfoModel> mLoginCall;
    private LoginCallback modelCallback;

    @Override
    public void loginNow(String acount, String psw, LoginCallback callback) {
        modelCallback = callback;

        String md5Psw = MD5Util.getInstance().getMD5String(psw);
        LogUtils.e(TAG,"PSW MD5:"+md5Psw);
        ResponseInfoModel bean = new ResponseInfoModel();
        ResponseInfoModel.Data beanData = bean.new Data();
        bean.setSessionflag(1);
        beanData.setLoginname(acount);
        beanData.setPassword(md5Psw);
        beanData.setOpen_method("runFactoryUserLogin");

        bean.setData(beanData);
        bean.setMethod("ysnetwork.shop.com.factory.request");

        Gson gson = new Gson();
        String str = gson.toJson(bean);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("data",str);

        //Retrofit本身会对json字符串进行url编码
        mLoginCall = iWebService.acountLogin(hashMap);
        mLoginCall.enqueue(webCallback);
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {
        if (body.getData().getToken_code() != null){
            SharedPreferencesUtils.setParam(MyApplication.getContext(),"token_code",body.getData().getToken_code());
        }
        modelCallback.loginSuccess();
    }

    @Override
    protected void onRequestFailure(String e) {
        modelCallback.loginFail(e);
    }
}
