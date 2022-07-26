//Given a binary tree, find its minimum depth. 
//
// The minimum depth is the number of nodes along the shortest path from the roo
//t node down to the nearest leaf node. 
//
// Note: A leaf is a node with no children. 
//
// Example: 
//
// Given binary tree [3,9,20,null,null,15,7], 
//
// 
//    3
//   / \
//  9  20
//    /  \
//   15   7 
//
// return its minimum depth = 2. 
// Related Topics Tree Depth-first Search Breadth-first Search 
// 👍 1441 👎 692


package Coding.solutions;

/**
 * 这个比较简单
 */
public class Q111_MinimumDepthOfBinaryTree{
  public static void main(String[] args) {
       Solution solution = new Q111_MinimumDepthOfBinaryTree().new Solution();
//       测试案例一
//           3
//          / \
//         9  20
//           /  \
//          15   7
//      TreeNode tree = new TreeNode(3);
//      tree.left = new TreeNode(9,null,null);
//      tree.right = new TreeNode(20);
//      tree.right.left = new TreeNode(15, null,null);
//      tree.right.right = new TreeNode(7,null,null);

//       测试案例二, [1,2,3,4,null,null,5]
//           1
//          / \
//         2   3
//        /     \
//       4       5
      TreeNode tree = new TreeNode(1);
      tree.left = new TreeNode(2);
      tree.left.left = new TreeNode(4, null, null);
      tree.left.right = null;
      tree.right = new TreeNode(3);
      tree.right.left = null;
      tree.right.right = new TreeNode(5, null, null);

// 测试案例三，[1,2,2,3,3,null,null,4,4]:
//            1
//           / \
//          2   2
//         / \
//        3   3
//       / \
//      4   4
//      TreeNode tree = new TreeNode(1);
//      tree.left = new TreeNode(2);
//      tree.left.left = new TreeNode(3);
//      tree.left.left.left = new TreeNode(4,null, null);
//      tree.left.left.right = new TreeNode(4, null, null);
//      tree.left.right = new TreeNode(3, null, null);
//      tree.right = new TreeNode(2, null ,null);

      int minimum_depth = solution.MinDepth(tree);
      System.out.println("minimum depth is : " + minimum_depth);

  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int minDepth(TreeNode root) {
        int min_depth = MinDepth(root);
        return min_depth;
    }

    public int MinDepth(TreeNode root){
        if( root == null) return 0;
//        System.out.print(root.val + ",  ");
        int left = MinDepth(root.left);
        int right = MinDepth(root.right);

//        这一句要特别注意，左右子树有一个为0的时候，不能直接取最小值，而是需要去掉0
        if(left == 0 | right == 0 ) return 1+ Math.max(left, right);

        return 1 + Math.min(left, right);

    }

}
//leetcode submit region end(Prohibit modification and deletion)


//  Definition for a binary tree node.
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