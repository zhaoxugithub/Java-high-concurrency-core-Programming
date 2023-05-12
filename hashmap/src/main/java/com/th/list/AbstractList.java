package com.th.list;

/**
 * @ClassName: AbstractList
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/5 10:39
 * @Version 1.0
 */
public abstract class AbstractList<E> implements List<E> {
    /**
     * 元素的数量
     */
    protected int size;



    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E element) {
        return indexOf(element) != ELEMENT_NOT_FOUND;
    }

    @Override
    public void add(E element) {
        add(size, element);
    }

    protected void outOfBounds(int index) {
        throw new IndexOutOfBoundsException("Index:" + index + ", Size:" + size);
    }

    protected void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            outOfBounds(index);
        }
    }

    protected void rangeCheckForAdd(int index) {
        if (index < 0 || index > size) {
            outOfBounds(index);
        }
    }



    @Override
    public E set(int index, E element) {
        return null;
    }


}