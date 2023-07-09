package com.crazymakercircle.mutithread.basic.create;

import com.crazymakercircle.util.Print;
import org.junit.Test;

import static com.crazymakercircle.util.ThreadUtil.getCurThreadName;

/**
 * Created by 尼恩@疯狂创客圈.
 */

public class CreateDemo {

    public static final int MAX_TURN = 5;
    static int threadNo = 1;

    static class DemoThread extends Thread {
        public DemoThread() {
            super("Mall-" + threadNo++);
        }

        // cpu 时间片开始运行
        public void run() {
            for (int i = 1; i < MAX_TURN; i++) {
                Print.cfo(getName() + ", 轮次：" + i);
            }
            Print.cfo(getName() + " 运行结束.");
        }
    }

    @Test
    public void test() {
        int process = Runtime.getRuntime().availableProcessors();
        System.out.println(process);
    }

    public static void main(String args[]) throws InterruptedException {
        Thread thread = null;
        // 方法一：使用Thread子类创建和启动线程
        for (int i = 0; i < 2; i++) {
            thread = new DemoThread();
            thread.start();
        }
        Print.cfo(getCurThreadName() + " 运行结束.");
    }
}