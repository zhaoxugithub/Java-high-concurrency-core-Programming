package com.crazymakercircle.falseShare;

import com.crazymakercircle.util.Print;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class FalseShareTest {
    public static final long TURN = 100_000_000L;

    @Test
    public void testPaddedAtomicLong() {
        PaddedAtomicLong atomicLong = new PaddedAtomicLong();
        String printable = ClassLayout.parseInstance(atomicLong)
                                      .toPrintable();
        Print.tcfo("printable = " + printable);
    }

    @Test
    public void testContendedDemo() {
        ContendedDemo contendedDemo = new ContendedDemo();
        String printable = ClassLayout.parseInstance(contendedDemo)
                                      .toPrintable();
        Print.tcfo("printable = " + printable);
    }


    @Test
    public void testNoPadding() throws InterruptedException {
        SomeOneEntity[] entity = new CacheLineNoPadding[2];
        entity[0] = new CacheLineNoPadding();
        entity[1] = new CacheLineNoPadding();
        executeChangValue(entity);
    }

    @Test
    public void testPadding() throws InterruptedException {
        SomeOneEntity[] entity = new CacheLineWithPadding[2];
        entity[0] = new CacheLineWithPadding();
        entity[1] = new CacheLineWithPadding();
        executeChangValue(entity);
    }

    @Test
    public void testPaddingAndUnsafe() throws InterruptedException {
        SomeOneEntity[] entity = new CacheLineWithUnsafe[2];
        entity[0] = new CacheLineWithUnsafe();
        entity[1] = new CacheLineWithUnsafe();
        executeChangValue(entity);
    }

    private void executeChangValue(SomeOneEntity[] entity) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread threadA = new Thread(() -> {
            for (long i = 0; i < TURN; i++) {
                entity[0].setValue(i);
            }
            countDownLatch.countDown();
        }, "ThreadA");
        Thread threadB = new Thread(() -> {
            for (long i = 0; i < 100_000_000L; i++) {
                entity[1].setValue(i);
            }
            countDownLatch.countDown();
        }, "ThreadB");
        final long start = System.nanoTime();
        threadA.start();
        threadB.start();
        // 等待线程A、B执行完毕
        countDownLatch.await();
        final long end = System.nanoTime();
        System.out.println("耗时：" + (end - start) / 1_000_000 + "毫秒");
    }

    @org.junit.Test
    public void testLongAdder() throws InterruptedException {
        LongAdder longAdder = new LongAdder();

        Cell cell = new Cell(1);
        String printable = ClassLayout.parseInstance(cell)
                                      .toPrintable();
        Print.tcfo("printable = " + printable);
    }

}