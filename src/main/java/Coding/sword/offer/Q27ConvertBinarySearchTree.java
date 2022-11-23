package Coding.sword.offer;

import java.util.ArrayList;
/**
 * 输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的双向链表。
 * 要求不能创建任何新的结点，只能调整树中结点指针的指向。
 * ---- 这一题不难 ----------
 */
public class Q27ConvertBinarySearchTree {
    public static void main(String[] args) {
        // 创建测试用例
        BinaryTreeNode tree = new BinaryTreeNode(10);
        tree.left = new BinaryTreeNode(6);
        tree.left.left = new BinaryTreeNode(4);
        tree.left.right = new BinaryTreeNode(8);
        tree.right = new BinaryTreeNode(14);
        tree.right.left = new BinaryTreeNode(12);
        tree.right.right = new BinaryTreeNode(16);
        tree.midOrderShow();
        convert(tree);
    }

    // 这个解法和 剑指offer上的不一样，不过我觉得好像问题不大
    public static void convert(BinaryTreeNode tree){
        if (tree == null)
            return;
        ArrayList<BinaryTreeNode> list = new ArrayList<>();
        // 用数组收集中序遍历的节点
        midTraverse(tree, list);
        BinaryTreeNode item_prev = null, item = null;
        for(int i = 1; i < list.size(); i++){
            //System.out.print(list.get(i).data + ", ");
            item_prev = list.get(i-1);
            item = list.get(i);
            item_prev.right = item;
            item.left = item_prev;
        }
        // 打印结果
        System.out.println("convert result: ");
        item = list.get(0);
        while (item != null){
            if (item.left !=null & item.right != null)
                System.out.print("(" + item.left.data + "<-)" + item.data + "(->" + item.right.data + "), ");
            else if (item.left != null){
                System.out.print("(" + item.left.data + "<-)" + item.data + ", ");
            }else if (item.right != null){
                System.out.print(item.data + "(->" + item.right.data + "), ");
            }else {
                System.out.print(item.data + ", ");
            }
            item = item.right;
        }
    }

    public static void midTraverse(BinaryTreeNode tree, ArrayList<BinaryTreeNode> list){
        if (tree == null)
            return;
        midTraverse(tree.left, list);
        list.add(tree);
        midTraverse(tree.right, list);
    }
}
