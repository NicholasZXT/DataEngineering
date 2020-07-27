//Given a binary tree and a sum, determine if the tree has a root-to-leaf path s
//uch that adding up all the values along the path equals the given sum. 
//
// Note: A leaf is a node with no children. 
//
// Example: 
//
// Given the below binary tree and sum = 22, 
//
// 
//      5
//     / \
//    4   8
//   /   / \
//  11  13  4
// /  \      \
//7    2      1
// 
//
// return true, as there exist a root-to-leaf path 5->4->11->2 which sum is 22. 
//
// Related Topics Tree Depth-first Search 
// 👍 1962 👎 499


package LeetCode.leetcode.editor.en;

import qianfeng.Tree;

import java.util.LinkedList;
import java.util.Queue;

public class Q112_PathSum{
  public static void main(String[] args) {
       Solution solution = new Q112_PathSum().new Solution();

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

//       测试案例二， sum=22
//             5
//            / \
//           4   8
//          /   / \
//         11  13  4
//        /  \      \
//       7    2      1
//      TreeNode tree = new TreeNode(5);
//      tree.left = new TreeNode(4);
//      tree.left.left = new TreeNode(11); tree.left.right = null;
//      tree.left.left.left = new TreeNode(7, null, null); tree.left.left.right = new TreeNode(2, null, null);
//      tree.right = new TreeNode(8);
//      tree.right.left = new TreeNode(13, null, null);
//      tree.right.right = new TreeNode(4);
//      tree.right.right.left = null; tree.right.right.right = new TreeNode(1,null,null);

      TreeNode tree = new TreeNode(1);
      tree.left = new TreeNode(2,null,null); tree.right = null;

//      层序遍历打印树
//      LateralTraverseBinaryTree(tree);
//      System.out.println();

//      System.out.println("result is : " + solution.hasPathSum(tree,22));
      System.out.println("result is : " + solution.hasPathSum(tree,1));

  }
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if( root == null ) return false;
        if( root.val == sum & root.left == null & root.right == null) return true;
        boolean left = hasPathSum(root.left,sum - root.val);
        boolean right  = hasPathSum(root.right, sum - root.val);
        return left | right;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

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