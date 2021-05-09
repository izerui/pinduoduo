package com.example.pingduoduo.selenium;

import org.apache.commons.lang3.RandomUtils;

public final class RandomTimers {
    public static void execute(int start, int end, Runnable runnable) {
        try {
            Thread.sleep(RandomUtils.nextInt(start * 1000, end * 1000));
            runnable.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
