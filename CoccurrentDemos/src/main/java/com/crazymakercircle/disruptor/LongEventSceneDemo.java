package com.crazymakercircle.disruptor;

import com.crazymakercircle.util.Print;
import com.crazymakercircle.util.ThreadUtil;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static net.openhft.affinity.AffinityStrategies.DIFFERENT_CORE;
import static net.openhft.affinity.AffinityStrategies.SAME_SOCKET;

public class LongEventSceneDemo {
    public static void handleEvent1(LongEvent event, long sequence, boolean endOfBatch) {
        Print.tcfo(event.getValue());
    }

    public static void handleEvent2(LongEvent event, long sequence, boolean endOfBatch) {
        Print.tcfo(event.getValue());
    }

    public static void handleEvent3(LongEvent event, long sequence, boolean endOfBatch) {
        Print.tcfo(event.getValue());
    }

    public static void handleEvent4(LongEvent event, long sequence, boolean endOfBatch) {
        Print.tcfo(event.getValue());
    }

    @org.junit.Test
    public void testSimpleProducerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(LongEventSceneDemo::handleEvent1, LongEventSceneDemo::handleEvent1, LongEventSceneDemo::handleEvent1);
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (long i = 0; true; i++) {/*发布事件*/
                    producer.onData(i);
                    ThreadUtil.sleepSeconds(1);
                }
            }
        };
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }


    @org.junit.Test
    public void testMultiProducerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.MULTI,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler());
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 3生产者，并发生产数据
        for (int l = 0; l < 3; l++) {
            LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
            Thread thread = new Thread(() -> {
                for (long i = 0; true; i++) {/*发布事件*/
                    producer.onData(i);
                    ThreadUtil.sleepSeconds(1);
                }
            });
            thread.setName("producer thread " + l);
            thread.start();
        }
        ThreadUtil.sleepSeconds(5);

    }

    @org.junit.Test
    public void testMultiConsumerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.MULTI,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler());
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                // 发布事件
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }

    @org.junit.Test
    public void testMiltiSerialConsumerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(LongEventSceneDemo::handleEvent1)
                 .then(LongEventSceneDemo::handleEvent2)
                 .then(LongEventSceneDemo::handleEvent3);
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {/*发布事件*/
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }


    @org.junit.Test
    public void testCurrentThenSerialConsumerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(LongEventSceneDemo::handleEvent1, LongEventSceneDemo::handleEvent2)
                 .then(LongEventSceneDemo::handleEvent3);

//        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler());

        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }

    @org.junit.Test
    public void testlinkSerialConsumerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(LongEventSceneDemo::handleEvent1)
                 .then(LongEventSceneDemo::handleEvent2);

        disruptor.handleEventsWith(LongEventSceneDemo::handleEvent3)
                 .then(LongEventSceneDemo::handleEvent4);
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }

    @Test
    public void testIsolateDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler());
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler2(), new LongEventWorkHandler2());
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }

    @org.junit.Test
    public void testChannelModelDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWithWorkerPool(new LongEventWorkHandler(), new LongEventWorkHandler())
                 .thenHandleEventsWithWorkerPool(new LongEventWorkHandler2(), new LongEventWorkHandler2());
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }


    @org.junit.Test
    public void testHexagonConsumerDisruptorWithMethodRef() {
        // 消费者线程池
        Executor executor = Executors.newCachedThreadPool();
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, executor, ProducerType.SINGLE,  // 多个生产者
                new YieldingWaitStrategy());

        EventHandler consumer1 = new LongEventHandlerWithName("consumer 1");
        EventHandler consumer2 = new LongEventHandlerWithName("consumer 2");
        EventHandler consumer3 = new LongEventHandlerWithName("consumer 3");
        EventHandler consumer4 = new LongEventHandlerWithName("consumer 4");
        EventHandler consumer5 = new LongEventHandlerWithName("consumer 5");
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler

        disruptor.handleEventsWith(consumer1, consumer2);
        disruptor.after(consumer1)
                 .handleEventsWith(consumer3);
        disruptor.after(consumer2)
                 .handleEventsWith(consumer4);
        disruptor.after(consumer3, consumer4)
                 .handleEventsWith(consumer5);
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(5);
    }


    @org.junit.Test
    public void testConsumerDisruptorWithAffinityThreadFactory() {
        AffinityThreadFactory affinityThreadFactory = new AffinityThreadFactory("affinityWorker", DIFFERENT_CORE, SAME_SOCKET);
        // 消费者线程池
        // 环形队列大小，2的指数
        int bufferSize = 1024;
        // 构造  分裂者 （事件分发者）
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(LongEvent::new, bufferSize, affinityThreadFactory, ProducerType.SINGLE,  // 单个生产者
                new BusySpinWaitStrategy());
        EventHandler consumer1 = new LongEventHandlerWithName("consumer 1");
        EventHandler consumer2 = new LongEventHandlerWithName("consumer 2");
        // 连接 消费者 处理器
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(consumer1, consumer2);
        // 开启 分裂者（事件分发）
        disruptor.start();
        // 获取环形队列，用于生产 事件
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        // 1生产者，并发生产数据
        LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
        Thread thread = new Thread(() -> {
            for (long i = 0; true; i++) {
                producer.onData(i);
                ThreadUtil.sleepSeconds(1);
            }
        });
        thread.start();
        ThreadUtil.sleepSeconds(Integer.MAX_VALUE);
    }
}
