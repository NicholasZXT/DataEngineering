//Given an array nums and a value val, remove all instances of that value in-pla
//ce and return the new length. 
//
// Do not allocate extra space for another array, you must do this by modifying 
//the input array in-place with O(1) extra memory. 
//
// The order of elements can be changed. It doesn't matter what you leave beyond
// the new length. 
//
// Example 1: 
//
// 
//Given nums = [3,2,2,3], val = 3,
//
//Your function should return length = 2, with the first two elements of nums be
//ing 2.
//
//It doesn't matter what you leave beyond the returned length.
// 
//
// Example 2: 
//
// 
//Given nums = [0,1,2,2,3,0,4,2], val = 2,
//
//Your function should return length = 5, with the first five elements of nums c
//ontaining 0, 1, 3, 0, and 4.
//
//Note that the order of those five elements can be arbitrary.
//
//It doesn't matter what values are set beyond the returned length. 
//
// Clarification: 
//
// Confused why the returned value is an integer but your answer is an array? 
//
// Note that the input array is passed in by reference, which means modification
// to the input array will be known to the caller as well. 
//
// Internally you can think of this: 
//
// 
//// nums is passed in by reference. (i.e., without making a copy)
//int len = removeElement(nums, val);
//
//// any modification to nums in your function would be known by the caller.
//// using the length returned by your function, it prints the first len element
//s.
//for (int i = 0; i < len; i++) {
//    print(nums[i]);
//} Related Topics Array Two Pointers 
// 👍 1486 👎 2757


package LeetCode.leetcode.editor.en;

/**
 * 这道题有点难度，需要思考一段时间
 * 和Q26有一点关系
 */
public class Q27_RemoveElement{
  public static void main(String[] args) {
       Solution solution = new Q27_RemoveElement().new Solution();
//       int[] nums = {3,2,2,3};  int val = 3;
       int[] nums = {0,1,2,2,3,  0,4,2}; int val = 2;
      System.out.println("result is : " + solution.removeElement(nums,val));
      for (int i = 0; i < nums.length ; i++) System.out.print(nums[i] + ", ");
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int removeElement(int[] nums, int val) {
        int index = 0;
        for(int i = 0; i < nums.length ; i++){
            if( nums[i] != val){
                nums[index] = nums[i];
                index++;
            }
        }
        return index;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

    /**
     * 下面这个是我一开始最暴力的解法
     */
    class Solution_1 {
        public int removeElement(int[] nums, int val) {
//      第一个for循环找出数组中 val 的个数
            int val_num = 0;
            for(int i = 0; i < nums.length ; i++){
                val_num += nums[i] == val ? 1:0;
            }
//      第二个for循环将前 nums.length-val_num 里的 val 替换成后面那些不是 val 的数
            int j = nums.length - val_num;
            for(int i = 0; i < nums.length - val_num; i++){
                if( nums[i] == val) {
                    while (j < nums.length){
                        if(nums[j] != val)
                            break;
                        else
                            j++;
                    }
                    nums[i] = nums[j++];
                }
            }
            return nums.length - val_num;
        }
    }
}