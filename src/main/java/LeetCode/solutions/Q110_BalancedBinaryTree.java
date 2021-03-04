//Given a binary tree, determine if it is height-balanced. 
//
// For this problem, a height-balanced binary tree is defined as: 
//
// a binary tree in which the left and right subtrees of every node differ in he
//ight by no more than 1. 
//
//
// Example 1: 
//
// Given the following tree [3,9,20,null,null,15,7]:
//    3
//   / \
//  9  20
//    /  \
//   15   7 
//
// Return true. 
// 
//Example 2: 
//
// Given the following tree [1,2,2,3,3,null,null,4,4]:
//       1
//      / \
//     2   2
//    / \
//   3   3
//  / \
// 4   4
// 
//
// Return false. 
// Related Topics Tree Depth-first Search 
// 👍 2303 👎 171


package LeetCode.solutions;


/**
 * 这个比较简单
 */
public class Q110_BalancedBinaryTree{
  public static void main(String[] args) {
       Solution solution = new Q110_BalancedBinaryTree().new Solution();
//       测试案例一
//           3
//          / \
//         9  20
//           /  \
//          15   7
//      TreeNode tree = new TreeNode(3);
//      tree.left = new TreeNode(9, null, null);
//      tree.right = new TreeNode(20);
//      tree.right.left = new TreeNode(15, null, null);
//      tree.right.right = new TreeNode(7, null, null);

//       测试案例二, [1,2,3,4,null,null,5]
//           1
//          / \
//         2   3
//        /     \
//       4       5
//      TreeNode tree = new TreeNode(1);
//      tree.left = new TreeNode(2);
//      tree.left.left = new TreeNode(4, null, null);
//      tree.left.right = null;
//      tree.right = new TreeNode(3);
//      tree.right.left = null;
//      tree.right.right = new TreeNode(5, null, null);

// 测试案例三，[1,2,2,3,3,null,null,4,4]:
//            1
//           / \
//          2   2
//         / \
//        3   3
//       / \
//      4   4
      TreeNode tree = new TreeNode(1);
      tree.left = new TreeNode(2);
      tree.left.left = new TreeNode(3);
      tree.left.left.left = new TreeNode(4,null, null);
      tree.left.left.right = new TreeNode(4, null, null);
      tree.left.right = new TreeNode(3, null, null);
      tree.right = new TreeNode(2, null ,null);

      boolean result = solution.isBalanced(tree);
      System.out.println("result = " + result);

  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isBalanced(TreeNode root) {
        boolean result;
        result = Deepth(root) >= 0 ? true: false;
        return result;
    }

    public int Deepth(TreeNode root){
        if (root == null) return 0;
        int left = Deepth(root.left);
        int right = Deepth(root.right);

        if(left == -1 | right == -1) return -1;

        int diff = Math.abs(left - right);
        if( diff > 1) {
            return -1;
        }else {
            return 1 + Math.max(left, right);
        }
    }

}
//leetcode submit region end(Prohibit modification and deletion)

//      Definition for a binary tree node.
  static class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }
}