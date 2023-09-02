package com.crazymakercircle.completableFutureDemo;

import com.crazymakercircle.util.Print;
import com.crazymakercircle.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.crazymakercircle.util.ThreadUtil.sleepSeconds;

public class CompletableFutureDemo {
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Test
    public void thenApply() throws Exception {
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
                                                    try {
                                                        // 休眠2秒
                                                        Thread.sleep(2000);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    Print.tco("supplyAsync " + Thread.currentThread()
                                                                                     .getName());
                                                    return "hello ";
                                                }, executorService)
                                                .thenAccept(s -> {
                                                    try {
                                                        Print.tco("thenApply_test" + s + "world");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });
        Print.tco(Thread.currentThread()
                        .getName());
        while (true) {
            if (cf.isDone()) {
                Print.tco("CompletedFuture...isDown");
                break;
            }
        }
    }

    // 无返回值异步调用
    @Test
    public void runAsyncDemo() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepSeconds(1);// 模拟执行1秒
            Print.tco("run end ...");
        });
        // 等待异步任务执行完成,现时等待2秒
        future.get(2, TimeUnit.SECONDS);
    }

    // 有返回值异步调用
    @Test
    public void supplyAsyncDemo() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            sleepSeconds(1);// 模拟执行1秒
            Print.tco("run end ...");
            return System.currentTimeMillis() - start;
        });
        // 等待异步任务执行完成,现时等待2秒
        long time = future.get(2, TimeUnit.SECONDS);
        Print.tco("异步执行耗时（秒） = " + time / 1000);
    }

    @Test
    public void whenCompleteDemo() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepSeconds(1);// 模拟执行1秒
            Print.tco("抛出异常！");
            throw new RuntimeException("发生异常");
            // Print.tco("run end ...");
        });
        // 设置执行完成后的回调钩子
        future.whenComplete((t, action) -> Print.tco("执行完成！"));
        // 设置发生异常后的回调钩子
        future.exceptionally(t -> {
            Print.tco("执行失败！" + t.getMessage());
            return null;
        });
        future.get();
    }

    @Test
    public void handleDemo() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepSeconds(1);// 模拟执行1秒
            Print.tco("抛出异常！");
            throw new RuntimeException("发生异常");
            // Print.tco("run end ...");
        });
        // 设置执行完成后的回调钩子
        future.handle((BiFunction<Void, Throwable, Void>) (input, throwable) -> {
            if (throwable == null) {
                Print.tcfo("没有发生异常！");
            } else {
                Print.tcfo("sorry,发生了异常！");
            }
            return null;
        });
        future.get();
    }

    // 有返回值异步调用
    @Test
    public void threadPoolDemo() throws Exception {
        // 业务线程池
        ThreadPoolExecutor pool = ThreadUtil.getMixedTargetThreadPool();
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            Print.tco("run begin ...");
            long start = System.currentTimeMillis();
            sleepSeconds(1);// 模拟执行1秒
            Print.tco("run end ...");
            return System.currentTimeMillis() - start;
        }, pool);

        // 等待异步任务执行完成,现时等待2秒
        long time = future.get(2, TimeUnit.SECONDS);
        Print.tco("异步执行耗时（秒） = " + time / 1000);
    }

    @Test
    public void thenApplyDemo() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                                                              long firstStep = 10L + 10L;
                                                              Print.tco("firstStep outcome is " + firstStep);
                                                              return firstStep;
                                                          })
                                                          .thenApplyAsync(firstStepOutCome -> {
                                                              long secondStep = firstStepOutCome * 2;
                                                              Print.tco("secondStep outcome is " + secondStep);
                                                              return secondStep;
                                                          });
        long result = future.get();
        Print.tco(" future is " + future);
        Print.tco(" outcome is " + result);
    }

    @Test
    public void thenComposeDemo() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                                                              long firstStep = 10L + 10L;
                                                              Print.tco("firstStep outcome is " + firstStep);

                                                              return firstStep;
                                                          })
                                                          .thenCompose(firstStepOutCome -> CompletableFuture.supplyAsync(() -> {
                                                              long secondStep = firstStepOutCome * 2;
                                                              Print.tco("secondStep outcome is " + secondStep);
                                                              return secondStep;
                                                          }));
        long result = future.get();
        Print.tco(" outcome is " + result);
    }

    @Test
    public void thenCombineDemo() throws Exception {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            Integer firstStep = 10 + 10;
            Print.tco("firstStep outcome is " + firstStep);
            return firstStep;
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            Integer secondStep = 10 + 10;
            Print.tco("secondStep outcome is " + secondStep);
            return secondStep;
        });
        CompletableFuture<Integer> future3 = future1.thenCombine(future2, (step1OutCome, step2OutCome) -> step1OutCome * step2OutCome);
        Integer result = future3.get();
        Print.tco(" outcome is " + result);
    }

    @Test
    public void applyToEitherDemo() throws Exception {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            Integer firstStep = 10 + 10;
            Print.tco("firstStep outcome is " + firstStep);
            return firstStep;
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            Integer secondStep = 100 + 100;
            Print.tco("secondStep outcome is " + secondStep);
            return secondStep;
        });
        CompletableFuture<Integer> future3 = future1.applyToEither(future2, eitherOutCome -> eitherOutCome);
        Integer result = future3.get();
        Print.tco(" outcome is " + result);
    }

    @Test
    public void allOfDemo() throws Exception {
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> Print.tco("模拟异步任务1"));
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> Print.tco("模拟异步任务2"));
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> Print.tco("模拟异步任务3"));
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(() -> Print.tco("模拟异步任务4"));
        CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2, future3, future4);
        all.join();
    }

}
