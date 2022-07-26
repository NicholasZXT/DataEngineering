//Invert a binary tree. 
//
// Example: 
//
// Input: 
//
// 
//     4
//   /   \
//  2     7
// / \   / \
//1   3 6   9 
//
// Output: 
//
// 
//     4
//   /   \
//  7     2
// / \   / \
//9   6 3   1 
//
// Trivia: 
//This problem was inspired by this original tweet by Max Howell: 
//
// Google: 90% of our engineers use the software you wrote (Homebrew), but you c
//an’t invert a binary tree on a whiteboard so f*** off. 
// Related Topics Tree 
// 👍 3428 👎 55


package Coding.solutions;

import java.util.Queue;
import java.util.LinkedList; // LinkedList是Queue这个接口的实现类


/**
 * 这个比较简单
 */
public class Q226_InvertBinaryTree{
  public static void main(String[] args) {
       Solution solution = new Q226_InvertBinaryTree().new Solution();

//       测试案例一
//           3
//          / \
//         9  20
//           /  \
//          15   7
      TreeNode tree = new TreeNode(3);
      tree.left = new TreeNode(9, null, null);
      tree.right = new TreeNode(20);
      tree.right.left = new TreeNode(15, null, null);
      tree.right.right = new TreeNode(7, null, null);

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

//   测试案例三，
//         4
//       /   \
//      2     7
//     / \   / \
//    1   3 6   9
//      TreeNode tree = new TreeNode(4);
//      tree.left = new TreeNode(2);
//      tree.left.left = new TreeNode(1,null, null);
//      tree.left.right = new TreeNode(3,null, null);
//      tree.right = new TreeNode(7);
//      tree.right.left = new TreeNode(6, null, null);
//      tree.right.right = new TreeNode(9, null, null);

//      层序遍历打印树
      System.out.println("原始树为：");
      LateralTraverseBinaryTree(tree);
      System.out.println();

      System.out.println("反转后为：");
      LateralTraverseBinaryTree(solution.invertTree(tree));
      System.out.println();
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if(root == null) return null;
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);
        return root;
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