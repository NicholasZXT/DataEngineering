//Given a binary tree, find its maximum depth. 
//
// The maximum depth is the number of nodes along the longest path from the root
// node down to the farthest leaf node. 
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
// return its depth = 3. 
// Related Topics Tree Depth-first Search 
// 👍 2560 👎 74


package LeetCode.solutions;
public class Q104_MaximumDepthOfBinaryTree{
  public static void main(String[] args) {
       Solution solution = new Q104_MaximumDepthOfBinaryTree().new Solution();
       TreeNode tree = new TreeNode(3);
       tree.left = new TreeNode(9); tree.left.left=null; tree.left.right=null;
       tree.right = new TreeNode(20);
       tree.right.left = new TreeNode(15); tree.right.left.left = null; tree.right.left.right = null;
       tree.right.right = new TreeNode(7); tree.right.right.left = null; tree.right.right.right = null;
       System.out.println(solution.maxDepth(tree));
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int maxDepth(TreeNode root) {
        int depth=0;
        if(root == null) return 0;
        int left_depth = 1 + maxDepth(root.left);
        int right_depth = 1 + maxDepth(root.right);
        depth = Math.max(left_depth,right_depth);
        return depth;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

//    Definition for a binary tree node.
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