package com.th.tree;

import com.th.tree.printer.BinaryTrees;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: bstTest
 * @Description:
 * @Author: 唐欢
 * @Date: 2023/5/4 14:35
 * @Version 1.0
 */
public class TreeTest {

    @Test
    public void bsttest() {
      /*  Integer data[] = new Integer[] {
               20,10,30,5,15,2,7,13,17,1,3,6,8,11,14,16,18,25,35,20,27,33,45,21,23,26,28,32,34,40,46
        };*/
       /* Integer data[] = new Integer[] {
                20,10,30,5,15,25,35,2,7,33,45
        };*/

        Integer data[] = new Integer[] {
               13,12,9,8,7,6,4
        };
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int i = 0; i < data.length; i++) {
            bst.add(data[i]);
        }

        System.out.println("打印树形结构的二叉树");
        BinaryTrees.println(bst);




        System.out.println();
        System.out.println("前序遍历(递归）:");
        bst.preorderTraversal(new BinarySearchTree.Visitor<Integer>() {
            public boolean visit(Integer element) {
                System.out.print(element + " ");
                //return element == 2 ? true : false;
                return  false;
            }
        });
        System.out.println("前序遍历(非递归）:");
        bst.preorderTraversalByStack();



        System.out.println();
        System.out.println("中序遍历(递归):");
        bst.inorderTraversal(new BinarySearchTree.Visitor<Integer>() {
            public boolean visit(Integer element) {
                System.out.print(element + " ");
              //  return element == 4 ? true : false;
                return false;
            }
        });
        System.out.println();
        System.out.println("中序遍历(非递归):");
        bst.inorderTraversalByStack();


        System.out.println();
        System.out.println("后序遍历(递归）:");

        bst.postorderTraversal(new BinarySearchTree.Visitor<Integer>() {
            public boolean visit(Integer element) {
                System.out.print(element + " ");
               // return element == 4 ? true : false;
                return  false;
            }
        });
        System.out.println();
        System.out.println("后序遍历(非递归）:");
        bst.postorderTraversalByStack();


        System.out.println();
        System.out.println("层级遍历:");

        bst.levelorderTraversal();
        System.out.println();


       // BinaryTrees.println(bst);
        //bst.remove(45);
        //BinaryTrees.println(bst);
    }

    @Test
    public  void avlTest(){
      /*  Integer data[] = new Integer[] {
              85,19,69,3,7,99,95,2,1,70,44,58,11,21,14,93,57,4,56
        };*/
        Integer data[] = new Integer[] {
               13,14,15,12,11,17,16,8,9,1
        };
        AVLTree<Integer>  avl = new AVLTree<>();
        for (int i = 0 ;i<data.length;i++){
            avl.add(data[i]);
            BinaryTrees.println(avl);

        }

    }

    @Test
    public void avltest2() {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            data.add((int) (Math.random() * 100));
        }

        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int i = 0; i < data.size(); i++) {
            bst.add(data.get(i));
        }
        System.out.println("bst 添加元素:");
        BinaryTrees.println(bst);
        for (int i = 0; i < data.size(); i++) {
            bst.contains(data.get(i));

        }
        for (int i = 0; i < data.size(); i++) {
            bst.remove(data.get(i));
        }
        System.out.println("bst 删除元素:");
        BinaryTrees.println(bst);

        AVLTree<Integer> avl = new AVLTree<>();
        for (int i = 0; i < data.size(); i++) {
            avl.add(data.get(i));
        }
        System.out.println("avl 添加元素:");
        BinaryTrees.println(avl);
        for (int i = 0; i < data.size(); i++) {
            avl.contains(data.get(i));
        }
        for (int i = 0; i < data.size(); i++) {
            avl.remove(data.get(i));

           // BinaryTrees.println(avl);
        }
        System.out.println("avl 删除元素:");
    }
    @Test
    public void rbTreetest1() {
        /*Integer data[] = new Integer[] {
                55, 87, 56, 74, 96, 22, 62, 20, 70, 68, 90, 50
        };*/

        Integer data[] = new Integer[] {
                1, 5, 9, 29, 13, 33, 17, 21, 25,37,41,45,49
        };

        RBTree<Integer> rb = new RBTree<>();
        for (int i = 0; i < data.length; i++) {
            rb.add(data[i]);
            System.out.println("【" + data[i] + "】");
            BinaryTrees.println(rb);
            System.out.println("---------------------------------------");
        }
        BinaryTrees.println(rb);


    }

    @Test
    public  void rbTreeTest(){
        Integer data[] = new Integer[] {
                55, 87, 56, 74, 96, 22, 62, 20, 70, 68, 90, 50
        };

        RBTree<Integer> rb = new RBTree<>();
        for (int i = 0; i < data.length; i++) {
            rb.add(data[i]);
        }

        BinaryTrees.println(rb);

        for (int i = 0; i < data.length; i++) {
            rb.remove(data[i]);
            System.out.println("---------------------------------------");
            System.out.println("【" + data[i] + "】");
            BinaryTrees.println(rb);
        }
    }


    @Test
    public void treeTest(){
        Integer data[] = new Integer[]{
                10, 35, 47, 11, 5, 57, 39, 14, 27, 26, 84, 75, 63, 41, 37, 24, 96
        };
        System.out.println();
        System.out.println("二叉查找树：");
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int i = 0; i < data.length; i++) {
            bst.add(data[i]);
        }
        System.out.println("打印树形结构的二叉树");
        BinaryTrees.println(bst);


        System.out.println();
        System.out.println("AVL树：");
        AVLTree<Integer> avlTree = new AVLTree<>();
        for (int i = 0; i < data.length; i++) {
            avlTree.add(data[i]);
        }
        BinaryTrees.println(avlTree);


        System.out.println();
        System.out.println("红黑树：");
        RBTree<Integer> rb = new RBTree<>();
        for (int i = 0; i < data.length; i++) {
            rb.add(data[i]);
        }
        BinaryTrees.println(rb);
    }



}