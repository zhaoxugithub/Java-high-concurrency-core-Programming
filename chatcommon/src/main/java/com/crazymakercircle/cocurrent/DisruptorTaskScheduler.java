/**
 * Created by 尼恩 at 疯狂创客圈
 */

package com.crazymakercircle.cocurrent;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

@Slf4j
public class DisruptorTaskScheduler {
    private static final int RING_BUFFER_SIZE = 1024 * 1024;
    private static final int CONCURRENT_SIZE = 10;
    private static DisruptorTaskScheduler inst = new DisruptorTaskScheduler();
    private TaskScheduler scheduler = new TaskScheduler();

    private DisruptorTaskScheduler() {
        scheduler.start();
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */
    public static void add(Runnable executeTask) {
        inst.scheduler.sendNotify(executeTask);
    }

    @Data
    public class NotifyEvent {
        private Runnable target;
    }

    //   消息工厂,用于生产消息
    public class NotifyEventFactory implements EventFactory {
        @Override
        public Object newInstance() {
            return new NotifyEvent();
        }
    }

    // 创建消费者，此处用于处理业务逻辑
    public class NotifyEventHandler implements EventHandler<NotifyEvent>, WorkHandler<NotifyEvent> {
        @Override
        public void onEvent(NotifyEvent notifyEvent, long l, boolean b) throws Exception {
            this.onEvent(notifyEvent);
            log.debug("接收任务 ==[{}]!", notifyEvent.getTarget());
        }

        @Override
        public void onEvent(NotifyEvent notifyEvent) throws Exception {
            notifyEvent.getTarget().run();
            log.debug("执行完成 ==[{}]!", notifyEvent.getTarget());
        }
    }

    //    自定义异常
    public class NotifyEventHandlerException implements ExceptionHandler {
        @Override
        public void handleEventException(Throwable throwable, long sequence, Object event) {
            throwable.fillInStackTrace();
            log.error("process  error ==[{}] ", throwable.getMessage());
        }

        @Override
        public void handleOnStartException(Throwable throwable) {
            log.error("start disruptor error ==[{}]!", throwable.getMessage());
        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {
            log.error("shutdown disruptor error ==[{}]!", throwable.getMessage());
        }
    }

    // 对Disruptor进行初始化
    public class TaskScheduler {
        private boolean isRunning = false;
        private Disruptor<NotifyEvent> disruptor;

        public void destroy() throws Exception {
            disruptor.shutdown();
        }

        synchronized public void start() {
            if (!isRunning) {
                disruptor = new Disruptor<NotifyEvent>(
                        new NotifyEventFactory(),
                        RING_BUFFER_SIZE,
                        Executors.defaultThreadFactory(),
                        ProducerType.MULTI,
//                        ProducerType.SINGLE,
                        new BlockingWaitStrategy());
                disruptor.setDefaultExceptionHandler(new NotifyEventHandlerException());
                // 创建10个消费者来处理同一个生产者发的消息(这10个消费者不重复消费消息)
                NotifyEventHandler[] consumers = new NotifyEventHandler[CONCURRENT_SIZE];
                for (int i = 0; i < consumers.length; i++) {
                    consumers[i] = new NotifyEventHandler();
                }
                // disruptor.handleEventsWith(consumers);
                // 并发消费
                disruptor.handleEventsWithWorkerPool(consumers);
                disruptor.start();
                isRunning = true;
            }
        }

        public void sendNotify(Runnable target) {
            RingBuffer<NotifyEvent> ringBuffer = disruptor.getRingBuffer();
            ringBuffer.publishEvent(new EventTranslatorOneArg<NotifyEvent, Runnable>() {
                @Override
                public void translateTo(NotifyEvent event, long sequence, Runnable target) {
                    event.setTarget(target);
                }
            }, target);
            // lambda式写法，
            // ringBuffer.publishEvent((event, sequence, data) -> event.setMessage(data), message);
        }
    }
}
