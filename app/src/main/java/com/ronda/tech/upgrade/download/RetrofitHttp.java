package com.ronda.tech.upgrade.download;

import android.util.Log;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ronda.tech.upgrade.base.MyApplication;
import com.ronda.tech.upgrade.constant.Constants;
import com.ronda.tech.upgrade.http.IWebService;
import com.ronda.tech.upgrade.presenter.callback.HomeCallback;
import com.ronda.tech.upgrade.utils.SharedPreferencesUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 文件下载专用网络类
 */
public class RetrofitHttp {
    private static final int DEFAULT_TIMEOUT = 10;
    private static final String TAG = "RetrofitClient";

    private OkHttpClient okHttpClient;
    private IWebService webService;

    public static String baseUrl = Constants.HOST_IP;

    private static RetrofitHttp sIsntance;

    public static RetrofitHttp getInstance() {
        if (sIsntance == null) {
            synchronized (RetrofitHttp.class) {
                if (sIsntance == null) {
                    sIsntance = new RetrofitHttp();
                }
            }
        }
        return sIsntance;
    }

    private RetrofitHttp() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        webService = retrofit.create(IWebService.class);
    }


    public void downloadFile(String fileName, String savePath, final String remoteUrl, long start, final DownloadCallBack downloadCallback) {
        File file1 = new File(savePath);
        if (!file1.exists()){
            boolean result = file1.mkdirs();
            Log.e("hnf","result:"+result);
        }

        byte[] buf = new byte[1024*1024];
        HttpURLConnection httpURLConnection = null;
        OutputStream outputStream = null;
        BufferedInputStream bis = null;
        RandomAccessFile rndFile = null;
        long total = start;
        boolean flag = false;
        String md5 = "";
        // 下载文件
        try {
            URL url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            //允许输入输出
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            //设置连接超时时间5s
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(1000*60);
            // 设置User-Agent
            httpURLConnection.setRequestProperty("User-Agent", "Net");
            // 设置续传开始
            httpURLConnection.setRequestProperty("Range", "bytes=" + start + "-");
            //发送数据
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(new String("fileName=" + fileName).getBytes("UTF-8"));
            outputStream.flush();
            // 获取输入流
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            int size = 0;
            StringBuffer sb = new StringBuffer("");
            String header = httpURLConnection.getHeaderField("Content-Range");
            long fileLength = Long.parseLong(header.substring(header.lastIndexOf("/")+1));
            md5 = httpURLConnection.getHeaderField("MD5");
            int progress = 0;
            int lastProgress = 0;
            Log.e(TAG,"content-length：" + fileLength);
            rndFile = new RandomAccessFile(savePath + fileName, "rwd");
            rndFile.seek(start);
            while ((size = bis.read(buf)) != -1) {
                rndFile.write(buf, 0, size);
                total+=size;
                lastProgress = progress;
                progress = (int) (total * 100 / fileLength);
                if (progress > 0 && progress != lastProgress) {
                    downloadCallback.onProgress(progress);
                }
            }

            rndFile.close();
            downloadCallback.onCompleted(md5);
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            downloadCallback.onError(e.getMessage());
        }

        //断点续传时请求的总长度
        File file = new File(Constants.APP_ROOT_PATH + Constants.DOWNLOAD_DIR, fileName);
        String totalLength = "-";
        if (file.exists()) {
            totalLength += file.length();
        }
    }
}