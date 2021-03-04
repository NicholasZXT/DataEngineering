//Given two binary trees, write a function to check if they are the same or not.
// 
//
// Two binary trees are considered the same if they are structurally identical a
//nd the nodes have the same value. 
//
// Example 1: 
//
// 
//Input:     1         1
//          / \       / \
//         2   3     2   3
//
//        [1,2,3],   [1,2,3]
//
//Output: true
// 
//
// Example 2: 
//
// 
//Input:     1         1
//          /           \
//         2             2
//
//        [1,2],     [1,null,2]
//
//Output: false
// 
//
// Example 3: 
//
// 
//Input:     1         1
//          / \       / \
//         2   1     1   2
//
//        [1,2,1],   [1,1,2]
//
//Output: false
// 
// Related Topics Tree Depth-first Search 
// 👍 2231 👎 64


package LeetCode.solutions;

import java.util.LinkedList;
import java.util.Queue;


/**
 * 这道题有点意思，能做出来，但是没有想象的那么简单
 */
public class Q100_SameTree{
  public static void main(String[] args) {
       Solution solution = new Q100_SameTree().new Solution();

//       测试案例一
//           3
//          / \
//         9  20
//           /  \
//          15   7
      TreeNode tree1 = new TreeNode(3);
      tree1.left = new TreeNode(9, null, null);
      tree1.right = new TreeNode(20);
      tree1.right.left = new TreeNode(15, null, null);
      tree1.right.right = new TreeNode(7, null, null);

      TreeNode tree2 = new TreeNode(3);
      tree2.left = new TreeNode(9, null, null);
      tree2.right = new TreeNode(20);
      tree2.right.left = new TreeNode(15, null, null);
      tree2.right.right = new TreeNode(7, null, null);


//       测试案例二
//Input:     1         1
//          /           \
//         2             2
//      TreeNode tree1 = new TreeNode(1);
//      tree1.left = new TreeNode(2, null, null);
//      tree1.right = null;
//
//      TreeNode tree2 = new TreeNode(1);
//      tree2.left = null;
//      tree2.right = new TreeNode(2,null,null);

//       测试案例三
//Input:     1         1
//          / \       / \
//         2   1     1   2
//      TreeNode tree1 = new TreeNode(1);
//      tree1.left = new TreeNode(2,null,null);
//      tree1.right = new TreeNode(1, null, null);
//
//      TreeNode tree2 = new TreeNode(1);
//      tree2.left = new TreeNode(1,null,null);
//      tree2.right = new TreeNode(2,null,null);

      System.out.println("result is : " + solution.isSameTree(tree1, tree2));
       
  }
  
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if( p == null & q == null ) return true;
        if( p == null | q == null ) return false;
//      先比较根节点的值是否相等
        if( !(p.val == q.val) ) return false;
//      再比较左子树是否相等
        if( !isSameTree(p.left, q.left) ) return false;
        if( !isSameTree(p.right, q.right) ) return false;
        return true;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

    /**
     * 层序遍历二叉树
     * @param root`
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