package com.crazymakercircle.threadpool;

// 忽略任务的拒绝策略
public class DiscardPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable r, ThreadPool executor) {
        // do nothing
        RunnableWrapper wrapper = (RunnableWrapper) r;
        System.out.println("Task rejected: " + wrapper.getTaskId());
    }
}