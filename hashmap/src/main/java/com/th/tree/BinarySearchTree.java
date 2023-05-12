package com.th.tree;

import java.util.Comparator;

/**
 * 二叉查找树
 * @param <E>
 */
public class BinarySearchTree<E> extends BinaryTree<E> {



    //比较器
    private Comparator<E> comparator;


    public BinarySearchTree() {
    }

    public BinarySearchTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }


    /**
     * 添加 node 之后的调整,BST树无需做调整
     * @param node 新添加的节点
     */
    protected void afterAdd(Node<E>  node){

    }
    public void add(E element) {
        //非空检测
        elementNotNullCheck(element);
        //添加第一个结点
        if (root == null) {
            root = createNode(element,null);
            size++;
            afterAdd(root);
            return;
        }
        //添加的不是第一个结点
        /**
         * 添加步骤:
         * (1)找到父结点 parent;
         * (2)创建新结点 node;
         * (3)parent.left =node 或 parent.right = node
         *
         * 若遇到值相等的元素建议覆盖旧的值
         */
        //用来标记移动的结点
        Node<E> node = root;
        //保存当前结点的父结点，默认根结点就是父结点
        Node<E> parent = root;
        //根据比较规则找到待添加元素的位置
        int cmp = 0;
        do {
            //比较值
            cmp = compare(element, node.element);
            //保存当前结点的父结点
            parent = node;
            if (cmp > 0) {
                node = ((Node<E>) node).right;
            } else if (cmp < 0) {
                node = ((Node<E>) node).left;
            } else {
                //若遇到值相等的元素建议覆盖旧的值
                ((Node<E>) node).element = element;
                return;
            }
        } while (node != null);

        //创建新节点.并判断是插入到哪里
        Node<E> newNode =createNode(element,parent);
        if (cmp > 0) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
        size++;
        // 新添加节点的处理
        afterAdd(newNode);
    }

    protected void elementNotNullCheck(E element) {
        if (element == null) {
            throw new IllegalArgumentException("element must not be null");
        }
    }
    /**
     * 根据元素查找结点
     * @param element
     * @return
     */
    private Node<E> node(E element){
        Node<E> node = root;
        while (node != null){
            int cmp= compare(element,node.element);
            if (cmp == 0){
                return  node;
            }else if (cmp > 0 ){
                node= node.right;
            }else {
                node = node.left;
            }
        }
        return  null;
    }
    public boolean contains(E element) {
        return node(element)!= null;
    }


    public void remove(E element) {
        remove(node(element));
    }


    /**
     * 添加 node 之后的调整,BST树无需做调整
     * @param node 新添加的节点
     */
    protected void afterRemove(Node<E>  node){

    }
    private void remove(Node<E> node) {
        if (node == null) return;

        size--;

        if (node.hasTwoChildren()) { // 度为2的节点
            // 找到后继节点
            Node<E> s = successor(node);
            // 用后继节点的值覆盖度为2的节点的值
            node.element = s.element;
            // 删除后继节点
            node = s;
        }

        // 删除node节点（node的度必然是1或者0）
        Node<E> replacement = node.left != null ? node.left : node.right;

        if (replacement != null) { // node是度为1的节点
            // 更改parent
            replacement.parent = node.parent;
            // 更改parent的left、right的指向
            if (node.parent == null) { // node是度为1的节点并且是根节点
                root = replacement;
            } else if (node == node.parent.left) {
                node.parent.left = replacement;
            } else { // node == node.parent.right
                node.parent.right = replacement;
            }

            // 删除节点之后的处理
            afterRemove(node);
        } else if (node.parent == null) { // node是叶子节点并且是根节点
            root = null;

            // 删除节点之后的处理
            afterRemove(node);
        } else { // node是叶子节点，但不是根节点
            if (node == node.parent.left) {
                node.parent.left = null;
            } else { // node == node.parent.right
                node.parent.right = null;
            }
            // 删除节点之后的处理
            afterRemove(node);
        }
    }
    /**
     * 规定传入对象的比较规则
     *
     * @param e1 第一个对象
     * @param e2 第二个对象
     * @return 0表示e1 =e2，大于0表示 e1 > e2,小于0表示 e1 < e2
     */
    protected int compare(E e1,E e2){
        if (comparator !=null){
            return  comparator.compare(e1,e2);
        }
        return  ((java.lang.Comparable<E>) e1).compareTo(e2);
    }


}
