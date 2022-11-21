package Coding.sword.offer;

/**
 * 输入一棵二叉树和整数，打印出二叉树中节点值之和为该输入整数的所有路径。
 * 从树的根节点开始往下直到叶结点所经过的结点形成一条路径。
 */
public class Q25PathInTree {
    public static void main(String[] args) {
        BinaryTreeNode tree = new BinaryTreeNode(10);
        tree.left = new BinaryTreeNode(5);
        tree.left.left = new BinaryTreeNode(4);
        tree.left.right = new BinaryTreeNode(7);
        tree.right = new BinaryTreeNode(12);

    }

    public static void findPath(BinaryTreeNode treeNode, int sum){

    }
}
