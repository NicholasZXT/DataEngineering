package Coding.sword.offer;

/**
 * 输入一个二叉树，输出它的镜像
 */
public class Q19MirrorOfBinaryTree<T> {
    public static void main(String[] args){
        // 手动创建两个二叉树
        BinaryTree<Integer> tree1 = new BinaryTree<>(8);
        tree1.left = new BinaryTree<Integer>(8);
        tree1.left.left = new BinaryTree<>(9);
        tree1.left.right = new BinaryTree<>(2);
        tree1.left.right.left = new BinaryTree<>(4);
        tree1.left.right.right = new BinaryTree<>(7);
        tree1.right = new BinaryTree<>(7);
        BinaryTree<Integer> tree2 = new BinaryTree<>(8);
        tree2.left = new BinaryTree<Integer>(9);
        tree2.left.left = new BinaryTree<Integer>(2);
        BinaryTree<Integer> tree3 = new BinaryTree<>(1);
        BinaryTree<Integer> tree4 = null;
        // 测试
        Q19MirrorOfBinaryTree<Integer> solver = new Q19MirrorOfBinaryTree<>();
        tree1.firstOrderShow();
        solver.reverseBinaryTree(tree1);
        tree1.firstOrderShow();
        tree2.firstOrderShow();
        solver.reverseBinaryTree(tree2);
        tree2.firstOrderShow();
        tree3.firstOrderShow();
        solver.reverseBinaryTree(tree3);
        tree3.firstOrderShow();
        solver.reverseBinaryTree(tree4);

    }

    public void reverseBinaryTree(BinaryTree<T> tree){
        if (tree == null)
            return;
        reverseBinaryTree(tree.left);
        reverseBinaryTree(tree.right);
        BinaryTree<T> temp = tree.left;
        tree.left = tree.right;
        tree.right = temp;
    }

    // 内部静态类实现二叉树
    public static class BinaryTree<T>{
        T data;
        BinaryTree<T> left;
        BinaryTree<T> right;
        public BinaryTree(T data){
            this.data = data;
            this.left = null;
            this.right = null;
        }
        public BinaryTree(T data, BinaryTree<T> left, BinaryTree<T> right){
            this.data = data;
            this.left = left;
            this.right = right;
        }
        public void firstOrderShow(){
            show(this);
            System.out.println();
        }
        private void show(BinaryTree<T> root){
            if (root != null){
                System.out.print(root.data + ", ");
                show(root.left);
                show(root.right);
            }
        }
    }
}
