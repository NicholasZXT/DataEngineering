//Given a binary tree, return all root-to-leaf paths. 
//
// Note: A leaf is a node with no children. 
//
// Example: 
//
// 
//Input:
//
//   1
// /   \
//2     3
// \
//  5
//
//Output: ["1->2->5", "1->3"]
//
//Explanation: All root-to-leaf paths are: 1->2->5, 1->3
// Related Topics Tree Depth-first Search 
// 👍 1725 👎 108


package LeetCode.leetcode.editor.en;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList; // LinkedList是Queue这个接口的实现类
import java.util.Arrays;


/**
 * 这道题有点难度，需要点时间
 */
public class Q257_BinaryTreePaths{
  public static void main(String[] args) {
       Solution solution = new Q257_BinaryTreePaths().new Solution();

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

//       测试案例一
//         1
//       /   \
//      2     3
//       \
//        5
//      TreeNode tree = new TreeNode(1);
//      tree.left = new TreeNode(2);
//      tree.left.left = null;
//      tree.left.right = new TreeNode(5,null, null);
//      tree.right = new TreeNode(3, null, null);

//      LateralTraverseBinaryTree(tree);
//      System.out.println();

      List<String> result = solution.binaryTreePaths(tree);
//      System.out.println(result.size());
      System.out.println(Arrays.toString(result.toArray()));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<String> binaryTreePaths(TreeNode root) {
        List<String> list = new ArrayList<>();
        if(root == null) return list;

        List<String> list_left = binaryTreePaths(root.left);
        List<String> list_right = binaryTreePaths(root.right);

//      注意处理左右子树都为空的情况
        if(list_left.isEmpty() & list_right.isEmpty()) list.add(Integer.toString(root.val));

        for(int i = 0; i < list_left.size(); i++){
            String t = list_left.get(i);
            list.add(root.val + "->" + list_left.get(i));
        }
        for(int i = 0; i < list_right.size(); i++){
            String t = list_right.get(i);
            list.add(root.val + "->" + list_right.get(i));
        }

        return list;
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