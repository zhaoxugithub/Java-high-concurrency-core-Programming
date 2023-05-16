package com.th.hash.arrayandlinked;


import com.th.tree.printer.BinaryTreeInfo;
import com.th.tree.printer.BinaryTrees;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * @ClassName: HashMap
 * @Description: 数组每个槽位看成一个链表结点, 有子节点的链表(链表长度超过7时转成红黑树)/没有子节点的链表
 * @Author: 唐欢
 * @Date: 2023/5/5 9:59
 * @Version 1.0
 */
public class HashMap<K, V> implements Map<K, V> {
    //数组默认初始容16
    //private int DEFAULT_CAPACITY = 1 << 4;
    private int DEFAULT_CAPACITY = 4;
    //数组最大容量2^30
    private int MAX_CAPACITY = 1 << 30;


    /**
     * 加载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 数组扩容的阈值= loadFactorx 容量(capacity)
     */
    int threshold;

    // 数组
    Node<K, V>[] table;

    //数组长度
    private int size;



    public HashMap() {
        threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
    }



    /**
     * 规定传入对象的比较规则
     *
     * @param k1 第一个对象
     * @param k2 第二个对象
     * @return 0表示e1 =e2，大于0表示 e1 > e2,小于0表示 e1 < e2
     */
    protected int compare(Object k1, Object k2) {

        return ((Comparable<Object>) k1).compareTo(k2);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        if (size == 0) {
            return;
        }
        size = 0;
        for (int i = 0; i < size; i++) {
            table[i] = null;
        }
    }

    /**
     * 插入节点
     *
     * @param key   key值
     * @param value value值
     * @return
     */
    @Override
    public V put(K key, V value) {
        //通过key计算hash值
        int hash = hash(key);

        //数组
        Node<K, V>[] tab;
        // 数组长度
        int n;

        // 数组的位置,即hash槽位
        int i;

        //根据数组长度和哈子自来寻址
        Node<K, V> parent;

        if ((tab = table) == null || (n = tab.length) == 0) {
            //第一次put的时候,调用ensureCapacity创建数组
            tab = ensureCapacity();
            n = tab.length;
        }

        // 开始时插入元素
        if ((parent = tab[i = hash]) == null) {
            System.out.println("下标为："+i+"数组插入的key:" + key + ",value:" + value);
            //如果没有hash碰撞,就直接插入数组中
            tab[i] = new Node<>(hash, key, value, null);
            ++size;
        } else { //有哈希碰撞时,采用链表存储
            // 下一个子结点
            Node<K, V> next;
            K k;
            System.out.println("下标为："+i+"有哈希碰撞的key:" + key + ",value:" + value);
            if (parent.hash == hash
                    && ((k = parent.key) == key || (key != null && key.equals(k)))) {
                // 哈希碰撞,且节点已存在,直接替换数组元素
                next = parent;
            } else {
                System.out.println("下标为："+i+"链表插入的key:" + key + ",value:" + value);

                // 哈希碰撞, 链表插入
                for (int linkSize = 0; ; ++linkSize) {
                    // System.out.println("linkSize="+linkSize+",node:"+parent);
                    //如果当前结点的下一个结点为null,就直接插入
                    if ((next = parent.next) == null) {
                        System.out.println("new链表长度为:" + linkSize);
                        parent.next = new Node<>(hash, key, value, null);
                        break;
                    }
                    if (next.hash == hash
                            && ((k = next.key) == key || (key != null && key.equals(k)))) {
                        //如果节点已经存在,直接跳出for循环
                        break;
                    }
                    parent = next;
                }
                printLinked(hash);
            }
        }
        if (size > threshold ){
            //扩容操作
            ensureCapacity();
        }
        return value;
    }


    /**
     * 数组扩容
     */
    private Node<K, V>[] ensureCapacity() {
        int oldCapacity = 0;
        if (table == null || table.length == 0) {
            table = new Node[DEFAULT_CAPACITY];
            return table;
        }
        oldCapacity = table.length;
        int newCapacity = 0;
        // 如果数组的长度 == 容量
        if (size > threshold) {
            // 新容量为旧容量的1.5倍
            newCapacity = oldCapacity + (oldCapacity >> 1);

            threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);

            Node<K, V>[] newTable = new Node[newCapacity];
            // 把原来数组中的元素放到新数组中
            for (int i = 0; i < size; i++) {
                newTable[i] = table[i];
            }
            // System.out.println(oldCapacity + "扩容为" + newCapacity);
            table = newTable;

        }


