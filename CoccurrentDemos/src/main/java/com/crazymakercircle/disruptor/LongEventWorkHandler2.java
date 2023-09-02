package com.crazymakercircle.disruptor;

import com.crazymakercircle.util.Print;
import com.lmax.disruptor.WorkHandler;

/**
 * 类似于消费者
 * disruptor会回调此处理器的方法
 */
class LongEventWorkHandler2 implements WorkHandler<LongEvent> {
    @Override
    public void onEvent(LongEvent event) throws Exception {
        Print.tcfo(event.getValue());
    }
}