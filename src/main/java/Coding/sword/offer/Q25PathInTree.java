package Coding.sword.offer;

import java.util.Stack;

/**
 * 输入一棵二叉树和整数，打印出二叉树中节点值之和为该输入整数的所有路径。
 * 从树的根节点开始往下直到叶结点所经过的结点形成一条路径。
 *
 * 这一题有点难度，需要思考一下
 */
public class Q25PathInTree {
    public static void main(String[] args) {
        BinaryTreeNode tree = new BinaryTreeNode(10);
        tree.left = new BinaryTreeNode(5);
        tree.left.left = new BinaryTreeNode(4);
        tree.left.right = new BinaryTreeNode(7);
        tree.right = new BinaryTreeNode(12);
        Stack<Integer> stack = new Stack<Integer>();
        findPath(tree, 22, stack);
    }

    public static void findPath(BinaryTreeNode tree, int sum, Stack<Integer> stack){
        if (tree == null)
            return;
        int remain = sum - tree.data;
        if (remain < 0 )
            return;
        if (remain == 0 & tree.left == null & tree.right == null){
            Stack<Integer> temp_stack = new Stack<>();
            while (!stack.empty()){
                temp_stack.push(stack.pop());
            }
            while (!temp_stack.empty()){
                int value = temp_stack.pop();
                System.out.print(value + ", ");
                stack.push(value);
            }
            System.out.println(tree.data);
            return;
        }
        // 下面 先 push ，返回前一定要记得 pop
        stack.push(tree.data);
        findPath(tree.left, remain, stack);
        findPath(tree.right, remain, stack);
        stack.pop();
    }
}
