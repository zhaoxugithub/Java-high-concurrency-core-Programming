package com.th.list.linked;

import org.junit.Test;

import java.util.Arrays;

/**
 * @ClassName: LinkTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/6 14:20
 * @Version 1.0
 */
public class LinkTest {

    @Test
    public  void singleLinkedListTest(){
        Integer data[] = new Integer[] {
                13,14,15,12,11,17,16,8,9,1
        };
        SingleLinkedList<Integer> singleLinkedList =  new SingleLinkedList<>();
        Arrays.stream(data).forEach(singleLinkedList ::add);
        singleLinkedList.toString();
    }
}