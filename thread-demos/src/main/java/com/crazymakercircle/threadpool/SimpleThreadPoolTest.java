package com.crazymakercircle.threadpool;

// 定义一个测试用例类
public class SimpleThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool threadPool = new SimpleThreadPool(1, 4, 2, 3,2000, new DiscardPolicy());
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new RunnableWrapper(i));
        }
        Thread.sleep(10_000);
        threadPool.shutdown();
    }
}