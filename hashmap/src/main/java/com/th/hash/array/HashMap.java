package com.th.hash.array;


import java.util.Objects;

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
    private int DEFAULT_CAPACITY = 1 << 2;
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
        if ((parent = tab[i = hash]) == null) { //无hash碰撞，在当前下标位置直接插入
            System.out.println("下标:" + i + ",数组插入的key:" + key + ",value:" + value);
            //如果没有hash碰撞,就直接插入数组中
            tab[i] = new Node<>(hash, key, value, null);
            ++size;
        } else {  // 有hash碰撞的时候，就采用线性探查法解决hash碰撞：fi=（f(key)+i)%4

            if (i == (n - 1)) {
                //若已是下标最大值，就从头开始查找空位置插入
                for (int j = 0; j < i; j++) {
                    if (tab[j] == null) {
                        System.out.println("已最后一个下标，从0下标开始找,下标为：" + j + ",数组插入的key:" + key + ",value:" + value);
                        tab[j] = new Node<>(hash, key, value, null);
                        ++size;
                        break;
                    }
                }
            } else { // 若不是下标最大值，那就从当前下标往后查找空位置插入

                for (int index = 1; index < n - i - 1; index++) {
                    //先往后查找，若往后查找有空位，就直接插入，
                    if (tab[i + index] == null) {
                        System.out.println("从当前下标往后找,下标为：" + (i + index) + ",数组插入的key:" + key + ",value:" + value);
                        tab[i + index] = new Node<>(hash, key, value, null);
                        ++size;
                        break;
                    }
                }
            }
        }

        // 判断当前数组是否需要扩容
        if (size > threshold) {
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
        //数组未初始化，对数组进行初始化
        if (table == null || table.length == 0) {
            table = new Node[DEFAULT_CAPACITY];
            return table;
        }

        // 数组已初始化，旧容量
        oldCapacity = table.length;
        // 扩容后新的数组容量
        int newCapacity = 0;
        // 如果数组的长度 == 容量
        if (size > threshold) {
            // 新容量为旧容量的1.5倍
            newCapacity = oldCapacity + (oldCapacity >> 1);

            //数组扩容阈值= 新容量*负载因子（0.75）
            threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
            //创建一个新数组
            Node<K, V>[] newTable = new Node[newCapacity];
            // 把原来数组中的元素放到新数组中
            for (int i = 0; i < size; i++) {
                newTable[i] = table[i];
            }
            table = newTable;
            System.out.println(oldCapacity + "扩容为" + newCapacity);
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
            {
                //如果不是就for循环查找
                for (int i = 0; i < table.length; i++) {
                    if (table[index = i].hash == hash && ((k = table[index ].key) == key || (key != null && key.equals(k)))) {
                        oldValue = parent.value;
                       /* for (int j = index + 1; j < table.length; j++) {
                            table[j - 1] = table[j];
                        }
                        --size;
                        table[(table.length - 1)] = null;*/
                        table[index] = null;
                        return oldValue;
                    }
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
     * 通过key值在数组中查找value值
     *
     * @param hash
     * @param key
     * @return
     */
    private Node<K, V> getNode(int hash, Object key) {
        K k;

        //如果不是就for循环查找
        for (int i = 0; i < table.length; i++) {
            if (table[i].hash == hash && ((k = table[i].key) == key || (key != null && key.equals(k)))) {
                return table[i];
            }
        }
        return null;
    }


    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            if (node != null&&(node.value == value || (value != null && node.value.equals(value)))) {
                return true;
            }
        }

        return false;
    }


    private int hash(Object key) {
        if (key == null) return 0;

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