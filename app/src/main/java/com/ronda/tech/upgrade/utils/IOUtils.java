package com.ronda.tech.upgrade.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
    private static final String TAG = "IOUtils";
    /**
     * 关闭流
     */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(TAG,e.getMessage());
            }
        }
        return true;
    }


}
