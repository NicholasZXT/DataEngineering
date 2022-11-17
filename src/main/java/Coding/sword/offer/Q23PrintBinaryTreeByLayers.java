package Coding.sword.offer;

import java.util.Queue;
import java.util.LinkedList;

/**
 * 层序遍历二叉树
 */
public class Q23PrintBinaryTreeByLayers {
    public static void main(String[] args) {
        BinaryTreeNode tree = new BinaryTreeNode(8);
        tree.left = new BinaryTreeNode(6);
        tree.left.left = new BinaryTreeNode(5);
        tree.left.right = new BinaryTreeNode(7);
        tree.right = new BinaryTreeNode(10);
        tree.right.left = new BinaryTreeNode(9);
        tree.right.right = new BinaryTreeNode(11);
        printBinaryTreeByLayers(tree);
    }

    public static void printBinaryTreeByLayers(BinaryTreeNode tree){
        if (tree == null)
            return;
        Queue<BinaryTreeNode> queue = new LinkedList<BinaryTreeNode>();
        BinaryTreeNode temp;
        queue.add(tree);
        while (!queue.isEmpty()){
            temp = queue.poll();
            System.out.print(temp.data + ", ");
            if (temp.left != null)
                queue.add(temp.left);
            if (temp.right != null)
                queue.add(temp.right);
        }

    }
}
