//Given an integer array nums, find the contiguous subarray (containing at least
// one number) which has the largest sum and return its sum. 
//
// Example: 
//
// 
//Input: [-2,1,-3,4,-1,2,1,-5,4],
//Output: 6
//Explanation: [4,-1,2,1] has the largest sum = 6.
// 
//
// Follow up: 
//
// If you have figured out the O(n) solution, try coding another solution using 
//the divide and conquer approach, which is more subtle. 
// Related Topics Array Divide and Conquer Dynamic Programming 
// 👍 7980 👎 375


package Coding.solutions;

import java.util.Arrays;

/*
这道题有点难度
 */
public class Q53_MaximumSubarray{
  public static void main(String[] args) {
       Solution solution = new Q53_MaximumSubarray().new Solution();
       int[] nums = new int[]{-2,1,-3,4,-1,2,1,-5,4};
      System.out.println(solution.maxSubArray(nums));
  }

  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int maxSubArray(int[] nums) {
        int len = nums.length;
        int[] dp = new int[len];
        dp[0] = nums[0];
        int max = nums[0];
        for (int i = 1; i < len; i++){
            dp[i] = nums[i] + (dp[i-1] > 0 ? dp[i-1] : 0);
            max = Math.max(max, dp[i]);
//            下面这个是我一开始的代码，得到的结果不对
//            if( nums[i] < 0){
//                dp[i] = dp[i-1] > 0 ? dp[i-1] : Math.max(dp[i-1], nums[i]);
//            }else {
//                dp[i] = dp[i-1] > 0 ? dp[i-1] + nums[i] : nums[i];
//            }
        }
//        快速打印数组
        System.out.println(Arrays.toString(dp));
//        return dp[len-1];
        return max;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

/*
这道题采用DP来解答，首先需要定义的就是子问题。
设数组为A，子问题定义为 maxSubArray(A, i)，记为F(i)，表示 包含A[i] 的最大子数组之和（注意这个包含），
从F(i) -> F(i+1)，必须要包括 A[i+1]，但是需要考虑F(i)的正负：
1. 如果F(i) > 0 ，那么直接有 F(i+1) = F(i) + A(i);
2. 如果F(I) < 0 , 就不能加到A[i]上，有 F(i+1) = A(i).
这样得到的数组 F[i] 里，记录的是以 A[i] 为结尾的最大子数组之和，但不是整个数组的最大子数组之和，整个数组的最大子数组之和
可能出现在 F[i] 中的某个位置，所以还需要一个变量来记录最大值。

实际上 F(i) 和 A[i] 的正负号组合一共有四种情况，我一开始的想法是：从 F[i] -> F[i+1]，有：
1. A[i] > 0, F[i] > 0, 那么 F[i+1] = F[i] + A[i]
2. A[i] > 0, F[i] < 0, 那么 F[i+1] = A[i]
3. A[i] < 0, F[i] > 0, 那么 F[i+1] = F[i]，丢弃了A[i]
4. A[i] < 0, F[i] < 0, 那么 F[i+1] = max(F[i], A[i])，可能丢弃A[i]
原本我以为这样得到的F[i]里记录的是 从0至i 的子数组中最大之和，最终的结果就放在F[i]的最后一个结果里。
但是实际上这样得到的是数组中所有正数之和。
对于数组[-2, 1, -3, 4, -1, 2, 1, -5, 4]
当i=2时，输入的数组为[-2, 1, -3]， 此时A[2]=-3, F[i-1]=1，情况3，所以F[i]=F[i-1]=1
当i=3时，输入的数组为[-2, 1, -3, 4]， 此时A[i]=4, F[i-1]=1，情况1，所以F[i]=F[i-1]+A[i]=4+1=5——这一步就有问题，
5是A[1]+A[3]的结果，并不是连续子数组。这里问题就出现在F[i]的定义上，在i=2的时候计算F[i]时，就丢弃了A[2]，导致算得的F[2]
并没有包含A[2]，不包含的话，就破坏了连续子数组这个要求。
这就是为什么上面定义F[i]的时候，必须包含A[i]作为结尾，既然A[i]必须加进去，上面那四种情况里，就只用考虑F[i]的正负了。

//        [-2, 1, -3, 4, -1, 2, 1, -5, 4]
//        [-2, 1, 1, 5, 5, 7, 8, 8, 12]
//        [-2, 1, -2, 4, 3, 5, 6, 1, 5]
*/


}