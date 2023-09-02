package com.crazymakercircle.jvm;

import com.crazymakercircle.im.common.bean.User;
import com.crazymakercircle.util.Print;
import net.openhft.affinity.AffinityLock;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

public class ObjectTest {
    @Test
    public void testEmptyString() {
        String s = new String();
        String printable = ClassLayout.parseInstance(s)
                                      .toPrintable();
        Print.tcfo("printable = " + printable);
    }


    @Test
    public void testNotEmptyString() {
        String s = new String("100000000000AAAAAAAAAA");
        String printable = ClassLayout.parseInstance(s)
                                      .toPrintable();
        Print.tcfo("printable = " + printable);
    }

    @Test
    public void testJIT() {
        for (int i = 0; i < 200; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < 1000; j++) {
                new User();
            }
            long time = System.nanoTime() - start;
            // 输出统计结果
            System.out.printf("%d\t%d\n", i, time);
        }
    }
}

