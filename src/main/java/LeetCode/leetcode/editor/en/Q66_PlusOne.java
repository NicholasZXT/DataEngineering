//Given a non-empty array of digits representing a non-negative integer, increme
//nt one to the integer. 
//
// The digits are stored such that the most significant digit is at the head of 
//the list, and each element in the array contains a single digit. 
//
// You may assume the integer does not contain any leading zero, except the numb
//er 0 itself. 
//
// Example 1: 
//
// 
//Input: [1,2,3]
//Output: [1,2,4]
//Explanation: The array represents the integer 123.
// 
//
// Example 2: 
//
// 
//Input: [4,3,2,1]
//Output: [4,3,2,2]
//Explanation: The array represents the integer 4321.
// 
// Related Topics Array 
// 👍 1604 👎 2462   <-------------------------- 这道题估计受到了吐槽


package LeetCode.leetcode.editor.en;

import java.util.Arrays;

/**
 *这道题看似无意义，但是要注意边界条件
 */
public class Q66_PlusOne{
  public static void main(String[] args) {
       Solution solution = new Q66_PlusOne().new Solution();
//       int[] digits = {1,2,3};
//       int[] digits = {4,3,2,1};
//      下面几个例子才是这道题的重点
//       int[] digits = {9,9,9};
       int[] digits = {9,9};
//       int[] digits = {9};
//        int[] digits = {0};
//        int[] digits = {1};
      System.out.println("result is : " + Arrays.toString(solution.plusOne(digits)));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] plusOne(int[] digits) {
        int[] result = {};
        int len = digits.length;

        if( len == 0) return result;

        if( digits[len - 1] + 1 < 10) {
//            快速复制数组
            result = digits.clone();
            result[len - 1] = result[len - 1] + 1;
            return result;
        }
//        接下来才是重头戏
        digits[len-1] = 0;
        for( int i = digits.length - 2; i >= 0; i--){
            if( digits[i] + 1 < 10){
                result = digits.clone();
                result[i] = result[i] + 1;
                return result;
            }else {
                digits[i] = 0;
            }
        }

        if(digits[0] == 0){
            result = new int[len+1];
            result[0] = 1;
            for( int i = 1; i < len+1; i++) result[i] = 0;
        }
        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}