package com.util;

import java.util.concurrent.atomic.AtomicInteger;

public class CountUtil {
    private static AtomicInteger count = new AtomicInteger(0);
    private static long start;

    public static void start() {
        count.set(0);
    }

    public static void count() {
        int num = count.addAndGet(1);
        if (num == 1) {
            start = System.currentTimeMillis();
        }
        if (num % 1000000 == 0) {
            System.out.println("共用时：" + (System.currentTimeMillis() - start));
            count.set(0);
        }
    }
}
