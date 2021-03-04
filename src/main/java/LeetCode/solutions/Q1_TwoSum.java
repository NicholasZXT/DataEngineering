  //Given an array of integers, return indices of the two numbers such that they a
//dd up to a specific target. 
//
// You may assume that each input would have exactly one solution, and you may n
//ot use the same element twice. 
//
// Example: 
//
// 
//Given nums = [2, 7, 11, 15], target = 9,
//
//Because nums[0] + nums[1] = 2 + 7 = 9,
//return [0, 1].
// 
// Related Topics Array Hash Table 
// 👍 15537 👎 560


package LeetCode.solutions;

import java.util.HashMap;
import java.util.Map;

public class Q1_TwoSum {
    public static void main(String[] args) {
        Solution solution = new Q1_TwoSum().new Solution();
    }

    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public int[] twoSum(int[] nums, int target) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int i = 0; i < nums.length; i++) {

                int complement = target - nums[i];
                if (map.containsKey(complement)) {
                    // if(map.get(complement) != i){
                    return new int[]{i, map.get(complement)};
                    // }
                }
                map.put(nums[i], i);
            }
            throw new IllegalArgumentException("No two sum solution");
        }
    }
//leetcode submit region end(Prohibit modification and deletion)
}