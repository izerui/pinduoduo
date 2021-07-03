package com.example.pinduoduo.utils;

import org.apache.commons.lang3.RandomUtils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class StringRandomUtils {

    /**
     * 获取指定长度随机简体中文
     *
     * @return String
     */
    public static String getRandomJianHan() {
        String ret = "";
        for (int i = 0; i < RandomUtils.nextInt(50, 100); i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); //获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); //获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBk"); //转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

//    public static void main(String[] args) {
//        String randomJianHan = getRandomJianHan();
//        System.out.println(randomJianHan);
//    }
}
