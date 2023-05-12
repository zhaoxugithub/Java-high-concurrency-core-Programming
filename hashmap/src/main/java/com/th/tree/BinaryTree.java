package com.th.tree;

import com.th.list.linked.SingleLinkedList;
import com.th.tree.printer.BinaryTreeInfo;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @ClassName: BinaryTree
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/4 13:53
 * @Version 1.0
 */
public class BinaryTree<E> implements BinaryTreeInfo {
    //当前树结点个数
    protected    int size;

    //根节点
    protected Node<E> root;


    //比较器
    private Comparator<E> comparator;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size ==0;
    }

    public void clear() {
        root =null;
        size =0;
    }



    /********************************************************************************************************/
    //采用递归方式实现二叉树的前序遍历/中序遍历/后序遍历/层序遍历

    /**
     * 前序遍历
     */
    public  void preorderTraversal(BinarySearchTree.Visitor<E> visitor){
        if (visitor == null ){
            throw new IllegalArgumentException("visitor is null");
        }
        preorderTraversal(root,visitor);
    }
    public  void preorderTraversal(Node<E> node, BinarySearchTree.Visitor<E> visitor){
        if(node == null || visitor.stop){
            return;
        }
        visitor.stop = visitor.visit(node.element);
        preorderTraversal(node.left,visitor);
        preorderTraversal(node.right,visitor);
    }


    /**
     * 中序遍历
     */
    public  void inorderTraversal(BinarySearchTree.Visitor<E> visitor){
        if (visitor == null ){
            throw new IllegalArgumentException("visitor is null");
        }
        inorderTraversal(root,visitor);
    }
    public  void inorderTraversal(Node<E> node, BinarySearchTree.Visitor<E> visitor){
        if(node == null || visitor.stop){
            return;
        }
        inorderTraversal(node.left,visitor);
        if (visitor.stop){
            return;
        }
        visitor.stop = visitor.visit(node.element);
        inorderTraversal(node.right,visitor);
    }

    /**
     * 后序遍历
     */
    public  void postorderTraversal(BinarySearchTree.Visitor<E> visitor){
        if (visitor == null){
            throw new IllegalArgumentException("visitor is null");
        }
        postorderTraversal(root,visitor);
    }
    public  void postorderTraversal(Node<E> node, BinarySearchTree.Visitor<E> visitor){
        if(node == null || visitor.stop){
            return;
        }
        postorderTraversal(node.left,visitor);
        postorderTraversal(node.right,visitor);
        if (visitor.stop){
            return;
        }
        visitor.stop = visitor.visit(node.element);
    }

    /*******************************************************************************************************/


    /********************************************************************************************************/
    //采用栈实现二叉树的前序遍历/中序遍历/后序遍历/层序遍历
    /**
     * 前序遍历-栈,栈具有先进后出的特点, 右结点先入栈
     * 思路:
     *1、根节点入栈
     *2、从栈中弹出一个节点cur
     *3、打印(处理)cur
     *4、先右节点再左节点入栈(如果有)
     *5、重复步骤2-4
     */
    public  void preorderTraversalByStack(){
        preorderTraversalByStack(root);
    }

    public void preorderTraversalByStack(Node<E> popNode) {
        if (popNode == null ) {
            return;
        }
        SingleLinkedList<E> linkedList = new SingleLinkedList<E>();
        Stack<Node<E>> stack = new Stack<>();

        Node<E> node = popNode;
        stack.push(popNode);
        while (!stack.isEmpty() ) {
            node = stack.pop();
            linkedList.add(node.element);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        linkedList.toString();
    }

    /**
     * 中序遍历-栈-
     * 1、每颗子树，整课树左边界进栈
     * 2、依次弹出的过程中打印
     * 3、如果弹出节点存在右树，右树入栈
     * 4、重复3
     */
    public  void inorderTraversalByStack(){

        inorderTraversalByStack(root);
    }

    /**
     * 从根节点依次将左结点入栈，当没有左结点的时候，弹出栈顶元素，将其写入list，
     * 并将其右结点入栈。重复上述操作。与常规方法相比，省去了查看左子树是否被访问过的步骤，
     * 对每个结点，都是先遍历其左子树，所以当访问到该结点的时候，
     * 可以确保其左子树已经被访问过了，只需要访问其本身和其右结点即可。
     *
     * @param popNode
     */
    public  void inorderTraversalByStack(Node<E>  popNode){
        if(popNode == null ){
            return;
        }
        Stack<Node<E>> stack = new Stack<>();
       Node<E> node = popNode;
       SingleLinkedList<E> linkedList = new SingleLinkedList<>();
       // stack.push(node);
        while(node !=null || !stack.isEmpty() ){
          if (node != null){
              stack.push(node);
              node = node.left;
          }else{
              node = stack.pop();
              linkedList.add(node.element);
              node = node.right;
          }
        }
        linkedList.toString();
    }

    /**
     * 后续遍历-栈
     *思路：将根节点左子树入栈，当访问到叶子结点，出栈，
     * 查看该叶子结点的父节点是否有右结点，有的话，
     * 如果右结点未访问则将其入栈，当一个节点的左右结点都已经访问过或者不包含左右结点，则出栈。
     */
    public  void postorderTraversalByStack(){

        postorderTraversalByStack(root);
    }
    public  void postorderTraversalByStack(Node<E> popNode){
        if(popNode == null){
            return;
        }
        Stack<Node<E>> stack = new Stack<>();
        SingleLinkedList<E> linkedList = new SingleLinkedList<>();
        Node<E> node = popNode;

        // 栈顶结点
        Node<E> peek;
        //上次访问的结点
        Node<E> prev = null;
        stack.push(node);

        while( !stack.isEmpty()){
            //获取栈顶结点
            peek = stack.peek();
            //如果栈顶结点是叶子结点或者栈顶结点的右结点是上一次pop的节点，就pop 出栈顶元素
            if (peek.isLeaf() || peek.right == prev) {
                node = stack.pop();
                linkedList.add(node.element);
                prev = node;

            }else  {
                //如果栈顶结点的左结点 是上一次pop的结点，如果栈顶结点的右结点存在，就push，如果不存在就pop 出栈顶结点
                if (peek.left == prev){
                    if (peek.right!=null){
                        stack.push(peek.right);
                    }else{
                        node = stack.pop();
                        linkedList.add(node.element);
                        prev = node;
                    }
                }else {
                    //如果栈顶结点的左结点不是上一次pop的结点，即把栈顶结点的左结点push入栈
                    stack.push(peek.left);
                    node = peek.left;
                }
            }
        }

        linkedList.toString();
    }

    /**
     * 层级遍历-队列
     * 思路:
     *  1、头节点进队
     *  2、节点出队并输出，输出节点命名为cur
     *  3、如果cur左节点存在，则左节点入队，如果node右节点存在，则右节点入队 (先左后右)
     *  4、周而复始2-3步骤
     */
    public  void levelorderTraversal(){
        if( root == null) {
            throw new IllegalArgumentException("Visitor不能为空");
        }
        Queue<Node<E>> linkedList = new LinkedList<>();
        SingleLinkedList singleLinkedList = new SingleLinkedList();
        //将根结点入队
        linkedList.offer(root);
        while (!linkedList.isEmpty()){
            //队头元素出队
            Node<E> node = linkedList.poll();
            singleLinkedList.add(node.element);
            //回调，将处理遍历数据的业务交给调用者，如果返回true停止遍历
            if(node.left !=null) {
                linkedList.offer(node.left);
            }
            if(node.right !=null) {
                linkedList.offer(node.right);
            }
        }
        singleLinkedList.toString();
    }

   /* public  String toString(){
        StringBuilder sb = new StringBuilder();
        toString(root,sb,"");
        System.out.println(sb.toString());
        return  sb.toString();
    }*/

    private  void toString(Node<E> node, StringBuilder sb,String prefix){
        if (node == null){
            return;
        }
        sb.append(prefix)
                .append(node.element)
                .append("\n");
        toString(node.left,sb,prefix+"L----");
        toString(node.right,sb,prefix+"R----");
    }


    /******************************************************************************************************/
    /**
     * 方式一:采用递归方式计算二叉树高度
     */
    public  int height(){
        return  height(root);
    }
    public  int height(Node<E> node){
        if (node == null){
            return  0;
        }
        return 1+Math.max(height(node.left),height(node.right));
    }

    /**
     * 方式二:通过层序遍历层高
     *
     */
    public  int heightByLevel(){
        if(root == null){
            return 0;
        }

        //二叉树的高度
        int height =0;

        //存储着每一层的元素数量
        int levelSize =1 ;
        Queue<Node<E>> queue = new LinkedList<>();

        queue.offer(root);

        while(!queue.isEmpty()){
            Node<E> node = queue.poll();
            levelSize --;

            if (node.left != null){
                queue.offer(node.left);

            }
            if (node.right != null){
                queue.offer(node.right);

            }
            //意味着即要访问下一层
            if (levelSize == 0){
                levelSize=queue.size();
                height ++;
            }

        }
        return height;
    }
    /******************************************************************************************************/

    /**
     * 判断是否是完全二叉树
     * @return
     */
    public  boolean isComplete(){
        if (root == null){
            return  false;
        }

        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root);
        boolean leaf =false;
        while (!queue.isEmpty()){
            Node<E> node = queue.poll();
            if(leaf && !node.isLeaf()){
                return  false;
            }
            if (node.left != null){
                queue.offer(node.left);
            }else if (node.right != null){
                return  false;
            }
            if(node.right !=null){
                queue.offer(node.right);
            }else{
                leaf =true;
            }
        }
        return leaf;
    }





    /**
     * 增强遍历接口:
     * 不希望全部遍历完，满足某个条件解终止遍历。
     * @param <E>
     */
    public  abstract  static   class Visitor<E> {
        //stop用来标记递归遍历中是否需要停止遍历
        boolean stop = false;

        /**
         *
         * @param element
         * @return 如果返回true,就代表停止遍历
         */
        abstract    boolean visit(E element);
    }


    /**
     * 获取后继结点
     * @param node
     * @return
     */
    protected Node<E> successor(Node<E> node) {
        if (node == null) return null;

        // 前驱节点在左子树当中（right.left.left.left....）
        Node<E> p = node.right;
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
    /*******************************************************************************************************/



    @Override
    public Object root() {
        return root;
    }

    @Override
    public Object left(Object node) {
        return ((Node<E>)node).left;
    }

    @Override
    public Object right(Object node) {
        return ((Node<E>)node).right;
    }

    @Override
    public Object string(Object node) {
        return node;
    }


    /**
     * 生产结点
     * @param element
     * @param parent
     * @return
     */
    protected  Node<E> createNode(E element,Node<E> parent){
        return  new Node<>(element,parent);
    }


    /**
     * 树节点
     * @param <E>
     */
    protected static class Node<E> {
        //当前结点保存的元素
        E element ;

        //左结点
       Node<E> left;

        //右结点
        Node<E> right;

        //父结点
        Node<E> parent;


        public Node(E element,Node<E> parent) {
            this.element = element;
            this.parent = parent;
        }

        /**
         *是否是叶子节点
         */
        public boolean isLeaf() {
            return left == null && right == null;
        }

        public boolean hasTwoChildren() {
            return left != null && right != null;
        }
        /**
         * 是否为左结点
         * @return
         */
        public  boolean isLeftChild(){
            return parent != null && this == parent.left;
        }


        /**
         * 判断是否为右子树
         * @return
         */
        public  boolean isRightChild(){
            return  parent != null && this == parent.right;
        }

        /**
         * 获取兄弟结点
         * @return
         */
        public Node<E> sibling() {
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
            return element.toString();
        }
    }
}