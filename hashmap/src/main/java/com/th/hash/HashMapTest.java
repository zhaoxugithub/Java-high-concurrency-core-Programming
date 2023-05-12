package com.th.hash;

import org.junit.Test;

/**
 * @ClassName: HashMapTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/6 14:33
 * @Version 1.0
 */
public class HashMapTest {

    @Test
    public  void hashMapTest(){
        HashMap<Integer,Integer> hashMap = new HashMap<>();
        hashMap.put(4,104);  //无哈希碰撞
        hashMap.put(6,108); //无哈希碰撞
        hashMap.put(7,112); //有哈希碰撞,是链表
        hashMap.put(11,111); //有哈希碰撞,是链表
        hashMap.put(15,115); //有哈希碰撞,是链表
        hashMap.put(19,119); //有哈希碰撞,是链表
        hashMap.put(1,100); //无哈希碰撞
        hashMap.put(5,105); // 有哈希碰撞, 从这里开始转成链表
        hashMap.put(9,109);
        hashMap.put(29,129);
        hashMap.put(13,113);
        hashMap.put(17,117);
        hashMap.put(21,121);
        hashMap.put(25,125);

        hashMap.put(33,133);//先插入到链表,然后链表就转换成红黑树
       hashMap.put(37,137);
       hashMap.put(41,141);
        hashMap.put(45,145);
        hashMap.put(49,149);



      // System.out.println("hashMap get value:"+hashMap.get(19));

       System.out.println("hashMap remove value:"+hashMap.remove(49));

       // System.out.println("hashMap remove value:"+hashMap.remove(37));


        //System.out.println("hash containsValue:" +hashMap.containsValue(149));

      // System.out.println("hash containsKey:" +hashMap.containsKey(11));
    }
}