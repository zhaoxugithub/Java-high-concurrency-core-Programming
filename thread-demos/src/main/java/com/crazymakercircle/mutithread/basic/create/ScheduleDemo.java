package com.crazymakercircle.mutithread.basic.create;

import com.crazymakercircle.util.Print;

import static com.crazymakercircle.util.ThreadUtil.getCurThreadName;

/**
 * Created by 尼恩@疯狂创客圈.
 */

public class ScheduleDemo {
    public static final int MAX_TURN = 50;

    public static Thread getCurThread() {
        return Thread.currentThread();
    }

    public static int getCurPriority() {
        return Thread.currentThread().getPriority();
    }

    static int threadNo = 1;

    static class DemoThread extends Thread {
        public DemoThread() {
            super("Mall-" + threadNo++);
        }
        public void run() {
            long startTime = System.currentTimeMillis();
            for (int j = 1; j < MAX_TURN; j++) {
                long used = System.currentTimeMillis() - startTime;
                Print.cfo(getName() + ", 运行时间：" + used);
            }
            Print.cfo(getName() + " 运行结束.");
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Thread thread = null;
        for (int i = 0; i < 20; i++) {
            thread = new DemoThread();
            thread.start();
        }
        Print.cfo(getCurThreadName() + " 运行结束.");
    }
}