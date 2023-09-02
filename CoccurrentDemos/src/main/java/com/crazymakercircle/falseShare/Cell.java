package com.crazymakercircle.falseShare;

import com.lmax.disruptor.util.Util;

@sun.misc.Contended
public final class Cell {
    volatile long value;

    Cell(long x) {
        value = x;
    }

    boolean cas(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long valueOffset;

    static {
        UNSAFE = Util.getUnsafe();
        try {
            valueOffset = UNSAFE.objectFieldOffset(Cell.class.getDeclaredField("value"));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}