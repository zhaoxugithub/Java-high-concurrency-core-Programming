package com.crazymakercircle.thread;

import com.crazymakercircle.util.ThreadUtil;
import org.junit.jupiter.api.Test;

public class NotifyTest {
    Object lock = new Object();

    @Test
    public void testNotify() {
        synchronized (lock) {
            lock.notify();
        }
        ThreadUtil.sleepMilliSeconds(1000000);
    }

    // 写一个冒泡排序
    @Test
    public static void testSort() {
        int[] arr = {1, 5, 3, 4, 2};
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    // 写一个给定特定日期算出星期几的方法
    public static void main(String[] args) {
        testSort();
    }
}
