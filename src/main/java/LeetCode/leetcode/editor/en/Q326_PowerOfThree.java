//Given an integer, write a function to determine if it is a power of three. 
//
// Example 1: 
//
// 
//Input: 27
//Output: true
// 
//
// Example 2: 
//
// 
//Input: 0
//Output: false 
//
// Example 3: 
//
// 
//Input: 9
//Output: true 
//
// Example 4: 
//
// 
//Input: 45
//Output: false 
//
// Follow up: 
//Could you do it without using any loop / recursion? Related Topics Math 
// 👍 515 👎 1469


package LeetCode.leetcode.editor.en;
public class Q326_PowerOfThree{
  public static void main(String[] args) {
       Solution solution = new Q326_PowerOfThree().new Solution();
       int n = 27;
//       int n = 0;
//       int n = 1;
//       int n = 9;
//       int n = 45;
//       int n = 19684;
      System.out.println(19684/3);
      System.out.println(n + ", result is : " + solution.isPowerOfThree(n));

  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPowerOfThree(int n) {
//        if(n == 0) return false;
        if(n == 1) return true;
        int res = n/3;
        int mode = n%3;

        while (res >= 3 | mode != 0){
            mode = res%3;
            res = res/3;
        }
        return res == 1 & mode == 0;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}