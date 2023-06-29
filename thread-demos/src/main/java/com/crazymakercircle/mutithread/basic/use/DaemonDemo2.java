package com.crazymakercircle.mutithread.basic.use;

import com.crazymakercircle.util.Print;

import static com.crazymakercircle.util.ThreadUtil.getCurThreadName;
import static com.crazymakercircle.util.ThreadUtil.sleepMilliSeconds;

/**
 * Created by 尼恩@疯狂创客圈.
 */

public class DaemonDemo2 {

    public static final int SLEEP_GAP = 500;
    public static final int MAX_TURN = 5;

    static class NormalThread extends Thread {
        static int threadNo = 1;

        public NormalThread() {
            super("normalThread-" + threadNo);
            threadNo++;
        }

        public void run() {
            for (int i = 0; ; i++) {
                sleepMilliSeconds(SLEEP_GAP);
                Print.synTco(getName() + ", 守护状态为:" + isDaemon());
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Thread daemonThread = new Thread(() ->
        {
            for (int i = 0; i < 5; i++) {
                Thread normalThread = new NormalThread();
//                normalThread.setDaemon(false);
                normalThread.start();
            }
        }, "daemonThread");
        daemonThread.setDaemon(true);
        daemonThread.start();
        // 这里，一定不能让main线程结束，否则看不到结果
        sleepMilliSeconds(10000000);
        Print.synTco(getCurThreadName() + " 运行结束.");
    }
}