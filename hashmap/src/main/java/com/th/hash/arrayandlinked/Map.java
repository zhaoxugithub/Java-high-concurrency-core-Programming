package com.th.hash.arrayandlinked;

/**
 * @ClassName: Map
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/5 9:55
 * @Version 1.0
 */
public interface Map<K,V> {
    int size();

    boolean isEmpty();

    void clear();

    V put(K key,V value);

    V remove(K key);

    V get(K key);

    boolean containsKey(K key);

    boolean containsValue(V value);

    boolean equals(Object o);

    int hashCode();

    interface Entry<K, V> {
        K getKey();
        V getValue();
        V setValue(V value);
        int hashCode();
        boolean equals(Object o);
    }


}
