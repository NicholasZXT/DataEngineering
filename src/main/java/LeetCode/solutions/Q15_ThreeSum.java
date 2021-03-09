//Given an array nums of n integers, are there elements a, b, c in nums such tha
//t a + b + c = 0? Find all unique triplets in the array which gives the sum of ze
//ro. 
//
// Notice that the solution set must not contain duplicate triplets. 
//
// 
// Example 1: 
// Input: nums = [-1,0,1,2,-1,-4]
//Output: [[-1,-1,2],[-1,0,1]]
// Example 2: 
// Input: nums = []
//Output: []
// Example 3: 
// Input: nums = [0]
//Output: []
// 
// 
// Constraints: 
//
// 
// 0 <= nums.length <= 3000 
// -105 <= nums[i] <= 105 
// 
// Related Topics Array Two Pointers 
// 👍 9696 👎 995


package LeetCode.solutions;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Q15_ThreeSum{
  public static void main(String[] args) {
       Solution solution = new Q15_ThreeSum().new Solution();
       int[] nums = new int[]{-1,0,1,2,-1,-4};
       List<List<Integer>> result = solution.threeSum(nums);
      System.out.println("the result is : " + Arrays.toString(result.toArray()));
//      for(List<Integer> res: result){
//          System.out.println(Arrays.toString(res.toArray()));
//      }

  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        int complement = 0;

        for(int i = 0; i < nums.length; i++){
            complement = 0 - nums[i];
            map.clear();
            for(int j = i+1; j < nums.length; j++){
                if(map.containsKey(complement - nums[j])){
//                    int match = (int)map.get(complement-nums[j]);
                    result.add(new ArrayList<Integer>((Arrays.asList(nums[i], nums[j], complement-nums[j]))));
                }else {
                    map.put(nums[j], j);
                }
            }
        }
        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}