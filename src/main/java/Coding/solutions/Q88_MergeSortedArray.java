//Given two sorted integer arrays nums1 and nums2, merge nums2 into nums1 as one
// sorted array. 
//
// Note: 
//
// 
// The number of elements initialized in nums1 and nums2 are m and n respectivel
//y. 
// You may assume that nums1 has enough space (size that is equal to m + n) to h
//old additional elements from nums2. 
// 
//
// Example: 
//
// 
//Input:
//nums1 = [1,2,3,0,0,0], m = 3
//nums2 = [2,5,6],       n = 3
//
//Output: [1,2,2,3,5,6]
// 
//
// 
// Constraints: 
//
// 
// -10^9 <= nums1[i], nums2[i] <= 10^9 
// nums1.length == m + n 
// nums2.length == n 
// 
// Related Topics Array Two Pointers 
// 👍 2307 👎 4227


package Coding.solutions;

/**
 * 这道题值得一看
 */
public class Q88_MergeSortedArray{
  public static void main(String[] args) {
       Solution solution = new Q88_MergeSortedArray().new Solution();
//       int[] nums1 = {1, 2, 3, 0, 0, 0};
//       int[] nums2 = {2,5,6};
//      solution.merge(nums1, 3, nums2,3);
      int[] nums1 = {0};
      int[] nums2 = {1};
      solution.merge(nums1, 0, nums2,1);
      for(int i: nums1){
           System.out.print(i + ", ");
       }
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int tail1 = m - 1;
        int tail2 = n - 1;
        int finished = m + n - 1;
        while(tail1 >= 0 & tail2 >= 0){
            nums1[finished--] = (nums1[tail1] > nums2[tail2]) ? nums1[tail1--]: nums2[tail2--];
        }
//      剩下的只需要考虑nums有没有归并完，这里的条件必须是 >= ,不能是 >
        while (tail2 >= 0 ){
            nums1[finished--] = nums2[tail2--];
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}