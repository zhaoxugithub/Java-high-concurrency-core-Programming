package com.crazymakercircle.disruptor;

import com.lmax.disruptor.RingBuffer;

//  事件生产者：业务代码
// 通过从 环形队列中 获取 序号， 通过序号获取 对应的 事件对象， 将数据填充到 事件对象，再通过 序号将 事件对象 发布出去。
class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData用来发布事件，每调用一次就发布一次事件事件
     * 它的参数会通过事件传递给消费者
     *
     * @param data
     */
    public void onData(long data) {
        // step1：通过从 环形队列中 获取 序号
        // 可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
        long sequence = ringBuffer.next();
        try {
            // step2: 通过序号获取 对应的 事件对象， 将数据填充到 事件对象，
            // 用上面的索引，取出一个空的事件用于填充
            LongEvent event = ringBuffer.get(sequence);// for the sequence
            event.setValue(data);
        } finally {
            // step3: 再通过 序号将 事件对象 发布出去。
            // 发布事件
            ringBuffer.publish(sequence);
        }
    }
}