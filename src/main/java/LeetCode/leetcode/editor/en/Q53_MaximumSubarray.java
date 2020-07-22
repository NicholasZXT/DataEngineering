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


package LeetCode.leetcode.editor.en;
public class Q53_MaximumSubarray{
  public static void main(String[] args) {
       Solution solution = new Q53_MaximumSubarray().new Solution();
      System.out.println(solution.maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int maxSubArray(int[] nums) {
        int len = nums.length;
        int[] dp = new int[len];
        dp[0] = nums[0];
//        int max = nums[0];
//        int pre = max;
        for (int i = 1; i < len; i++){
            if( nums[i] < 0){
                dp[i] = dp[i-1];
            }else {
                dp[i] = dp[i] > 0 ? dp[i] + nums[i] : nums[i];
            }
        }
        return dp[len-1];
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}