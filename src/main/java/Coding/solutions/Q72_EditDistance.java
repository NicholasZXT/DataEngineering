//Given two words word1 and word2, find the minimum number of operations require
//d to convert word1 to word2. 
//
// You have the following 3 operations permitted on a word: 
//
// 
// Insert a character 
// Delete a character 
// Replace a character 
// 
//
// Example 1: 
//
// 
//Input: word1 = "horse", word2 = "ros"
//Output: 3
//Explanation: 
//horse -> rorse (replace 'h' with 'r')
//rorse -> rose (remove 'r')
//rose -> ros (remove 'e')
// 
//
// Example 2: 
//
// 
//Input: word1 = "intention", word2 = "execution"
//Output: 5
//Explanation: 
//intention -> inention (remove 't')
//inention -> enention (replace 'i' with 'e')
//enention -> exention (replace 'n' with 'x')
//exention -> exection (replace 'n' with 'c')
//exection -> execution (insert 'u')
// 
// Related Topics String Dynamic Programming 
// 👍 4329 👎 59


package Coding.solutions;
public class Q72_EditDistance{
  public static void main(String[] args) {
       Solution solution = new Q72_EditDistance().new Solution();

       String w1_1 = "horse", w1_2 = "ros";
       String w2_1 = "intention", w2_2 = "execution";
       String w3_1 = "", w3_2 = "a";

       solution.minDistance(w1_1, w1_2);
       solution.minDistance(w2_1, w2_2);
       solution.minDistance(w3_1, w3_2);

  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int minDistance(String word1, String word2) {
        int len1 = word1.length(), len2 = word2.length();
//        用于记录的dp二维数组，dp[i][j] 表示 S1[0: i] 到 S2[0: j] 的最小编辑距离
        int[][] dp = new int[len1+1][len2+1];

        for (int i = 1; i <= len1; i++){
            dp[i][0] = i;
        }
        for (int j = 1; j <= len2; j++){
            dp[0][j] = j;
        }

        for(int i = 1; i <= len1; i++){
            for(int j = 1; j <= len2; j++){
                if(word1.charAt(i-1) == word2.charAt(j-1))
                    dp[i][j] = dp[i-1][j-1];
                else
                    dp[i][j] = Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1])) + 1;
            }
        }
        System.out.println(dp[len1][len2]);
        return dp[len1][len2];
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}