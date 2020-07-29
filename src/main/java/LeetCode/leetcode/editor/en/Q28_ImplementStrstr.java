//Implement strStr(). 
//
// Return the index of the first occurrence of needle in haystack, or -1 if need
//le is not part of haystack. 
//
// Example 1: 
//
// 
//Input: haystack = "hello", needle = "ll"
//Output: 2
// 
//
// Example 2: 
//
// 
//Input: haystack = "aaaaa", needle = "bba"
//Output: -1
// 
//
// Clarification: 
//
// What should we return when needle is an empty string? This is a great questio
//n to ask during an interview. 
//
// For the purpose of this problem, we will return 0 when needle is an empty str
//ing. This is consistent to C's strstr() and Java's indexOf(). 
//
// 
// Constraints: 
//
// 
// haystack and needle consist only of lowercase English characters. 
// 
// Related Topics Two Pointers String 
// 👍 1631 👎 1939


package LeetCode.leetcode.editor.en;

/**
 * 这道题比较简单
 */
public class Q28_ImplementStrstr{
  public static void main(String[] args) {
       Solution solution = new Q28_ImplementStrstr().new Solution();
//       String haystack = "hello"; String needle = "ll";
//       String haystack = "aaaaa"; String needle = "bba";
//       String haystack = "a"; String needle = "a";
       String haystack = "mississippi"; String needle = "mississippi";
      System.out.println("result is : " + solution.strStr(haystack,needle));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int strStr(String haystack, String needle) {
        if( needle.isEmpty() ) return 0;
        if( haystack.isEmpty() ) return -1;
        for(int i = 0; i <= haystack.length() - needle.length(); i++){
            boolean flag = true;
            for ( int j = 0; j < needle.length(); j++){
                if( haystack.charAt(i + j) != needle.charAt(j)) flag = false;
            }
            if( flag ) return i;
        }
        return -1;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}