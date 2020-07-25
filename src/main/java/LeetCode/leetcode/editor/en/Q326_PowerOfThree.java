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

/**
这道题看着简单，但实际上不好做
类似的还有231题
 */
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
//      System.out.println(19684/3);
      System.out.println(2 / 3);
      System.out.println(n + ", result is : " + solution.isPowerOfThree(n));

  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPowerOfThree(int n) {
        if(n < 1) return false;
        int mode = n % 3;
//        while循环的判断用mode，但是最终结果的判断用 n
//        当余数为0时一直除下去
        while (mode == 0){
            n = n / 3;
            mode = n % 3;
        }
        return n == 1 ;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}