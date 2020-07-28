//You are climbing a stair case. It takes n steps to reach to the top. 
//
// Each time you can either climb 1 or 2 steps. In how many distinct ways can yo
//u climb to the top? 
//
// Example 1: 
//
// 
//Input: 2
//Output: 2
//Explanation: There are two ways to climb to the top.
//1. 1 step + 1 step
//2. 2 steps
// 
//
// Example 2: 
//
// 
//Input: 3
//Output: 3
//Explanation: There are three ways to climb to the top.
//1. 1 step + 1 step + 1 step
//2. 1 step + 2 steps
//3. 2 steps + 1 step
// 
//
// 
// Constraints: 
//
// 
// 1 <= n <= 45 
// 
// Related Topics Dynamic Programming 
// 👍 4447 👎 143


package LeetCode.leetcode.editor.en;

/**
 * 这道题的解法众多，可以好好研究下
 */
public class Q70_ClimbingStairs{
  public static void main(String[] args) {
       Solution solution = new Q70_ClimbingStairs().new Solution();
//       int n = 3;
       int n = 44;  //结果应当为: 1134903170
      System.out.println("result is : " + solution.climbStairs(n));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

//  这个解法是官方的，但是也会超时
    public int climbStairs(int n) {
        return recursiveClimbStairs(0, n);
    }

    // n是当前的步数，n_final是最终爬的台阶数
    public int recursiveClimbStairs(int n, int n_final){
        if( n > n_final) return 0;
//        注意这个条件
        if( n == n_final) return 1;
        int step_1 = recursiveClimbStairs(n + 1, n_final);
        int step_2 = recursiveClimbStairs(n + 2, n_final);
        return step_1 + step_2;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

    /**
     * 下面这个解法没问题，但是会报超时
     */
    class Solution_1 {
        public int climbStairs(int n) {
            int step_1 = recursiveClimbStairs(n, 1);
            int step_2 = recursiveClimbStairs(n, 2);
            return step_1 + step_2;
        }
//        n是剩下的步数，num_step是下一步走的步数
        public int recursiveClimbStairs(int n, int num_step){
            if( n <= 0 ) return 0;
            if( n - num_step == 0) return 1;
            int step_1 = recursiveClimbStairs(n - num_step, 1);
            int step_2 = recursiveClimbStairs(n - num_step,2);
            return step_1 + step_2;
        }
    }

}