package com.ronda.tech.upgrade.constant;

import android.os.Environment;

import com.ronda.tech.upgrade.base.MyApplication;

public class Constants {

    public static final int DEBUGLEVEL = 0;

    /**
     * 服务器请求接口
     */
    public static final String HOST_IP = "https://ronda.jiede-tech.cn/";//测试

//    public static final String HOST_IP = "https://114.215.66.14:80/v1/";//正式

    public static final String ACOUNT_LOGIN = "ysnetworkweb/common/gateway.do";

    public static final String RESET_ACOUNT_PASSWORD = "ysnetworkweb/common/gateway.do";

    public static final String ACCOUNT_FORGET_PASSWORD = "ysnetworkweb/common/gateway.do";

    public static final String SEND_CHECKCODE = "ysnetworkweb/common/gateway.do";

    public static final String REMOTE_VERSION = "ysnetworkweb/common/gateway.do";


    /**
     * sharePreferent使用字段
     */
    public static final String REMEMBER_ACOUNT = "acount";
    public static final String REMEMBER_PASSWORLD = "passworld";
    public static final String CHECKBOX_STATE = "checkbox";
    public static final String LOGIN_SUCCESS_LASTTIME = "login_in";

    public final static String APP_ROOT_PATH = Environment.getExternalStorageDirectory()+"/"+ MyApplication.getContext().getPackageName();
    public final static String DOWNLOAD_DIR = "/download/";
}
