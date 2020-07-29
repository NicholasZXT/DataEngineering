//Given a positive integer num, write a function which returns True if num is a 
//perfect square else False. 
//
// Follow up: Do not use any built-in library function such as sqrt. 
//
// 
// Example 1: 
// Input: num = 16
//Output: true
// Example 2: 
// Input: num = 14
//Output: false
// 
// 
// Constraints: 
//
// 
// 1 <= num <= 2^31 - 1 
// 
// Related Topics Math Binary Search 
// 👍 897 👎 169


package LeetCode.leetcode.editor.en;

/**
 * 这道题的解法一个数学问题，高效的实现才是一个编程问题
 * 1 = 1
 * 4 = 1 + 3
 * 9 = 1 + 3 + 5
 * 16 = 1 + 3 + 5 + 7
 * ....
 * n^2 = 1 + 3 + ... + (2n-1) = n/2*(1 + (2n-1))
 */
public class Q367_ValidPerfectSquare{
  public static void main(String[] args) {
       Solution solution = new Q367_ValidPerfectSquare().new Solution();
//       int num = 16;
       int num = 169;
      System.out.println("result is : " + solution.isPerfectSquare(num));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPerfectSquare(int num) {
        int i = 1;
        while (num > 0) {
            num -= i;
            i += 2;
        }
        return num == 0;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}