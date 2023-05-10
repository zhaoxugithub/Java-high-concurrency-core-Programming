package com.crazymakercircle.threadpool;

// 直接抛出异常的拒绝策略
public class AbortPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPool executor) {
        throw new RuntimeException("The thread pool is full and the task queue is full.");
    }
}