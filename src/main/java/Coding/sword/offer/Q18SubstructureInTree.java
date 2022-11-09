package Coding.sword.offer;


/**
 * 对比两个二叉树，看是否含有子结构：这一题有一点难度，不过还是能自己写出来
 */
public class Q18SubstructureInTree<T> {
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
        tree2.right = new BinaryTree<Integer>(2);
        tree1.firstOrderShow();
        tree2.firstOrderShow();

        // 测试
        Q18SubstructureInTree<Integer> solver = new Q18SubstructureInTree<>();
        boolean result = solver.searchSubtree(tree1, tree2);
        System.out.println("result: " + result);
    }

    public boolean searchSubtree(BinaryTree<T> tree1, BinaryTree<T> tree2){
        /**
         * 先查找根节点相同的地方，找到之后在判断是否为子树
         */
        boolean result = false;
        if (tree1 == null | tree2 == null)
            return result;
        if (tree1.data == tree2.data){
            result = isSubtree(tree1, tree2);
        }
        boolean left_result = searchSubtree(tree1.left, tree2);
        boolean right_result = searchSubtree(tree1.right, tree2);
        result = result | left_result | right_result;
        //result = result | searchSubtree(tree1.left, tree2) | searchSubtree(tree1.right, tree2);
        return result;
    }

    private boolean isSubtree(BinaryTree<T> tree1, BinaryTree<T> tree2){
        /**
         * 判断是否为子树，这个写的时候需要注意一点
         */
        if (tree2 == null)
            return true;
        if (tree1 == null)
            return false;
        boolean result;
        if (tree1.data == tree2.data){
            result = isSubtree(tree1.left, tree2.left) & isSubtree(tree1.right, tree2.right);
        }else {
            result = false;
        }
        return result;
    }

    // 使用内部静态类来作为二叉树的实现
    public static class BinaryTree<T> {
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
            if(root != null){
                System.out.print(root.data + ", ");
                show(root.left);
                show(root.right);
            }
        }
    }
}
