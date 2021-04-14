package com.ronda.tech.upgrade.base;


import android.content.Context;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.http.IWebService;
import com.ronda.tech.upgrade.model.ResponseInfoModel;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author snow.huang
 * created 2021/1/23 15:55
 * 用于创建Retrofit实例
 */
public abstract class BaseImpl {
    private static final String TAG = "BaseImpl";

    private Context mContext;
    private Retrofit mRetrofit;
    protected IWebService iWebService;

    public BaseImpl(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkhttpBuilder())
                .build();
        iWebService = mRetrofit.create(IWebService.class);
    }

    /**
     * 获取okhttp实例
     * @return
     */
    private OkHttpClient getOkhttpBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        LogUtils.d(TAG,message);
                    }
                })
                .setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(35, TimeUnit.SECONDS)
                .readTimeout(35,TimeUnit.SECONDS)
                .writeTimeout(35,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    protected Callback webCallback = new Callback<ResponseInfoModel>() {
        @Override
        public void onResponse(Call<ResponseInfoModel> call, retrofit2.Response<ResponseInfoModel> response) {
            ResponseInfoModel body = response.body();
            if (body == null){
                return;
            }
            Gson gson = new Gson();
            String str = gson.toJson(body);
            LogUtils.e(TAG,"json str:"+str);
//            if ("0".equals(body.getRet())){
                onRequestSuccessful(body);
//            }else {
//                onRequestFailure(body.getRet()+" "+body.getMessage());
//            }
        }

        @Override
        public void onFailure(Call<ResponseInfoModel> call, Throwable t) {
            LogUtils.e(TAG,"onFailure:"+t.getMessage()+" call fail:"+call.toString());
            onRequestFailure(t.getMessage());
        }
    };

    protected abstract void onRequestSuccessful(ResponseInfoModel body);

    protected abstract void onRequestFailure(String e);
}
