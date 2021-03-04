//Implement int sqrt(int x). 
//
// Compute and return the square root of x, where x is guaranteed to be a non-ne
//gative integer. 
//
// Since the return type is an integer, the decimal digits are truncated and onl
//y the integer part of the result is returned. 
//
// Example 1: 
//
// 
//Input: 4
//Output: 2
// 
//
// Example 2: 
//
// 
//Input: 8
//Output: 2
//Explanation: The square root of 8 is 2.82842..., and since 
//             the decimal part is truncated, 2 is returned.
// 
// Related Topics Math Binary Search 
// 👍 1360 👎 1926


package LeetCode.solutions;

/**
 * 和Q367有关，但是有个小地方需要注意
 */
public class Q69_Sqrtx{
  public static void main(String[] args) {
       Solution solution = new Q69_Sqrtx().new Solution();
//       int x = 2;
//       int x = 8;
//       int x = 9;
//       int x = 16;
//      int x = 256;
//      int x = 10000;
       int x = 2147395600;  //结果为46340，这里要注意溢出问题
//      System.out.println("Integer.MAX_VALUE is : " + Integer.MAX_VALUE); // 最大值为 2147483647
      System.out.println(x + ", result is : " + solution.mySqrt(x));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int mySqrt(int x) {
        int n = 0, sum = 0;
        int diff = x - sum;
        while(diff >= 0){
            n++;
            diff -= (2*n - 1);
        }
//        采用下面这种的话，在数字很大的时候会出现溢出
//        while(sum <= x){
//            n++;
//            sum += (2*n - 1); // 这一步有可能出现溢出
//        }
//        System.out.println("sum is :" + sum);
        return n - 1;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}