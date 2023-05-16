package com.crazymakercircle.jvm;

import com.crazymakercircle.util.ThreadUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class JvmMemoryTest {

    static class TestMemory {
        int foo;
    }

    //声明缓存对象
    private static final Map map = new HashMap();

    public static void main(String args[]){
        //为dump出堆提供时间
        ThreadUtil.sleepSeconds(10);

        //循环添加对象到缓存
        for(int i=0; i<1_000_000;i++){
            TestMemory t = new TestMemory();
            map.put("key"+i,t);
        }
        System.out.println("first");

        //为dump出堆提供时间
        ThreadUtil.sleepSeconds(10);


        for(int i=0; i<1_000_000;i++){
            TestMemory t = new TestMemory();
            map.put("key"+i,t);
        }
        System.out.println("second");

        //为dump出堆提供时间
        ThreadUtil.sleepSeconds(10);


        for(int i=0; i<1_000_000;i++){
            TestMemory t = new TestMemory();
            map.put("key"+i,t);
        }
        System.out.println("third");
        //为dump出堆提供时间
        ThreadUtil.sleepSeconds(10);

        for(int i=0; i<1_000_000;i++){
            TestMemory t = new TestMemory();
            map.put("key"+i,t);
        }
        System.out.println("forth");

        System.out.println("done");
        while (true) {  //为dump出堆提供时间
            ThreadUtil.sleepSeconds(10);
        }
    }

}


