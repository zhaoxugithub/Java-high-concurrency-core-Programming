package com.crazymakercircle.falseShare;

import com.lmax.disruptor.util.Util;
import sun.misc.Unsafe;

public class CacheLineWithUnsafe implements SomeOneEntity {
    // 模拟  disruptor 的 Sequence
    protected volatile long p1, p2, p3, p4, p5, p6, p7;
    public volatile long x = 1L;
    protected volatile long p9, p10, p11, p12, p13, p14, p15;
    private static final Unsafe UNSAFE;
    private static final long X_VALUE_OFFSET;

    static {
        UNSAFE = Util.getUnsafe();
        try {
            X_VALUE_OFFSET = UNSAFE.objectFieldOffset(CacheLineWithUnsafe.class.getDeclaredField("x"));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(long value) {
//        UNSAFE.putLongVolatile(this, X_VALUE_OFFSET, value);
        // 和平常写volatile比如  x=n;
        UNSAFE.putOrderedLong(this, X_VALUE_OFFSET, value);
        // 和平常非volatile比如  y=n;
    }
}