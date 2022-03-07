package LeetCode.SwordOffer;

import java.util.Arrays;

public class Q6_ReconstructBinaryTree {
    public static void main(String[] args){
        int[] firstOrder = {1,2,4,7,3,5,6,8};
        int[] midOrder = {4,7,2,1,5,3,8,6};
        BinaryTreeNode tree = reconstruct(firstOrder, midOrder);
        tree.firstRootShow();

        // 测试二叉树
        System.out.println("----------------------------------");
        BinaryTreeNode tree1 = new BinaryTreeNode(1);
        tree1.left = new BinaryTreeNode(2);
        tree1.left.left = new BinaryTreeNode(4);
        tree1.left.left.right = new BinaryTreeNode(7);
        tree1.right = new BinaryTreeNode(3);
        tree1.right.left = new BinaryTreeNode(5);
        tree1.right.right = new BinaryTreeNode(6);
        tree1.right.right.left = new BinaryTreeNode(8);
        tree1.firstRootShow();
    }

    public static BinaryTreeNode reconstruct(int[] firstOrder, int[] midOrder){
        if (firstOrder.length <=0 | midOrder.length <= 0){
            return null;
        }
        System.out.println("first order: " + Arrays.toString(firstOrder) + ", mid order: " + Arrays.toString(midOrder));
        int root_value = firstOrder[0];
        int root_index = 0;
        while (midOrder[root_index] != root_value & root_index < midOrder.length){
            root_index += 1;
        }
        int left_num = root_index;
        int right_num = midOrder.length - root_index - 1;
        System.out.println("left_num: " + left_num + ", right num: " + right_num);
        int[] first_left = Arrays.copyOfRange(firstOrder, 1, left_num+1);
        int[] mid_left = Arrays.copyOfRange(midOrder, 0, left_num);
        int[] first_right = Arrays.copyOfRange(firstOrder, firstOrder.length - right_num, firstOrder.length);
        int[] mid_right = Arrays.copyOfRange(midOrder,midOrder.length - right_num, midOrder.length);
        BinaryTreeNode tree = new BinaryTreeNode(root_value);
        tree.left = reconstruct(first_left, mid_left);
        tree.right = reconstruct(first_right, mid_right);
        return tree;
    }
}

/***
 * 二叉树的实现类
 * 最好不要作为内部类实现，作为内部类的话，main() 方法里无法直接创建这个类的对象
 */
class BinaryTreeNode{
    int data;
    BinaryTreeNode left;
    BinaryTreeNode right;
    public BinaryTreeNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
    public BinaryTreeNode(int data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
    public void firstRootShow(){
        show(this);
        System.out.println();
    }
    private void show(BinaryTreeNode root){
        if (root != null){
            System.out.print(root.data);
            System.out.print(", ");
            show(root.left);
            show(root.right);
        }
    }
}
