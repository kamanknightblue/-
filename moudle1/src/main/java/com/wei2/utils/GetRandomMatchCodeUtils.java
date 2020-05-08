package com.wei2.utils;

import java.util.Random;

public class GetRandomMatchCodeUtils {
    public static String generate(){
        Random random = new Random();
        int a = random.nextInt();
        if (a < 0) {
            a = Math.abs(a);
        }
        return a + "";
    }
}
