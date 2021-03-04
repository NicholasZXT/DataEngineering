//Given a sorted array and a target value, return the index if the target is fou
//nd. If not, return the index where it would be if it were inserted in order. 
//
// You may assume no duplicates in the array. 
//
// Example 1: 
//
// 
//Input: [1,3,5,6], 5
//Output: 2
// 
//
// Example 2: 
//
// 
//Input: [1,3,5,6], 2
//Output: 1
// 
//
// Example 3: 
//
// 
//Input: [1,3,5,6], 7
//Output: 4
// 
//
// Example 4: 
//
// 
//Input: [1,3,5,6], 0
//Output: 0
// 
// Related Topics Array Binary Search 
// 👍 2467 👎 257


package LeetCode.solutions;

/**
 * 这道题比较简单
 */
public class Q35_SearchInsertPosition{
  public static void main(String[] args) {
       Solution solution = new Q35_SearchInsertPosition().new Solution();
//       int[] nums = {1,3,5,6};  int target = 5;
//       int[] nums = {1,3,5,6};  int target = 2;
//       int[] nums = {1,3,5,6};  int target = 7;
       int[] nums = {1,3,5,6};  int target = 0;
      System.out.println("result is : " + solution.searchInsert(nums,target));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int searchInsert(int[] nums, int target) {
        for(int i = 0; i < nums.length; i++){
            if( nums[i] == target) return i;
            if( nums[i] > target) return i;
        }
        return nums.length ;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}