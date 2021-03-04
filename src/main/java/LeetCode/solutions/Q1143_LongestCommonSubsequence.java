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


package LeetCode.solutions;

/**
 * 这个是最长公共子序列问题，
 * 这里的dp[i][j]表示的是 S1[0: i] 和 S2[0: j] 中最长公共子序列的长度
 */
public class Q1143_LongestCommonSubsequence{
  public static void main(String[] args) {
       Solution solution = new Q1143_LongestCommonSubsequence().new Solution();

       String s1_1 = "abcde", s1_2 = "abc";
       String s2_1 = "abc", s2_2 = "abc";
       String s3_1 = "abc", s3_2 = "def";

       //Java数组建立之后，数组内的值默认初始化为0
//       int[][] array = new int[3][3];
//      System.out.println(array[0][0]);

       solution.longestCommonSubsequence(s1_1, s1_2);
       solution.longestCommonSubsequence(s2_1, s2_2);
       solution.longestCommonSubsequence(s3_1, s3_2);

  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int len1 = text1.length(), len2 = text2.length();
//        这里创建的dp数组两个维度各多一个，是利用了Java数组默认初始值为0，避免了手动处理边界条件
//        也因为数组的长度多了1，所以i和j从1开始
        int[][] dp = new int[len1 + 1][len2 + 1];
//        用于记录最长公共子序列
        StringBuilder max_subseq = new StringBuilder();
        for (int i = 1; i <= len1; i++){
            for(int j = 1; j <= len2; j++){
//                下面不用处理手动处理dp数组内i = 1或者j=1时数组越界的异常了
//                但是i和j在text1里还需要-1
                if( text1.charAt(i-1) == text2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                    max_subseq.append(text1.charAt(i - 1));
                }
                else
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
        System.out.print("max subsequence: length -- " + dp[len1][len2]);
        System.out.println(", subsequence: " + max_subseq.toString());
        return dp[len1][len2];
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}