        return table;
    }

    /**
     * 结点删除
     *
     * @param key
     * @return
     */
    @Override
    public V remove(K key) {
        int hash = hash(key);
        //数组
        Node<K, V>[] tab;
        Node<K, V> parent;
        K k;
        int index;
        V oldValue = null;
        //节点是存在的
        if ((parent = table[index =  hash]) != null) {
            if (parent.next != null) { //链表删除
                Node<K, V> node = parent;
                Node<K, V> preNode = null;
                for (int linkSize = 0; ; ++linkSize) {

                    if (node.hash == hash
                            && ((k = node.key) == key || (key != null && key.equals(k)))) {
                        if (linkSize == 0) {
                            //如果是第一个结点,就把第二个结点挂载到table中
                            oldValue = node.value;
                            table[index] = node.next;
                        } else {
                            if (preNode.next.next == null) {
                                //删除的如是尾节点, 就把尾节点置为null
                                oldValue = preNode.next.value;
                                preNode.next = null;


                            } else {

                                oldValue = preNode.next.value;
                                preNode.next = preNode.next.next;
                            }
                        }
                       // size--;
                        break;
                    }
                    //删除结点的前结点
                    preNode = node;
                    if ((node = node.next) == null) {
                        break;
                    }

                }
                System.out.println("链表删除元素:" + key);
                printLinked(hash);
            } else { //数组删除
                if (parent.hash == hash
                        && ((k = parent.key) == key || (key != null && key.equals(k)))) {
                    oldValue = parent.value;
                   /* for (int i = index + 1; i < table.length; i++) {
                        table[i - 1] = table[i];
                    }
                    --size;
                    table[(table.length - 1)] = null;*/
                    table[index] = null;
                    return oldValue;
                }


            }
        }
        return oldValue;
    }


    @Override
    public V get(Object key) {
        Node<K, V> e;

        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * 通过key值在数组/链表查找value值
     *
     * @param hash
     * @param key
     * @return
     */
    private Node<K, V> getNode(int hash, Object key) {
        //数组
        Node<K, V>[] tab;

        //数组长度
        int n;
        // (n-1)$hash 获取该key对应的数据节点的hash槽位,即链表的根结点
        Node<K, V> parent;

        //root的子节点
        Node<K, V> next;

        K k;

        //如果数组为空,并且长度为空, hash槽位对应的节点为空,就返回null
        if ((tab = table) != null && (n = table.length) > 0
                && (parent = tab[ hash]) != null) {
            // 如果计算出来的hash槽位所对应的结点hash值等于hash值,结点的key=查找key值,
            // 返回hash槽位对应的结点,即数组
            if (parent.hash == hash && ((k = parent.key) == key || (key != null && key.equals(k)))) {
                return parent;
            }
            //如果不在根结点,在子结点
            if ((next = parent.next) != null) {

                //在链表中查找,需要通过循环一个个往下查找
                while (next != null) {
                    if (next.hash == hash && ((k = next.key) == key || (key != null && key.equals(k)))) {
                        return next;
                    }
                    next = next.next;
                }

            }

        }
        return null;
    }


    /**
     * 链表打印
     *
     * @param hash
     */
    public String printLinked(int hash) {
        StringBuilder string = new StringBuilder();
        string.append(" [");
        Node<K, V> node = table[hash];
        int n = 0;
        for (int linkSize = 0; ; ++linkSize) {

            if (linkSize != 0) {
                string.append("->");
                string.append("("+node.key + "," + node.value+")");
                node = node.next;
                ++n;
            }
            if (node == null) {
                break;
            }
        }
        string.append("],链表长度："+n);
        System.out.println(string.toString());
        return string.toString();
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            if ( node != null) {
                // 链表中循环
                do {
                    if (node.value == value || (value != null && node.value.equals(value))) {
                        return true;
                    }

                } while ((node = node.next) != null);
            }
        }
        return false;
    }


    private int hash(Object key) {
        if (key == null) return 0;
        // int hash = key.hashCode();
        // return hash ^ (hash >>> 16);
        int hash = (Integer) key % 4;
        return hash;
    }


    /**
     * 链表结点
     *
     * @param <K>
     * @param <V>
     */
    static class Node<K, V> implements Entry<K, V> {
        int hash;
        K key;
        V value;
        Node<K, V> next;


        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Map.Entry) {
                Node<K, V> e = (Node<K, V>) obj;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return key + " = " + value;
        }
    }

}