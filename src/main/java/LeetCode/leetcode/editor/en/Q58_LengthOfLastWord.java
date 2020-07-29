//Given a string s consists of upper/lower-case alphabets and empty space charac
//ters ' ', return the length of last word (last word means the last appearing wor
//d if we loop from left to right) in the string. 
//
// If the last word does not exist, return 0. 
//
// Note: A word is defined as a maximal substring consisting of non-space charac
//ters only. 
//
// Example: 
//
// 
//Input: "Hello World"
//Output: 5
// 
//
// 
// Related Topics String 
// 👍 673 👎 2471


package LeetCode.leetcode.editor.en;


/**
 * 这题过于简单，不太懂为啥要出这种题目，看点赞数也能看出问题
 */
public class Q58_LengthOfLastWord{
  public static void main(String[] args) {
       Solution solution = new Q58_LengthOfLastWord().new Solution();
//       String s = "Hello World";
       String s = " ";
      System.out.println("result is : " + solution.lengthOfLastWord(s));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int lengthOfLastWord(String s) {
        String[] array = s.split(" ");
        int result = array.length > 0 ? array[array.length-1].length(): 0;
        return result;
//        return array.length;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}