package com.ronda.tech.upgrade.utils;

public class TypeChangeUtils {

    private static final String HexStr = "0123456789abcdef";

    /**
     * 十六进制字符串转为字节数组
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }
    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static byte[] hexToByteArr(String hexStr) {
        char[] charArr = hexStr.toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }


    public static String byte2hex(byte [] buffer){
        StringBuilder sb = null;
        if (buffer.length > 0){
            sb = new StringBuilder();
            String tmp = null;
            for (byte b : buffer) {
                // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
                tmp = Integer.toHexString(0xFF & b);
                if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
                {
                    tmp = "0" + tmp;
                }
                sb.append(tmp);
            }
        }
        return sb.toString();
    }
}
