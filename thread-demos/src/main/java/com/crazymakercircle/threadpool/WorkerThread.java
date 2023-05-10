package com.crazymakercircle.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


// 定义一个工作线程类
public class WorkerThread extends Thread {
    private List<WorkerThread> threads;
    // 空闲时长
    private long keepAliveTime;
    // 用于从任务队列中取出并执行任务
    private BlockingQueue<Runnable> taskQueue;

    // 构造方法，传入任务队列
    public WorkerThread(long keepAliveTime, BlockingQueue<Runnable> taskQueue, List<WorkerThread> threads) {
        this.keepAliveTime = keepAliveTime;
        this.taskQueue = taskQueue;
        this.threads = threads;
    }

    // 重写run方法
    @Override
    public void run() {
        long lastActiveTime = System.currentTimeMillis();
        // 循环执行，直到线程被中断
        Runnable task;
        while (!Thread.currentThread().isInterrupted() || !taskQueue.isEmpty()) {
            try {
                // 从任务队列中取出一个任务，如果队列为空，则阻塞等待
                task = taskQueue.poll(keepAliveTime, TimeUnit.MILLISECONDS);
                RunnableWrapper wrapper = (RunnableWrapper) task;
                if (task != null) {
                    System.out.printf("WorkerThread %s, poll current task: %s%n", Thread.currentThread().getName(), wrapper.getTaskId());
                    task.run();
                    lastActiveTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lastActiveTime >= keepAliveTime) {
                    // 从线程池中移除
                    threads.remove(this);
                    System.out.printf("WorkerThread %s exit %n", Thread.currentThread().getName());
                    break;
                }
            } catch (Exception e) {
                // 从线程池中移除
                System.out.printf("WorkerThread %s occur exception%n", Thread.currentThread().getName());
                threads.remove(this);
                e.printStackTrace();
                // 如果线程被中断，则退出循环
                break;
            }
        }
    }
}
