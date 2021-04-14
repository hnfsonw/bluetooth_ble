package com.ronda.tech.upgrade.model.impl;

import com.google.gson.Gson;
import com.ronda.tech.upgrade.base.BaseImpl;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.model.inter.IForgetPasswoldAModel;
import com.ronda.tech.upgrade.presenter.callback.CheckCodeCallback;
import com.ronda.tech.upgrade.presenter.callback.ForgetPswCallback;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.MD5Util;

import java.util.HashMap;

import retrofit2.Call;

public class ForgetPasswoldAModelImpl extends BaseImpl implements IForgetPasswoldAModel {
    private static final String TAG = "ForgetPasswoldAModelImp";
    private CheckCodeCallback modelCallback;
    private Call<ResponseInfoModel> mCheckCodeCall;
    private ForgetPswCallback pswCallback;
    private int requestType;//0:获取验证码  1:提交验证码信息

    @Override
    public void getCheckCode(String phone, CheckCodeCallback callback) {
        requestType = 0;
        modelCallback = callback;
        LogUtils.e(TAG,"phone:"+phone);

        ResponseInfoModel bean = new ResponseInfoModel();
        ResponseInfoModel.Data beanData = bean.new Data();
        bean.setSessionflag(1);
        beanData.setLoginname(phone);
        beanData.setOpen_method("runFactorySendCode");

        bean.setData(beanData);
        bean.setMethod("ysnetwork.shop.com.factory.request");

        Gson gson = new Gson();
        String str = gson.toJson(bean);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("data",str);

        mCheckCodeCall = iWebService.sendCheckCode(hashMap);
        mCheckCodeCall.enqueue(webCallback);
    }

    @Override
    public void summit(String phone, String checkCode,String psw, ForgetPswCallback callback) {
        requestType = 1;
        pswCallback = callback;
        LogUtils.e(TAG,"phone:"+phone+" checkCode:"+checkCode);
        LogUtils.e(TAG,"new psw MD5:"+MD5Util.getInstance().getMD5String(psw.toLowerCase()));
        ResponseInfoModel bean = new ResponseInfoModel();
        ResponseInfoModel.Data beanData = bean.new Data();
        bean.setSessionflag(1);
        beanData.setLoginname(phone);
        beanData.setPassword(MD5Util.getInstance().getMD5String(psw));
        beanData.setCode(checkCode);
        beanData.setOpen_method("runFactoryForgetPassword");

        bean.setData(beanData);
        bean.setMethod("ysnetwork.shop.com.factory.request");

        Gson gson = new Gson();
        String str = gson.toJson(bean);

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("data",str);

        mCheckCodeCall = iWebService.accountForgetpassword(hashMap);
        mCheckCodeCall.enqueue(webCallback);
    }

    @Override
    protected void onRequestSuccessful(ResponseInfoModel body) {
        if (requestType == 0){
            modelCallback.getCheckCodeSuccess();
        }

        if (requestType == 1){
            pswCallback.summitSuccess();
        }
    }

    @Override
    protected void onRequestFailure(String e) {
        if (requestType == 0){
            modelCallback.getCheckCodeFail(e);
        }

        if (requestType == 1){
            pswCallback.summitFail(e);
        }
    }
}
