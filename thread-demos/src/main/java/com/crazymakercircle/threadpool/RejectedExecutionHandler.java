package com.crazymakercircle.threadpool;

// 拒绝策略接口
public interface RejectedExecutionHandler {
    // 参数：r 代表被拒绝的任务，executor 代表线程池对象
    void rejectedExecution(Runnable r, ThreadPool executor);
}

