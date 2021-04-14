package com.ronda.tech.upgrade.utils;

import android.text.TextUtils;

public class DecodeUtil {
    public static void main(String[] args) {
        int hex1 = Integer.parseInt("C8",16);
        int hex2 = Integer.parseInt("01",16);
        int hex3 = Integer.parseInt("C9",16);
        int hex4 = hex1^hex2;
    }

    /**
     * 把mac地址和版本号进行异或运算
     * @param mac mac地址
     * @param version 版本号
     * @return 异或运算的结果数组
     */
    public static String decodeMac(String mac,String version){
        if (TextUtils.isEmpty(mac)||TextUtils.isEmpty(version)){
            return null;
        }
        String[] macArr = mac.split(":");
        String[] verArr = version.split(" ");
        //mac[0]^ver[0]  mac[1]^ver[1] ver[0]^ver[1]
        int hex1 = Integer.parseInt(macArr[0],16)^Integer.parseInt(verArr[0],16);
        int hex2 = Integer.parseInt(macArr[1],16)^Integer.parseInt(verArr[1],16);
        int hex3 = Integer.parseInt(verArr[0],16)^Integer.parseInt(verArr[1],16);
        String decodeStr = intToHex(hex1)+intToHex(hex2)+intToHex(hex3);
        return decodeStr;
    }

    /**
     * 将10进制转为16进制
     *
     * @param n
     * @return java.lang.String
     */
    private static String intToHex(int n) {
        if (n == 0) {
            return "00";
        }
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a.length() < 2 ? "0" + a : a;
    }

}
