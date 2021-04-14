package com.ronda.tech.upgrade.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装一些格式判断工具
 */
public class JudgeUtils {

    /**
     * 判断一个字符串是否是mac地址
     * @param val
     * @return
     */
    public static boolean stringIsMac(String val) {
        String regex = "^[A-F0-9]{2}([-:]?[A-F0-9]{2})([-:.]?[A-F0-9]{2})([-:]?[A-F0-9]{2})([-:.]?[A-F0-9]{2})([-:]?[A-F0-9]{2})$";
        Matcher matcher = Pattern.compile(regex).matcher(val);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
