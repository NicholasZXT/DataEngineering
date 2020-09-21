//Given two strings text1 and text2, return the length of their longest common s
//ubsequence. 
//
// A subsequence of a string is a new string generated from the original string 
//with some characters(can be none) deleted without changing the relative order of
// the remaining characters. (eg, "ace" is a subsequence of "abcde" while "aec" is
// not). A common subsequence of two strings is a subsequence that is common to bo
//th strings. 
//
// 
//
// If there is no common subsequence, return 0. 
//
// 
// Example 1: 
//
// 
//Input: text1 = "abcde", text2 = "ace" 
//Output: 3  
//Explanation: The longest common subsequence is "ace" and its length is 3.
// 
//
// Example 2: 
//
// 
//Input: text1 = "abc", text2 = "abc"
//Output: 3
//Explanation: The longest common subsequence is "abc" and its length is 3.
// 
//
// Example 3: 
//
// 
//Input: text1 = "abc", text2 = "def"
//Output: 0
//Explanation: There is no such common subsequence, so the result is 0.
// 
//
// 
// Constraints: 
//
// 
// 1 <= text1.length <= 1000 
// 1 <= text2.length <= 1000 
// The input strings consist of lowercase English characters only. 
// 
// Related Topics Dynamic Programming 
// 👍 1793 👎 21


package LeetCode.leetcode.editor.en;

import java.util.Arrays;

/**
 * 这个是最长公共子串问题，LeetCode上没有这个原题，是我自己修改的
 * 这里的dp[i][j]表示的是 S1中以S1[i]结尾的子串 和 S2中以S2[j]结尾的子串 两者之间的最长公共子串长度，
 * 不是 S1[0: i] 和 S2[0: j] 两者之间的最大公共子串长度
 */
public class Q1143_LongestCommonSubString {
  public static void main(String[] args) {
       Solution solution = new Q1143_LongestCommonSubString().new Solution();

       String s1_1 = "abcf", s1_2 = "cabdec";  //最长公共子串为 ab， 长度为2
       String s2_1 = "abc", s2_2 = "abc";
       String s3_1 = "abc", s3_2 = "def";
       String s4_1 = "fish", s4_2 = "hishh";

       solution.longestCommonSubstring(s1_1, s1_2);
       solution.longestCommonSubstring(s2_1, s2_2);
       solution.longestCommonSubstring(s3_1, s3_2);
       solution.longestCommonSubstring(s4_1, s4_2);

  }

class Solution {
    public int longestCommonSubstring(String text1, String text2) {
        int len1 = text1.length(), len2 = text2.length();
//        left和right用于记录dp中最大值的位置
        int left = 0, right = 0;
//        这里创建的dp数组两个维度各长一点，是利用了Java数组默认初始值为0，避免了手动处理边界条件
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 1; i <= len1; i++){
            for(int j = 1; j <= len2; j++){
                if( text1.charAt(i-1) == text2.charAt(j-1) ){
                    dp[i][j] = dp[i-1][j-1] + 1;
                    if( dp[i][j] > dp[left][right] ){
                        left = i; right = j;
                    }
                }
                else
                    dp[i][j] = 0;
            }
        }
        int max_len = dp[left][right];
        System.out.print("max common substring: length -- " + max_len);
//        下面打印出最长公共子串
        char[] max_substring = new char[max_len];
        for(int i = max_len; i>0; i-- ){
            max_substring[max_len - i] = text1.charAt( left-1 - (i-1) );
        }
        System.out.println(", substring -- :" + Arrays.toString(max_substring));

        return max_len;
    }
}

}