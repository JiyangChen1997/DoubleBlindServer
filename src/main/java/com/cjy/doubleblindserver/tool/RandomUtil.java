package com.cjy.doubleblindserver.tool;

import java.util.Random;

public class RandomUtil {

    public synchronized static Long uniqueLong() {
        Random r = new Random();
        return System.currentTimeMillis() * (int) 1e6 + r.nextInt((int) 1e6);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(RandomUtil.uniqueLong());
        }
    }
}
