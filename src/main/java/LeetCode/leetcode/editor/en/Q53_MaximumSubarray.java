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
        int start = 0;
        int end = 0;
        int sum = 0;
        int i = start;
        int j = end;
        while( end < nums.length){
            if(nums[j] >= nums[i]){
                j++;
            }
        }


        return sum;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}