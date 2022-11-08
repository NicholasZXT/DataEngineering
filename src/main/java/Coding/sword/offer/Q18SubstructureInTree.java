package Coding.sword.offer;

public class Q18SubstructureInTree<T> {
    public static void main(String[] args){
        // 手动创建两个二叉树
        BinaryTreeNode<Integer> tree1 = new BinaryTreeNode<>();


    }

    /**
     * 对比两个二叉树，看是否含有子结构
     */
    public boolean hasSubtree(){
        return true;
    }

    // 使用内部静态类来作为二叉树的实现
    public static class BinaryTreeNode<T> {
        T data;
        BinaryTreeNode<T> left;
        BinaryTreeNode<T> right;
        public BinaryTreeNode(T data){
            this.data = data;
            this.left = null;
            this.right = null;
        }
        public BinaryTreeNode(T data, BinaryTreeNode<T> left, BinaryTreeNode<T> right){
            this.data = data;
            this.left = left;
            this.right = right;
        }
        public void firstOrderShow(){
            show(this);
            System.out.println();
        }
        public void show(BinaryTreeNode<T> root){
            if(root != null){
                System.out.print(root.data + ", ");
                show(root.left);
                show(root.right);
            }
        }
    }
}
