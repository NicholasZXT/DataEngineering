//Given a binary tree, return the bottom-up level order traversal of its nodes' 
//values. (ie, from left to right, level by level from leaf to root). 
//
// 
//For example: 
//Given binary tree [3,9,20,null,null,15,7], 
// 
//    3
//   / \
//  9  20
//    /  \
//   15   7
// 
// 
// 
//return its bottom-up level order traversal as: 
// 
//[
//  [15,7],
//  [9,20],
//  [3]
//]
// 
// Related Topics Tree Breadth-first Search 
// 👍 1503 👎 213


package LeetCode.leetcode.editor.en;

import qianfeng.Tree;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList; // LinkedList是Queue这个接口的实现类


public class Q107_BinaryTreeLevelOrderTraversalIi{
  public static void main(String[] args) {
       Solution solution = new Q107_BinaryTreeLevelOrderTraversalIi().new Solution();
//       测试案例一
//           3
//          / \
//         9  20
//           /  \
//          15   7
//       TreeNode tree = new TreeNode(3);
//       tree.left = new TreeNode(9); tree.left.left = null; tree.left.right = null;
//       tree.right = new TreeNode(20);
//       tree.right.left = new TreeNode(15); tree.right.left.left = null; tree.right.left.right = null;
//       tree.right.right = new TreeNode(7); tree.right.right.left = null; tree.right.right.right = null;

//       测试案例二, [1,2,3,4,null,null,5]， 这个案例要特别注意
//           1
//          / \
//         2   3
//        /     \
//       4       5
      TreeNode tree = new TreeNode(1);
      tree.left = new TreeNode(2);
      tree.left.left = new TreeNode(4); tree.left.left.left = null; tree.left.left.right = null;
      tree.left.right = null;
      tree.right = new TreeNode(3);
      tree.right.left = null;
      tree.right.right = new TreeNode(5); tree.right.right.left = null; tree.right.right.right = null;

//       测试案例三
//           1
//          / \
//         2   3
//        / \
//       4   5
//      TreeNode tree = new TreeNode(1);
//      tree.left = new TreeNode(2);
//      tree.left.left = new TreeNode(4); tree.left.left.left = null; tree.left.left.right = null;
//      tree.left.right = new TreeNode(5); tree.left.right.left = null; tree.left.right.right = null;
//      tree.right = new TreeNode(3); tree.right.left = null; tree.right.right = null;

//       先序遍历打印树
//       PreTraverseBinaryTree(tree);
//      System.out.println();

//      层序遍历打印树
      LateralTraverseBinaryTree(tree);
      System.out.println();


//      获取并打印
//       List<List<Integer>> result = solution.levelOrderBottom(tree);
//       for (List<Integer> list: result){
//           System.out.println(Arrays.toString(list.toArray()));
//       }
  }

//leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public List<List<Integer>> levelOrderBottom(TreeNode root) {
            List<List<Integer>> result = new ArrayList<>();
//        这里注意List的初始化
            result.add(Arrays.asList(1,2,3));

            Queue<TreeNode> queue = new LinkedList<>();
            queue.add(root);
            while (!queue.isEmpty()){

            }


            return result;
        }


    }
//leetcode submit region end(Prohibit modification and deletion)


    /**
     * 下面这个实现的是每棵子树的同一个级别能同时输出，见案例二，而不是题目要求的那样
     */
    class Solution_other {
        public List<List<Integer>> levelOrderBottom(TreeNode root) {
            List<List<Integer>> result = new ArrayList<>();
//        这里注意List的初始化
//            result.add(Arrays.asList(1,2,3));
            int root_val = PreTraveseBTreeStack(root, result);
            List<Integer> sublist = new ArrayList<>();
            if(root_val != -1) sublist.add(root_val);
            if(!sublist.isEmpty()) result.add(sublist);
            return result;
        }
        public int PreTraveseBTreeStack(TreeNode root, List<List<Integer>> list){
            if (root == null) return -1;
            List<Integer> sublist = new ArrayList<Integer>();
            int left = PreTraveseBTreeStack(root.left, list);
            int right = PreTraveseBTreeStack(root.right, list);
            if(left != -1) sublist.add(left);
            if(right != -1) sublist.add(right);

//            这里调用的是传入的引用对象list本身的方法， 所以它会影响调用这个函数时传入的对象
            if(!sublist.isEmpty()) list.add(sublist);
            return root.val;
        }
    }


    /**
     * 先序遍历打印二叉树
     */
    public static void PreTraverseBinaryTree(TreeNode root){
        if(root == null) {System.out.print("null, "); return;}
        System.out.print(root.val + ", ");
        PreTraverseBinaryTree(root.left);
        PreTraverseBinaryTree(root.right);
    }

    /**
     * 层序遍历二叉树
     * @param root
     */
    public static void LateralTraverseBinaryTree(TreeNode root){
//        LinkedList是Queue这个接口的实现类
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode tree ;
        queue.add(root);
        while (!queue.isEmpty()){
            tree = queue.poll();
            if( tree != null){
                System.out.print(tree.val + ", ");
                queue.add(tree.left);
                queue.add(tree.right);
            }
        }
    }

    /**
     *  Definition for TreeNode
     */
     static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() { }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
     }


}