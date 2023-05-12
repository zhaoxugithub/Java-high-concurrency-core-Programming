package com.th.list.arry;

import org.junit.Test;

/**
 * @ClassName: ArrayListTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/5 10:16
 * @Version 1.0
 */
public class ArrayListTest {


    @Test
    public  void test1(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(10);
        arrayList.add(56);
        arrayList.add(23);
        arrayList.add(52);
        arrayList.add(41);
        arrayList.add(17);
        arrayList.add(45);
        arrayList.add(63);
        System.out.println("是否包含某个元素:"+arrayList.contains(10));

        System.out.println("数组打印:"+arrayList.toString());

        System.out.println("通过下标获取数组元素:"+arrayList.get(1));

        System.out.println("数组删除:"+arrayList.remove(1));
        System.out.println("数组打印:"+arrayList.toString());

    }

}