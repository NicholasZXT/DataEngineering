package Coding.sword.offer;

/**
 * 输入一棵二叉搜索树，将该二叉搜索树转换成一个排序的双向链表。
 * 要求不能创建任何新的结点，只能调整树中结点指针的指向。
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


    }

    public static void convert(){

    }
}
