package LeetCode.SwordOffer;

import scala.None;

public class Q6_ReconstructBinaryTree {
    public static void main(String[] args){
        BinaryTreeNode tree = new BinaryTreeNode(1);


    }

    //public BinaryTree reconstruct(int[] preOrder, int[] midOrder){
    //
    //
    //}

    public class BinaryTreeNode{
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
        public void midOrderShow(){
            show(this);
        }
        private void show(BinaryTreeNode root){
            if (root != null){
                show(root.left);
                System.out.print(root.data);
                System.out.print(", ");
                show(root.right);
            }
        }
    }
}
