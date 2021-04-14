package com.ronda.tech.upgrade.utils;

import android.util.Log;

/**
 * Created by SNOW on 2019/3/18.
 * log格式化输出日志
 */

public class LogUtils {

    /**
     * 写文件的锁对象
     */

    public static void i(String tag, String msg){
        Log.i(tag, "i: ---------------------->"+msg);
    }

    public static void w(String tag, String msg){
        Log.w(tag, "i: **********************>"+msg);
    }

    public static void d(String tag, String msg){
        Log.d(tag, "i: ~~~~~~~~~~~~~~~~~~~~~~>"+msg);
    }

    public static void e(String tag, String msg){
        Log.e(tag, "i: !!!!!!!!!!!!!!!!!!!!!!!>"+msg);
    }

}
