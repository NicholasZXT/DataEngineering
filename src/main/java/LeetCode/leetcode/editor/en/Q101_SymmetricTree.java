//Given a binary tree, check whether it is a mirror of itself (ie, symmetric aro
//und its center). 
//
// For example, this binary tree [1,2,2,3,4,4,3] is symmetric: 
//
// 
//    1
//   / \
//  2   2
// / \ / \
//3  4 4  3
// 
//
// 
//
// But the following [1,2,2,null,3,null,3] is not: 
//
// 
//    1
//   / \
//  2   2
//   \   \
//   3    3
// 
//
// 
//
// Follow up: Solve it both recursively and iteratively. 
// Related Topics Tree Depth-first Search Breadth-first Search 
// 👍 4228 👎 104


package LeetCode.leetcode.editor.en;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 这道题也有点意思，可以写出来，不过有些地方需要注意
 * 和二叉树的层序遍历有关
 */
public class Q101_SymmetricTree{
  public static void main(String[] args) {
       Solution solution = new Q101_SymmetricTree().new Solution();

//       测试案例一
//           1
//          / \
//         2   2
//        / \ / \
//       3  4 4  3
      TreeNode tree = new TreeNode(1);
      tree.left = new TreeNode(2);
      tree.left.left = new TreeNode(3,null,null);
      tree.left.right = new TreeNode(4, null,null);
      tree.right = new TreeNode(2);
      tree.right.left = new TreeNode(4,null,null);
      tree.right.right = new TreeNode(3,null,null);

//       测试案例二
//          1
//         / \
//        2   2
//         \   \
//         3    3
//      TreeNode tree = new TreeNode(1);
//      tree.left = new TreeNode(2);
//      tree.left.left = null;
//      tree.left.right = new TreeNode(4, null,null);
//      tree.right = new TreeNode(2);
//      tree.right.left = null;
//      tree.right.right = new TreeNode(3,null,null);

      System.out.println("result is :" + solution.isSymmetric(tree));
       
  }
  
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isSymmetric(TreeNode root) {
        if(root == null) return true;
        Queue<TreeNode> queue = new LinkedList<>();
        TreeNode tree ;
        ArrayList<Integer> levelVals;
        int levelNum;
        queue.add(root);
//        while循环的是层
        while (!queue.isEmpty()){
            levelNum = queue.size();
            levelVals = new ArrayList<Integer>(levelNum);
//            for循环的是每一层的节点
            for (int i = 0; i < levelNum; i++){
                tree = queue.poll();
                if( tree != null){
                    queue.add(tree.left); queue.add(tree.right);
                    levelVals.add(tree.val);
                }else {
//                    注意这里，需要加上节点为空时的值
                    levelVals.add(null);
                }
            }
//          处理每一层节点的值，看是否对称
            for(int i = 0; i < levelNum/2; i++){
                if(levelVals.get(i) != levelVals.get(levelNum-1 - i)) return false;
            }
        }
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