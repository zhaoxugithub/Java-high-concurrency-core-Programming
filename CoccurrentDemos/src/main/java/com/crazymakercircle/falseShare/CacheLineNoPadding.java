package com.crazymakercircle.falseShare;

public class CacheLineNoPadding implements SomeOneEntity {
    public volatile long x = 1L;

    @Override
    public void setValue(long value) {
        x = value;
    }
}