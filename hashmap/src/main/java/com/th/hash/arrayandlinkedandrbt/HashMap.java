package com.th.hash.arrayandlinkedandrbt;


import com.th.tree.printer.BinaryTreeInfo;
import com.th.tree.printer.BinaryTrees;

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
    private static final boolean RED = false;
    private static final boolean BLACK = true;
    //数组默认初始容16
    //private int DEFAULT_CAPACITY = 1 << 4;
    private int DEFAULT_CAPACITY = 4;
    //数组最大容量2^30
    private int MAX_CAPACITY = 1 << 30;

    //链表长度到达8时转成红黑树
    private static final int TREEIFY_THRESHOLD = 8;

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

        return ((java.lang.Comparable<Object>) k1).compareTo(k2);
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
            System.out.println("数组插入的key:" + key + ",value:" + value);
            //如果没有hash碰撞,就直接插入数组中
            tab[i] = new Node<>(hash, key, value, null);
            ++size;

        } else { //有哈希碰撞时,需要判断是红黑树还是链表
            // 下一个子结点
            Node<K, V> next;
            K k;
            System.out.println("下标："+i+"，有哈希碰撞的key:" + key + ",value:" + value);
            if (parent.hash == hash
                    && ((k = parent.key) == key || (key != null && key.equals(k)))) {
                // 哈希碰撞,且节点已存在,直接替换数组元素
                next = parent;
            } else if (parent instanceof RBTreeNode) {
                // 如果是红黑树节点,就插入红黑树节点
                System.out.println("下标："+i+"，往红黑树中插入的key:" + key + ",value:" + value);
                //先找到root根节点
                int index = (tab.length - 1) & hash;
                //取出红黑树的根结点
                RBTreeNode<K, V> root = (RBTreeNode<K, V>) tab[index];


                putRBTreeVal(root, hash, key, value);
            } else {
                System.out.println("下标："+i+"，链表插入的key:" + key + ",value:" + value);

                // 哈希碰撞, 链表插入
                for (int linkSize = 0; ; ++linkSize) {
                    // System.out.println("linkSize="+linkSize+",node:"+parent);
                    //如果当前结点的下一个结点为null,就直接插入
                    if ((next = parent.next) == null) {
                        System.out.println("new链表长度为:" + linkSize);
                        parent.next = new Node<>(hash, key, value, null);
                        // 链表长度 >8时,链表的第九个元素开始转换为红黑树
                        if (linkSize >= TREEIFY_THRESHOLD - 1) {
                            Node<K, V> testNode = tab[i];
                            System.out.println("转换成红黑树插入的key:" + key + ",value:" + value);
                           /* for (int linkSize1 = 0; linkSize1 <linkSize+1; ++linkSize1){
                                System.out.println("linkSize1="+linkSize+",testNode:"+testNode);
                                testNode = testNode.next;
                            }*/
                            //System.out.println("node:"+parent.next);

                            System.out.println("链表长度为:" + linkSize);

                            linkToRBTree(tab, hash, ++linkSize);
                        }
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
     * 把链表转换成红黑树
     *
     * @param tab
     * @param hash
     */
    private void linkToRBTree(Node<K, V>[] tab, int hash, int linkSize) {
        // 通过hash计算出当前table数组的位置
        int index = (tab.length - 1) & hash;
        Node<K, V> node = tab[index];
       // RBTreeNode<K, V>[] rbTable = new RBTreeNode[linkSize + 1];
        int n = 0;
        //遍历链表中的每个节点,将链表转换为红黑树

        do {
            //把链表结点转换成红黑树结点
            RBTreeNode<K, V> next = replacementTreeNode(node, null);
            putRBTreeVal(next, hash, next.key, next.value);
           // rbTable[n] = next;
            System.out.println("转换成红黑树数组的循环次数:" + n);
            ++n;

            node = node.next;
        } while (node != null);
        System.out.println("n:" + n);
        // 输出数组元素
      /*  for (int i = 0; i < n; i++) {
            System.out.println("i:" + i + ":" + rbTable[i] + ",");
            putRBTreeVal(rbTable[i], hash, rbTable[i].key, rbTable[i].value);

        }*/

        print(hash);
    }

    /**
     * 把链表结点转换成红黑树结点
     *
     * @param p
     * @param next
     * @return
     */
    RBTreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
        return new RBTreeNode<K, V>(p.hash, p.key, p.value, next);
    }


    /**
     * 把新增结点添加到红黑树中,进入此方法的hash值都是相同的
     *
     * @param tabnode tab[ hash]中的节点或者新增节点
     * @param hash    hash值
     * @param key     key值
     * @param value   value值
     */
    RBTreeNode<K, V> putRBTreeVal(RBTreeNode<K, V> tabnode, int hash, K key, V value) {
        if ((table[hash]) instanceof RBTreeNode) {

            RBTreeNode<K, V> root = (RBTreeNode<K, V>) table[hash];
            RBTreeNode<K, V> parent = root;
            RBTreeNode<K, V> node = root;

            int cmp = 0;

            // 先找到父节点
            do {
                parent = node;
                K k1 = node.key;
                //比较key值
                cmp = compare(key, k1);
                if (cmp > 0) {
                    node = node.right;
                } else if (cmp < 0) {
                    node = node.left;
                } else {
                    V oldValue = node.value;
                    node.key = key;
                    node.value = value;
                    node.hash = hash;
                    return node;
                }

            } while (node != null);


            //插入新节点
            RBTreeNode<K, V> newNode = new RBTreeNode<>(hash, key, value, parent);
            if (cmp > 0) {
                parent.right = newNode;
            } else if (cmp < 0) {
                parent.left = newNode;
            }
            newNode.parent = parent;
            //插入成功后自平衡操作
            fixAfterPut(newNode, hash);
        } else {
            table[hash] = tabnode;
            fixAfterPut(tabnode, hash);

        }

        return null;
    }

    /**
     * 添加后平衡二叉树并设置结点颜色
     *
     * @param node 新添结点
     * @param hash hash值
     */
    private void fixAfterPut(RBTreeNode<K, V> node, int hash) {
        RBTreeNode<K, V> parent = node.parent;

        // 添加的是根节点 或者 上溢到达了根节点
        if (parent == null) {
            black(node);
            return;
        }

        // 如果父节点是黑色，直接返回
        if (isBlack(parent)) {
            return;
        }

        // 叔父节点
        RBTreeNode<K, V> uncle = parent.sibling();
        // 祖父节点
        RBTreeNode<K, V> grand = red(parent.parent);
        if (isRed(uncle)) { // 叔父节点是红色【B树节点上溢】
            black(parent);
            black(uncle);
            // 把祖父节点当做是新添加的节点
            fixAfterPut(grand, hash);
            return;
        }

        // 叔父节点不是红色
        if (parent.isLeftChild()) { // L
            if (node.isLeftChild()) { // LL
                black(parent);
            } else { // LR
                black(node);
                rotateLeft(parent, hash);
            }
            rotateRight(grand, hash);
        } else { // R
            if (node.isLeftChild()) { // RL
                black(node);
                rotateRight(parent, hash);
            } else { // RR
                black(parent);
            }
            rotateLeft(grand, hash);
        }
    }


    /**
     * 左旋
     *
     * @param grand
     */
    private void rotateLeft(RBTreeNode<K, V> grand, int hash) {
        RBTreeNode<K, V> parent = grand.right;
        RBTreeNode<K, V> child = parent.left;
        grand.right = child;
        parent.left = grand;
        afterRotate(grand, parent, child, hash);
    }

    /**
     * 右旋
     *
     * @param grand
     */
    void rotateRight(RBTreeNode<K, V> grand, int hash) {
        RBTreeNode<K, V> parent = grand.left;
        RBTreeNode<K, V> child = parent.right;
        grand.left = child;
        parent.right = grand;
        afterRotate(grand, parent, child, hash);
    }

    void afterRotate(RBTreeNode<K, V> grand, RBTreeNode<K, V> parent, RBTreeNode<K, V> child, int hash) {
        // 让parent称为子树的根节点
        parent.parent = grand.parent;
        if (grand.isLeftChild()) {
            grand.parent.left = parent;
        } else if (grand.isRightChild()) {
            grand.parent.right = parent;
        } else { // grand是root节点
            int index = table.length - 1 & hash;
            table[index] = parent;
        }

        // 更新child的parent
        if (child != null) {
            child.parent = grand;
        }

        // 更新grand的parent
        grand.parent = parent;
        print(hash);

    }


    /**
     * 设置红色或黑色
     *
     * @param node
     * @param color
     * @return
     */
    private Node<K, V> color(Node<K, V> node, boolean color) {
        if (node == null) return node;
        ((RBTreeNode<K, V>) node).color = color;
        return node;
    }

    /**
     * 设置为红色
     *
     * @param node
     * @return
     */
    private RBTreeNode<K, V> red(RBTreeNode<K, V> node) {
        return (RBTreeNode<K, V>) color(node, RED);
    }

    /**
     * 设置为黑色
     *
     * @param node
     * @return
     */
    private Node<K, V> black(Node<K, V> node) {
        return color(node, BLACK);
    }

    /**
     * 判断结点颜色
     *
     * @param node
     * @return
     */
    private boolean colorOf(Node<K, V> node) {
        return node == null ? BLACK : ((RBTreeNode<K, V>) node).color;
    }

    /**
     * 判断是否是黑色
     *
     * @param node
     * @return
     */
    private boolean isBlack(Node<K, V> node) {
        return colorOf(node) == BLACK;
    }

    /**
     * 判断是否为红色
     *
     * @param node
     * @return
     */
    private boolean isRed(Node<K, V> node) {
        return colorOf(node) == RED;
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
             System.out.println(oldCapacity + "扩容为" + newCapacity);
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
        if ((parent = table[index = hash]) != null) {
            if (parent instanceof RBTreeNode) { // 红黑树删除


                RBTreeNode<K, V> willNode = (RBTreeNode<K, V>) parent;
                //找到要删除的结点
                RBTreeNode<K, V> removeNode = (RBTreeNode<K, V>) getRBTreeNode(willNode, hash, key);
                oldValue = removeNode.value;

                // 度为2 的结点
                if (removeNode.hasTwoChildren()) {
                    //找到后继接地那
                    RBTreeNode<K, V> s = successor(removeNode);
                    removeNode.key = s.key;
                    removeNode.value = s.value;
                    removeNode.hash = s.hash;
                    // 删除后继节点
                    removeNode = s;
                }

                // 删除node节点（node的度必然是1或者0）
                RBTreeNode<K, V> replacement = removeNode.left != null ? removeNode.left : removeNode.right;

                if (replacement != null) { // node是度为1的节点
                    // 更改parent
                    replacement.parent = removeNode.parent;
                    // 更改parent的left、right的指向
                    if (removeNode.parent == null) { // node是度为1的节点并且是根节点
                        table[index] = replacement;
                    } else if (removeNode == removeNode.parent.left) {
                        removeNode.parent.left = replacement;
                    } else { // node == node.parent.right
                        removeNode.parent.right = replacement;
                    }

                    // 删除节点之后的处理
                    fixAfterRemove(replacement, hash);
                } else if (removeNode.parent == null) { // node是叶子节点并且是根节点
                    table[index] = null;
                } else { // node是叶子节点，但不是根节点
                    if (removeNode == removeNode.parent.left) {
                        removeNode.parent.left = null;
                    } else { // node == node.parent.right
                        removeNode.parent.right = null;
                    }

                    // 删除节点之后的处理
                    fixAfterRemove(removeNode, hash);

                }
                System.out.println("删除结点后的红黑树:" + key);
                print(hash);
                size--;
                return oldValue;
            } else if (parent.next != null) { //链表删除
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
                        size--;
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
                  /*  for (int i = index + 1; i < table.length; i++) {
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

    private RBTreeNode<K, V> successor(RBTreeNode<K, V> node) {

        // 前驱节点在左子树当中（right.left.left.left....）
        RBTreeNode<K, V> p = node.right;
        if (p != null) {
            while (p.left != null) {
                p = p.left;
            }
            return p;
        }

        // 从父节点、祖父节点中寻找前驱节点
        while (node.parent != null && node == node.parent.right) {
            node = node.parent;
        }

        return node.parent;
    }

    private void fixAfterRemove(RBTreeNode<K, V> node, int hash) {
        // 如果删除的节点是红色
        // 或者 用以取代删除节点的子节点是红色
        if (isRed(node)) {
            black(node);
            return;
        }

        RBTreeNode<K, V> parent = node.parent;
        if (parent == null) return;

        // 删除的是黑色叶子节点【下溢】
        // 判断被删除的node是左还是右
        boolean left = parent.left == null || node.isLeftChild();
        RBTreeNode<K, V> sibling = left ? parent.right : parent.left;
        if (left) { // 被删除的节点在左边，兄弟节点在右边
            if (isRed(sibling)) { // 兄弟节点是红色
                black(sibling);
                red(parent);
                rotateLeft(parent, hash);
                // 更换兄弟
                sibling = parent.right;
            }

            // 兄弟节点必然是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {
                // 兄弟节点没有1个红色子节点，父节点要向下跟兄弟节点合并
                boolean parentBlack = isBlack(parent);
                black(parent);
                red(sibling);
                if (parentBlack) {
                    fixAfterRemove(parent, hash);
                }
            } else { // 兄弟节点至少有1个红色子节点，向兄弟节点借元素
                // 兄弟节点的左边是黑色，兄弟要先旋转
                if (isBlack(sibling.right)) {
                    rotateRight(sibling, hash);
                    sibling = parent.right;
                }

                color(sibling, colorOf(parent));
                black(sibling.right);
                black(parent);
                rotateLeft(parent, hash);
            }
        } else { // 被删除的节点在右边，兄弟节点在左边
            if (isRed(sibling)) { // 兄弟节点是红色
                black(sibling);
                red(parent);
                rotateRight(parent, hash);
                // 更换兄弟
                sibling = parent.left;
            }

            // 兄弟节点必然是黑色
            if (isBlack(sibling.left) && isBlack(sibling.right)) {
                // 兄弟节点没有1个红色子节点，父节点要向下跟兄弟节点合并
                boolean parentBlack = isBlack(parent);
                black(parent);
                red(sibling);
                if (parentBlack) {
                    fixAfterRemove(parent, hash);
                }
            } else { // 兄弟节点至少有1个红色子节点，向兄弟节点借元素
                // 兄弟节点的左边是黑色，兄弟要先旋转
                if (isBlack(sibling.left)) {
                    rotateLeft(sibling, hash);
                    sibling = parent.left;
                }

                color(sibling, colorOf(parent));
                black(sibling.left);
                black(parent);
                rotateRight(parent, hash);
            }
        }
    }

    @Override
    public V get(Object key) {
        Node<K, V> e;

        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * 通过key值在数组/链表/红黑树中查找value值
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
                //有子结点的时候,需要判断是链表还是红黑树

                //在链表中查找,需要通过循环一个个往下查找
                while (next != null) {
                    if (next.hash == hash && ((k = next.key) == key || (key != null && key.equals(k)))) {
                        return next;
                    }
                    next = next.next;
                }

            }

            if (parent instanceof RBTreeNode) {
                //在红黑树中查找
                return getRBTreeNode((RBTreeNode<K, V>) parent, hash, key);
            }
        }


        return null;
    }

    /**
     * 在红黑树中查找结点
     *
     * @param node 根结点
     * @param hash hash(key) 计算出的哈希值
     * @param key  需要寻找的key值
     * @return
     */
    public Node<K, V> getRBTreeNode(RBTreeNode<K, V> node, int hash, Object key) {

        // 存储查找结果
        Node<K, V> result = null;
        K k;
        int cmp = 0;
        while (node != null) {
            //左节点
            RBTreeNode<K, V> nl = node.left;
            // 右节点
            RBTreeNode<K, V> nr = node.right;
            K k2 = node.key;
            int hash1 = node.hash;
            //比较hash值,判断是在左子树还是右子树
            if (hash > hash1) {
                //查找结点在右子树
                node = nr;
            } else if (hash < hash1) {
                //查找结点在左子树
                node = nl;
            } else if ((k = node.key) == key || (key != null && key.equals(k))) {
                //如果key 相等,就返回node
                return node;
            } else if (nl == null) {
                node = nr;
            } else if (nr == null) {
                node = nl;
            } else if (key != null & k2 != null
                    && key.getClass() == k2.getClass()
                    && key instanceof Comparable
                    && (cmp = compare(key, k2)) != 0
            ) {
                node = cmp > 0 ? node.right : node.left;
            } else if (node.right != null && (result = getRBTreeNode(node.right, hash, key)) != null) {
                return result;
            } else {
                node = node.left;
            }
        }
        return null;
    }

    /**
     * 打印红黑树
     *
     * @param hash
     */
    public void print(int hash) {


        final RBTreeNode<K, V> root = (RBTreeNode<K, V>) table[ hash];
        BinaryTrees.println(new BinaryTreeInfo() {
            @Override
            public Object string(Object node) {
                return node;
            }

            @Override
            public Object root() {
                return root;
            }

            @Override
            public Object right(Object node) {
                return ((RBTreeNode<K, V>) node).right;
            }

            @Override
            public Object left(Object node) {
                return ((RBTreeNode<K, V>) node).left;
            }
        });
        System.out.println("---------------------------------------------------");
    }

    /**
     * 链表打印
     *
     * @param hash
     */
    public String printLinked(int hash) {
        StringBuilder string = new StringBuilder();
        string.append(" [");
        Node<K, V> node = table[ hash];
        int n =0;
        for (int linkSize = 0; ; ++linkSize) {

            if (linkSize != 0) {
                string.append("->");
                string.append(node.key + "=" + node.value);
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
            if (node != null){
                // 如果结点是红黑树
                if (node instanceof RBTreeNode) {
                    Queue<RBTreeNode<K, V>> queue = new LinkedList<>();
                    queue.offer((RBTreeNode<K, V>) node);
                    while (!queue.isEmpty()) {
                        RBTreeNode<K, V> rbNode = queue.poll();
                        if (Objects.equals(value, rbNode.value)) {
                            return true;
                        }

                        if (rbNode.left != null) {
                            queue.offer(rbNode.left);
                        }
                        if (rbNode.right != null) {
                            queue.offer(rbNode.right);
                        }
                    }
                } else if (node.next != null) {
                    // 链表中循环
                    do {
                        if (node.value == value || (value != null && node.value.equals(value))) {
                            return true;
                        }

                    } while ((node = node.next) != null);
                } else if (node.value == value) {  //如果结点是数组, 直接比较值
                    if (node.value == value || (value != null && node.value.equals(value))) {
                        return true;
                    }
                }
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
    static class Node<K, V> implements Map.Entry<K, V> {
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

    /**
     * 红黑树结点
     *
     * @param <K>
     * @param <V>
     */
    static final class RBTreeNode<K, V> extends Node<K, V> {
        boolean color = RED;
        // 左节点
        RBTreeNode<K, V> left;
        // 右节点
        RBTreeNode<K, V> right;
        // 父节点
        RBTreeNode<K, V> parent;


        public RBTreeNode(int hash, K key, V value, Node<K, V> next) {
            super(hash, key, value, next);
        }


        public boolean hasTwoChildren() {
            return left != null && right != null;
        }

        /**
         * 是否为左结点
         *
         * @return
         */
        public boolean isLeftChild() {
            return parent != null && this == parent.left;
        }


        /**
         * 判断是否为右子树
         *
         * @return
         */
        public boolean isRightChild() {
            return parent != null && this == parent.right;
        }

        /**
         * 获取兄弟结点
         *
         * @return
         */
        public RBTreeNode<K, V> sibling() {
            if (isLeftChild()) {
                return parent.right;
            }

            if (isRightChild()) {
                return parent.left;
            }

            return null;
        }

        @Override
        public String toString() {
            String str = "";
            if (color == RED) {
                str = "R_";
            }
            return str + key.toString() + "=" + value.toString();
        }
    }
}