package com.ronda.tech.upgrade.utils;

public class Test {
    public static void main(String[] args) {
        String a = "aafe000000e9";
        String b = "c6f602";

        byte[] aaa = TypeChangeUtils.hexToByteArr(a);
        byte[] bb = TypeChangeUtils.hexToByteArr(b);

        String c = TypeChangeUtils.byte2hex(aaa);
        String d = TypeChangeUtils.byte2hex(bb);
    }
}
