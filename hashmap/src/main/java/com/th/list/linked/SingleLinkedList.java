package com.th.list.linked;

import com.th.list.AbstractList;

/**
 * @ClassName: singleLinkedList
 * @Description: 单向链表
 * @Author: 唐欢
 * @Date: 2023/5/5 11:13
 * @Version 1.0
 */
public class SingleLinkedList<E> extends AbstractList<E> {
    private Node<E> first;


    public SingleLinkedList() {
        first = new Node<E>(null,null);
    }

    @Override
    public void clear() {
        size = 0;
        first = null;
    }

    /*
     * 最好：O(1)
     * 最坏：O(n)
     * 平均：O(n)
     */
    @Override
    public E get(int index) {
        return node(index).element;
    }

    /*
     * 最好：O(1)
     * 最坏：O(n)
     * 平均：O(n)
     */
    @Override
    public E set(int index, E element) {
        Node<E> node = node(index);
        E old = node.element;
        node.element = element;
        return old;
    }

    /*
     * 最好：O(1)
     * 最坏：O(n)
     * 平均：O(n)
     */
    @Override
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        Node<E> prev = index == 0 ? first : node(index - 1);
        prev.next = new Node<>(element, prev.next);

        size++;
    }

    /*
     * 最好：O(1)
     * 最坏：O(n)
     * 平均：O(n)
     */
    @Override
    public E remove(int index) {

        rangeCheck(index);

        Node<E> prev = index == 0 ? first : node(index - 1);
        Node<E> node = prev.next;
        prev.next = node.next;
        size--;
        return node.element;
    }

    @Override
    public int indexOf(E element) {
        if (element == null) {
            Node<E> node = first;
            for (int i = 0; i < size; i++) {
                if (node.element == null) return i;

                node = node.next;
            }
        } else {
            Node<E> node = first;
            for (int i = 0; i < size; i++) {
                if (element.equals(node.element)) return i;

                node = node.next;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    /**
     * 获取index位置对应的节点对象
     * @param index
     * @return
     */
    private Node<E> node(int index) {
        rangeCheck(index);

        Node<E> node = first.next;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("size=").append(size).append(", [");
        Node<E> node = first.next;
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                string.append("->");
            }

            string.append(node.element);

            node = node.next;
        }
        string.append("]");
        System.out.println( string.toString());
        return string.toString();
    }

    private static class Node<E> {
        E element;
        Node<E> next;
        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }
    }
}