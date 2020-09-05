//Given a string s, find the longest palindromic substring in s. You may assume 
//that the maximum length of s is 1000. 
//
// Example 1: 
//
// 
//Input: "babad"
//Output: "bab"
//Note: "aba" is also a valid answer.
// 
//
// Example 2: 
//
// 
//Input: "cbbd"
//Output: "bb"
// 
// Related Topics String Dynamic Programming 
// 👍 7777 👎 565


package LeetCode.leetcode.editor.en;
public class Q5_LongestPalindromicSubstring{
  public static void main(String[] args) {
       Solution solution = new Q5_LongestPalindromicSubstring().new Solution();

       String s1 = "babad";
       String s2 = "cbbd";
       String s3 = "a";
       String s4 = "";
       String s5 = "bb";

       //substring方法的index是左闭右开的，左右相等时返回的是空字符串
      System.out.println(s1.substring(0,0));
      //空字符不能调用length方法,会抛出空指针异常
//      String s = null;
//      System.out.println(s.length());

       solution.longestPalindrome(s1);
       solution.longestPalindrome(s2);
       solution.longestPalindrome(s3);
       solution.longestPalindrome(s4);
       solution.longestPalindrome(s5);

  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
      /**
       * 使用动态规划的方法
       * @param s
       * @return
       */
    public String longestPalindrome(String s) {
        String result = "";
        int len = s.length();
        if( len == 0) return result;
//        用于记录动态规划过程的二维矩阵
        boolean[][] dp = new boolean[len][len];
        for(int i = len -1; i >= 0; i--){
            for( int j = i; j < len; j++){
//                这里用了 || 短路计算 来避免了数组的越界异常抛出
                dp[i][j] = ( s.charAt(i) == s.charAt(j) ) && ( j - i < 3 || dp[i+1][j-1] );
                if( dp[i][j] && (j+1 - i > result.length()) ){
                    result = s.substring(i, j+1);
                }
            }
        }
        System.out.println(s + ", substring is : " + result);
        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

    /**
     * 暴力解法，枚举所有子字符串，判断该子字符串是否是回文
     */
    class Solution_2 {
        public String longestPalindrome(String s) {
            String result = "";
            int len = s.length();
            if( len == 0) return result;
            int low = 0, high = 1;
//        int i = 0, j = 1;
            for(int i = 0; i < len; i++){
                for ( int j = i+1; j <= len; j++){
                    if( isPalindrome(s.substring(i, j)) ){
                        if( j - i > high - low){
                            low = i; high = j;
                        }
                    }
                }
            }
            result = s.substring(low, high);
            System.out.println(s + ", substring is : " + result);
            return result;
        }

        public boolean isPalindrome(String s){
            int len = s.length();
            if (len == 0) return false;
            if (len == 1) return true;
            boolean flag = true;
            for (int i = 0; i < len/2; i++){
                if(s.charAt(i) != s.charAt(len - i - 1)) flag = false;
            }
            return flag;
        }
    }
}