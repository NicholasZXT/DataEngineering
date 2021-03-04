//Given an integer, write a function to determine if it is a power of two. 
//
// Example 1: 
//
// 
//Input: 1
//Output: true 
//Explanation: 20 = 1
// 
//
// Example 2: 
//
// 
//Input: 16
//Output: true
//Explanation: 24 = 16 
//
// Example 3: 
//
// 
//Input: 218
//Output: false 
// Related Topics Math Bit Manipulation 
// 👍 910 👎 197

/**
 * 这道题和326题类似
 */
package LeetCode.solutions;
public class Q231_PowerOfTwo{
  public static void main(String[] args) {
       Solution solution = new Q231_PowerOfTwo().new Solution();
//       int n = 0;
//       int n = 1;
//       int n = 2;
//       int n = 16;
//       int n = 3;
       int n = 218;
      System.out.println(n + ", result is " + solution.isPowerOfTwo(n));
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPowerOfTwo(int n) {
        if(n < 1) return false;
        int mode = n % 2;
        while (mode == 0){
            n = n / 2;
            mode = n % 2;
        }
        return n == 1;
        
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}