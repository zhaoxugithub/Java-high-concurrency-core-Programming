package com.th.tree;


import java.util.Comparator;

public class RBTree<E> extends BBST<E>{
    private static final boolean RED = false;
    private static final boolean BLACK = true;

    public RBTree() {
        this(null);
    }

    public RBTree(Comparator<E> comparator) {
        super(comparator);
    }

    /**
     * 添加结点后的处理
     * @param node 新添加的节点
     */
    @Override
    protected void afterAdd(Node<E> node) {
        Node<E> parent = node.parent;

        // 添加的是根节点 或者 上溢到达了根节点
        if (parent == null) {
            black(node);
            return;
        }

        // 如果父节点是黑色，直接返回
        if (isBlack(parent)) return;

        // 叔父节点
        Node<E> uncle = parent.sibling();
        // 祖父节点
        Node<E> grand = red(parent.parent);
        if (isRed(uncle)) { // 叔父节点是红色【B树节点上溢】
            black(parent);
            black(uncle);
            // 把祖父节点当做是新添加的节点
            afterAdd(grand);
            return;
        }

        // 叔父节点不是红色
        if (parent.isLeftChild()) { // L
            if (node.isLeftChild()) { // LL
                black(parent);
            } else { // LR
                black(node);
                rotateLeft(parent);
            }
            rotateRight(grand);
        } else { // R
            if (node.isLeftChild()) { // RL
                black(node);
                rotateRight(parent);
            } else { // RR
                black(parent);
            }
            rotateLeft(grand);
        }
    }

    @Override
    protected void afterRemove(Node<E> node) {
        // 如果删除的节点是红色
        // 或者 用以取代删除节点的子节点是红色
        if (isRed(node)) {
            black(node);
            return;
        }

        Node<E> parent = node.parent;
        // 删除的是根节点
        if (parent == null) return;

        // 删除的是黑色叶子节点【下溢】
        // 判断被删除的node是左还是右
        boolean left = parent.left == null || node.isLeftChild();
        Node<E> sibling = left ? parent.right : parent.left;
        if (left) { // 被删除的节点在左边，兄弟节点在右边
            if (isRed(sibling)) { // 兄弟节点是红色
                black(sibling);
                red(parent);
                rotateLeft(parent);
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
                    afterRemove(parent);
                }
            } else { // 兄弟节点至少有1个红色子节点，向兄弟节点借元素
                // 兄弟节点的左边是黑色，兄弟要先旋转
                if (isBlack(sibling.right)) {
                    rotateRight(sibling);
                    sibling = parent.right;
                }

                color(sibling, colorOf(parent));
                black(sibling.right);
                black(parent);
                rotateLeft(parent);
            }
        } else { // 被删除的节点在右边，兄弟节点在左边
            if (isRed(sibling)) { // 兄弟节点是红色
                black(sibling);
                red(parent);
                rotateRight(parent);
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
                    afterRemove(parent);
                }
            } else { // 兄弟节点至少有1个红色子节点，向兄弟节点借元素
                // 兄弟节点的左边是黑色，兄弟要先旋转
                if (isBlack(sibling.left)) {
                    rotateLeft(sibling);
                    sibling = parent.left;
                }

                color(sibling, colorOf(parent));
                black(sibling.left);
                black(parent);
                rotateRight(parent);
            }
        }
    }

    public void remove(Node<E> node) {
        if (node ==null ){
            return;
        }
        size -- ;

        //度为2的结点
        if (node.hasTwoChildren()){
            //找到后继结点
            Node<E> s = successor(node);
            //用后续结点的值覆盖度为2的结点的值
            node.element = s.element;
            //删除后续结点
            node = s;
        }

        //删除node节点(node的度必然是1或0)
        Node<E> replacement = node.left != null?node.left : node.right;

        //node是度为1的结点
        if (replacement!= null){
            // 更改为parent
            replacement.parent = node.parent;
            //更改parent的left,right 的指向
            if (node.parent == null){
                root = replacement;
            }else if(node == node.parent.left){
                node.parent.left = replacement;
            }else{
                node.parent.right = replacement;
            }
            // 删除节点之后的处理
            afterRemove(replacement);
        }else if (node.parent == null){ //node 是叶子结点并且是根结点
            root = null;
            afterRemove(node);
        }else{ //node 是叶子结点,当不是跟结点
            if (node  == node.parent.left){
                node.parent.left = null;
            }else {
                node.parent.right = null;
            }
            afterRemove(node);
        }
    }

    /**
     * 设置红色或黑色
     * @param node
     * @param color
     * @return
     */
    private Node<E> color(Node<E> node, boolean color) {
        if (node == null) return node;
        ((RBNode<E>)node).color = color;
        return node;
    }

    /**
     * 设置为红色和
     * @param node
     * @return
     */
    private Node<E> red(Node<E> node) {
        return color(node, RED);
    }

    /**
     * 设置为黑色
     * @param node
     * @return
     */
    private Node<E> black(Node<E> node) {
        return color(node, BLACK);
    }

    /**
     * 判断结点颜色
     * @param node
     * @return
     */
    private boolean colorOf(Node<E> node) {
        return node == null ? BLACK : ((RBNode<E>)node).color;
    }

    /**
     * 判断是否是黑色
     * @param node
     * @return
     */
    private boolean isBlack(Node<E> node) {
        return colorOf(node) == BLACK;
    }

    /**
     * 判断是否为红色
     * @param node
     * @return
     */
    private boolean isRed(Node<E> node) {
        return colorOf(node) == RED;
    }

    @Override
    protected Node<E> createNode(E element, Node<E> parent) {
        return new RBNode<>(element, parent);
    }

    private static class RBNode<E> extends Node<E> {
       // private static final boolean RED = false;
        boolean color = RED;
        public RBNode(E element, Node<E> parent) {
            super(element, parent);
        }

        @Override
        public String toString() {
            String str = "";
            if (color == RED) {
                str = "R_";
            }
            return str + element.toString();
        }
    }
}
