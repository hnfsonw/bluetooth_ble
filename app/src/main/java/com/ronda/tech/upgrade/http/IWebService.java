package com.ronda.tech.upgrade.http;

import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.model.ResponseInfoModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * @author snow.huang
 * created 2021/1/23 16:16
 * 网络请求接口
 */
public interface IWebService {
    /**
     * 登录
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(Constants.ACOUNT_LOGIN)
    Call<ResponseInfoModel> acountLogin(@FieldMap Map<String, String> fields);


    /**
     * 重置密码
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(Constants.RESET_ACOUNT_PASSWORD)
    Call<ResponseInfoModel> resetAcountPassword(@FieldMap Map<String, String> fields);


    /**
     * 忘记密码
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(Constants.ACCOUNT_FORGET_PASSWORD)
    Call<ResponseInfoModel> accountForgetpassword(@FieldMap Map<String, String> fields);


    /**
     * 发送验证码
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(Constants.SEND_CHECKCODE)
    Call<ResponseInfoModel> sendCheckCode(@FieldMap Map<String, String> fields);

    /**
     * 获取远程版本信息
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(Constants.REMOTE_VERSION)
    Call<ResponseInfoModel> remoteVersion(@FieldMap Map<String, String> fields);

    @Streaming
    @GET
    Observable<ResponseBody> executeDownload(@Header("Range") String range, @Url() String url);
}
