package com.crazymakercircle.mutithread.basic.create3;

import com.crazymakercircle.util.JvmUtil;
import com.crazymakercircle.util.Print;

/**
 * Created by 尼恩@疯狂创客圈.
 */

public class StatusDemo2 {
    public static final int SLEEP_GAP = 500 * 1000;
    public static final int MAX_TURN = 3;

    public static String getCurThreadName() {
        return Thread.currentThread()
                     .getName();
    }

    public static void main(String args[]) throws InterruptedException {
        Print.cfo("当前进程的ID是" + JvmUtil.getProcessID());
        // 一条RUNNABLE状态的线程,无限制运行
        Thread runnableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    int i = 1;
                    i++;
                }
            }
        }, "runnableThread");
        runnableThread.start();
        // 演示一条WAITING状态的线程，调用join()实例方法
        Thread waitingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runnableThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "waitingThread");
        waitingThread.start();
    }
}