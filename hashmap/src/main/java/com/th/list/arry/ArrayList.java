package com.th.list.arry;

import com.th.list.AbstractList;

/**
 * @ClassName: ArrayList
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/5 10:11
 * @Version 1.0
 */
public class ArrayList<E> extends AbstractList<E> {

    /**
     * 所有的元素
     */
    private E[] elements;

    /**
     * 默认容量
     */
    private static final int DEFAULT_CAPACITY = 10;
    /**
     * 元素未找到默认返回值
     */
    private static final int ELEMENT_NOT_FOUND = -1;

    public ArrayList(int capaticy) {
        capaticy = (capaticy < DEFAULT_CAPACITY) ? DEFAULT_CAPACITY : capaticy;
       //创建默认容量的数组
        elements = (E[]) new Object[capaticy];
    }

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * 清除所有元素
     */
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }


    /**
     * 获取index位置的元素
     * @param index
     * @return
     */
    @Override
    public E get(int index) {
        rangeCheck(index);
        return elements[index];
    }

    /**
     * 设置index位置的元素
     * @param index
     * @param element
     * @return 原来的元素ֵ
     */
    @Override
    public E set(int index, E element) {
        rangeCheck(index);

        E old = elements[index];
        elements[index] = element;
        return old;
    }

    /**
     * 在index位置插入一个元素
     * @param index
     * @param element
     */
    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacity(size + 1);

        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
    }

    /**
     * 删除index位置的元素
     * @param index
     * @return
     */
    @Override
    public E remove(int index) {
        rangeCheck(index);

        E old = elements[index];
        for (int i = index + 1; i < size; i++) {
            elements[i - 1] = elements[i];
        }
        elements[--size] = null;
        return old;
    }

    /**
     * 查看元素的索引
     * @param element
     * @return
     */
    @Override
    public int indexOf(E element) {
        if (element == null) {  // 1
            for (int i = 0; i < size; i++) {
                if (elements[i] == null){
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(elements[i])){
                    return i; // n
                }
            }
        }
        return ELEMENT_NOT_FOUND;
    }


    /**
     * 保证要有capacity的容量,
     * 什么情况下适合使用均摊复杂度?
     *      经过连续的多次复杂度比较低的情况后，出现个别复杂度比较高的情况
     * @param capacity
     */
    private void ensureCapacity(int capacity) {
        int oldCapacity = elements.length;
        if (oldCapacity >= capacity) return;

        // 新容量为旧容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        E[] newElements = (E[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }
        elements = newElements;

        System.out.println(oldCapacity + "扩容为" + newCapacity);
    }

    /**
     * 缩容
     * 如果内存使用比较紧张，动态数组有比较多的剩余空间，可以考虑进行缩容操作
     * 比如剩余空间占总容量的一半时，就进行缩容
     */
    private  void trim(){
        int oldCapacity = elements.length;

        int newCapacity = oldCapacity >> 1;

        if(size >= (newCapacity) || oldCapacity <DEFAULT_CAPACITY){
            return;
        }
        //剩余空间还很多
        E[] newElements = (E[]) new Object[newCapacity];
        for (int i =0;i<size;i++){
            newElements[i] = elements[i];
        }
        elements = newElements;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("size=").append(size).append(", [");
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                string.append(", ");
            }

            string.append(elements[i]);

        }
        string.append("]");
        return string.toString();
    }
}