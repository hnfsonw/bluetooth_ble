package com.ronda.tech.upgrade.base;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context sInstance;

    /**
     * 得到上下文
     *
     * @return
     */
    public static Context getContext() {
        return sInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = getApplicationContext();

    }
}
