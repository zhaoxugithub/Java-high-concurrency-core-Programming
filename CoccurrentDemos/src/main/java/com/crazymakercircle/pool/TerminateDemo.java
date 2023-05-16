package com.crazymakercircle.pool;

import com.crazymakercircle.cocurrent.TargetTask;
import com.crazymakercircle.util.Print;
import com.crazymakercircle.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TerminateDemo {
    @Test
    public void testShutdownNow () throws InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        Print.tcfo("向固定大小线程池， 提交10个任务，每个任务 500ms!");
        for (int i = 0; i < 10; i++) {
            //执行10个任务
            threadPool.execute(new TargetTask());
        }
        //等100 ms
        ThreadUtil.sleepMilliSeconds(100);

        // 粗暴关闭线程池
        Print.tcfo(" 粗暴关闭线程池!");
        threadPool.shutdownNow();

        //等着看结果
        ThreadUtil.sleepMilliSeconds(100000);

    }


 @Test
    public void testAwaitTermination() throws InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            //执行10个任务
            threadPool.execute(new TargetTask());
        }
        try {
            // 柔和关闭线程池
            threadPool.shutdown();
            // awaitTermination + shutdown 配合使用
            // 等等  20S
            if (!threadPool.awaitTermination(20, TimeUnit.SECONDS)) {
                Print.tcfo(" 到达指定时间，还有线程没执行完，不再等待，关闭线程池!");
                threadPool.shutdownNow();
            }
        } catch (Throwable e) {
            threadPool.shutdownNow();
            e.printStackTrace();
        }

    }


}
