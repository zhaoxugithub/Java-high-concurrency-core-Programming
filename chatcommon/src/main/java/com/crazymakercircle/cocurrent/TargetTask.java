package com.crazymakercircle.cocurrent;

import com.crazymakercircle.util.Print;

import java.util.concurrent.atomic.AtomicInteger;

import static com.crazymakercircle.util.ThreadUtil.sleepMilliSeconds;

// 异步的执行目标类
public class TargetTask implements Runnable {
    public static final int SLEEP_GAP = 500;
    static AtomicInteger taskNo = new AtomicInteger(1);
    protected String taskName;

    public TargetTask() {
        taskName = "task-" + taskNo.get();
        taskNo.incrementAndGet();
    }

    public void run() {
        Print.tco("任务：" + taskName + " doing");
        // 线程睡眠一会
        try {
            Thread.sleep(SLEEP_GAP);
        } catch (InterruptedException e) {
            Print.tco(taskName + " 运行被异常打断." + e.getMessage());
        }
        Print.tco(taskName + " 运行结束.");
    }

    @Override
    public String toString() {
        return "TargetTask{" + taskName + '}';
    }
}