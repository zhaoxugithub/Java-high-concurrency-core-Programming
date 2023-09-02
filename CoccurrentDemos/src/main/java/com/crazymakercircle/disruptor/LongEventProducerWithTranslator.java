package com.crazymakercircle.disruptor;

import com.crazymakercircle.util.Print;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

class LongEventProducerWithTranslator {
    // 一个translator可以看做一个事件初始化器，publicEvent方法会调用它
    // 填充Event
    private static final EventTranslatorOneArg<LongEvent, Long> TRANSLATOR = (event, sequence, data) -> event.setValue(data);
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(Long data) {
        Print.tcfo("生产一个数据：" + data);
        ringBuffer.publishEvent(TRANSLATOR, data);
    }
}