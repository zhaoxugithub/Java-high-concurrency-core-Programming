package com.crazymakercircle.completableFutureDemo;

import com.crazymakercircle.util.Print;

import java.util.concurrent.CompletableFuture;

import static com.crazymakercircle.util.ThreadUtil.sleepSeconds;

public class DrinkTea {
    private static final int SLEEP_GAP = 3;// 等待3秒

    public static void main(String[] args) {
        // 任务 1
        CompletableFuture<Boolean> washJob = CompletableFuture.supplyAsync(() -> {
            Print.tcfo("洗茶杯");
            // 线程睡眠一段时间，代表清洗中
            sleepSeconds(SLEEP_GAP);
            Print.tcfo("洗完了");
            return true;
        });

        // 任务 2
        CompletableFuture<Boolean> hotJob = CompletableFuture.supplyAsync(() -> {
            Print.tcfo("洗好水壶");
            Print.tcfo("烧开水");
            // 线程睡眠一段时间，代表烧水中
            sleepSeconds(SLEEP_GAP);
            Print.tcfo("水开了");
            return true;
        });

        // 任务 3：任务 1 和任务 2 完成后执行：泡茶
        CompletableFuture<String> drinkJob = washJob.thenCombine(hotJob, (hotOk, washOK) -> {
            if (hotOk && washOK) {
                Print.tcfo("泡茶喝，茶喝完");
                return "茶喝完了";
            }
            return "没有喝到茶";
        });

        // 等待任务 3 执行结果
        Print.tco(drinkJob.join());
    }
}

