package com.crazymakercircle.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleThreadPool implements ThreadPool {
    // 线程池初始化时的线程数量
    private int initialSize;
    // 线程池最大线程数量
    private int maxSize;
    // 线程池核心线程数量
    private int coreSize;
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;
    // 用于存放和管理工作线程的集合
    private List<WorkerThread> threads;
    // 是否已经被shutdown标志
    private volatile boolean isShutdown = false;
    // 默认的拒绝策略
    private final static RejectedExecutionHandler DEFAULT_REJECT_HANDLER = new AbortPolicy();
    // 拒绝策略成员变量
    private final RejectedExecutionHandler rejectHandler;
    // 线程空闲时长
    private long keepAliveTime;

    public SimpleThreadPool(int initialSize, int maxSize, int coreSize, int queueSize, long keepAliveTime) {
        this(initialSize, maxSize, coreSize, queueSize, keepAliveTime, DEFAULT_REJECT_HANDLER);
    }

    public SimpleThreadPool(int initialSize, int maxSize, int coreSize, int queueSize, long keepAliveTime, RejectedExecutionHandler rejectHandler) {
        System.out.printf("初始化线程池: initialSize: %d, maxSize: %d, coreSize: %d%n", initialSize, maxSize, coreSize);
        // 初始化参数
        this.initialSize = initialSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        taskQueue = new LinkedBlockingQueue<>(queueSize);
        threads = new ArrayList<>(initialSize);
        this.rejectHandler = rejectHandler;
        this.keepAliveTime = keepAliveTime;
        // 初始化方法，创建一定数量的工作线程，并启动它们
        for (int i = 0; i < initialSize; i++) {
            WorkerThread workerThread = new WorkerThread(keepAliveTime, taskQueue, threads);
            workerThread.start();
            threads.add(workerThread);
        }
    }

    @Override
    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        RunnableWrapper wrapper = (RunnableWrapper) task;
        System.out.printf("put task: %s %n" , wrapper.getTaskId());

        // 当线程数量小于核心线程数时，创建新的线程
        if (threads.size() < coreSize) {
            addWorkerThread(task);
            System.out.printf("小于核心线程数，创建新的线程: thread count: %d, queue remainingCapacity : %d%n", threads.size(), taskQueue.remainingCapacity());
        } else if (!taskQueue.offer(task)) {
            // 当队列已满时，且线程数量小于最大线程数量时，创建新的线程
            if (threads.size() < maxSize) {
                addWorkerThread(task);
                System.out.printf("队列已满, 创建新的线程: thread count: %d, queue remainingCapacity : %d%n", threads.size(), taskQueue.remainingCapacity());
            } else {
                rejectHandler.rejectedExecution(task, this);
            }
        } else {
            System.out.printf("任务放入队列: thread count: %d, queue remainingCapacity : %d%n", threads.size(), taskQueue.remainingCapacity());
        }
    }


    // 创建新的线程，并执行任务
    private void addWorkerThread(Runnable task) {
        WorkerThread workerThread = new WorkerThread(keepAliveTime, taskQueue, threads);
        workerThread.start();
        threads.add(workerThread);
        // 任务放入队列
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    // 关闭线程池, 等待所有线程执行完毕
    @Override
    public void shutdown() {
        System.out.printf("shutdown thread, count: %d, queue remainingCapacity : %d%n", threads.size(), taskQueue.remainingCapacity());
        // 修改状态
        isShutdown = true;
        for (WorkerThread thread : threads) {
            // 中断线程
            thread.interrupt();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        System.out.printf("shutdown thread now, count: %d, queue remainingCapacity : %d%n", threads.size(), taskQueue.remainingCapacity());

        // 修改状态
        isShutdown = true;
        // 清空队列
        List<Runnable> remainingTasks = new ArrayList<>();
        taskQueue.drainTo(remainingTasks);

        // 中断所有线程
        for (WorkerThread thread : threads) {
            thread.interrupt();
        }
        // 返回未执行任务集合
        return remainingTasks;
    }
}