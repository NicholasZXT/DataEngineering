//Write a function to find the longest common prefix string amongst an array of 
//strings. 
//
// If there is no common prefix, return an empty string "". 
//
// Example 1: 
//
// 
//Input: ["flower","flow","flight"]
//Output: "fl"
// 
//
// Example 2: 
//
// 
//Input: ["dog","racecar","car"]
//Output: ""
//Explanation: There is no common prefix among the input strings.
// 
//
// Note: 
//
// All given inputs are in lowercase letters a-z. 
// Related Topics String 
// 👍 2596 👎 1863


package LeetCode.leetcode.editor.en;
public class Q14_LongestCommonPrefix{
  public static void main(String[] args) {
       Solution solution = new Q14_LongestCommonPrefix().new Solution();
       solution.longestCommonPrefix(new String[]{"flower","flow","flight"});
       solution.longestCommonPrefix(new String[]{"dog","racecar","car"});
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String longestCommonPrefix(String[] strs) {
        String result = "";

        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}