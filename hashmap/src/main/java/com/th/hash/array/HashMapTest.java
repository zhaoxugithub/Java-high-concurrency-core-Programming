package com.th.hash.array;

import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * @ClassName: HashMapTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/6 14:33
 * @Version 1.0
 */
public class HashMapTest {

    @Test
    public void hashMapTest() {
        HashMap<Integer, Integer> hashMap = new HashMap<>();

        Instant start = Instant.now();
       /* hashMap.put(4, 104);
        hashMap.put(6, 108);
        hashMap.put(7, 112);
        hashMap.put(11, 111);
        hashMap.put(15, 115);
        hashMap.put(19, 119);
        hashMap.put(1, 100);
        hashMap.put(5, 105);
        hashMap.put(9, 109);
        hashMap.put(29, 129);
        hashMap.put(13, 113);
        hashMap.put(17, 117);
        hashMap.put(21, 121);
        hashMap.put(25, 125);

        hashMap.put(33, 133);
        hashMap.put(37, 137);
        hashMap.put(41, 141);
        hashMap.put(45, 145);
        hashMap.put(49, 149);*/
        int MAX_VALUE_KEY = 10000;
        for (int i=0;i<=MAX_VALUE_KEY;i++){
            hashMap.put(i,(i+MAX_VALUE_KEY));
        }
        Instant finish  = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("保存Key-Value所需要的时间："+timeElapsed);


        Instant getstart = Instant.now();
        System.out.println("hashMap get value:" + hashMap.get(19));
        Instant getfinish  = Instant.now();
        long gettimeElapsed = Duration.between(getstart, getfinish).toMillis();
        System.out.println("获取Key-Value锁需要的时间："+gettimeElapsed);

        System.out.println("hashMap remove value:" + hashMap.remove(49));

        // System.out.println("hashMap remove value:"+hashMap.remove(37));


        System.out.println("hash containsValue:" +hashMap.containsValue(149));

         System.out.println("hash containsKey:" +hashMap.containsKey(11));
    }
}