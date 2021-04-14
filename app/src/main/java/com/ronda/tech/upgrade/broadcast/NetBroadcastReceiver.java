package com.ronda.tech.upgrade.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ronda.tech.upgrade.utils.LogUtils;

/**
 * 检查手机网络状态变化
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NetBroadcastReceiver";
    private NetChangeCallback callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    LogUtils.e(TAG,"NET_WIFI");
                    callback.onNetChanged(ConnectivityManager.TYPE_WIFI);
                }else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                    LogUtils.e(TAG,"NET_MOBILE");
                    callback.onNetChanged(ConnectivityManager.TYPE_MOBILE);
                }
            }else {
                LogUtils.e(TAG,"NO_NET");
                callback.onNetChanged(-1);
            }
        }
    }

    public void setCallback(NetChangeCallback callback) {
        this.callback = callback;
    }

    public interface NetChangeCallback{
        void onNetChanged(int type);
    }
}
