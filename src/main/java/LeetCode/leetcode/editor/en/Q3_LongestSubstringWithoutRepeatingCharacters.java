//Given a string, find the length of the longest substring without repeating cha
//racters. 
//
// 
// Example 1: 
//
// 
//Input: "abcabcbb"
//Output: 3 
//Explanation: The answer is "abc", with the length of 3. 
// 
//
// 
// Example 2: 
//
// 
//Input: "bbbbb"
//Output: 1
//Explanation: The answer is "b", with the length of 1.
// 
//
// 
// Example 3: 
//
// 
//Input: "pwwkew"
//Output: 3
//Explanation: The answer is "wke", with the length of 3. 
//             Note that the answer must be a substring, "pwke" is a subsequence
// and not a substring.
// 
// 
// 
// 
// Related Topics Hash Table Two Pointers String Sliding Window 
// 👍 10432 👎 605


package LeetCode.leetcode.editor.en;

import java.util.HashSet;

public class Q3_LongestSubstringWithoutRepeatingCharacters{
  public static void main(String[] args) {
       Solution solution = new Q3_LongestSubstringWithoutRepeatingCharacters().new Solution();
       String s1 = "abcabcbb";
       String s2 = "bbbbb";
       String s3 = "pwwkew";
       String s4 = "";
       String s5 = " ";
       String s6 = "au";

      solution.lengthOfLongestSubstring(s1);
      solution.lengthOfLongestSubstring(s2);
      solution.lengthOfLongestSubstring(s3);
      solution.lengthOfLongestSubstring(s4);
      solution.lengthOfLongestSubstring(s5);
      solution.lengthOfLongestSubstring(s6);
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
      /**
       * 解法2，使用HashSet作为滑动窗口
       * @param s
       * @return
       */
    public int lengthOfLongestSubstring(String s) {
        HashSet<Character> slide = new HashSet<>();
//        low,high用于记录得到的最大子串的边界，max_len记录长度
        int low = 0, high = 0, max_len = 0;
//        i,j用于迭代记录
        int i = 0, j = 0;
        int len = s.length();
//        while循环中，i是子串（滑动窗口）开始的位置，j是结束的位置。
//        每一轮中，j增加之后，如果s[j] 不在 slide中，j继续向后滑动，不变；
//        如果s[j] 在 slide 中（不管它在哪个位置），j不动，i向后滑动一位，
//        此时表示从s[i]开始的最大不重复子串已经找到了，需要寻找从s[i+1]开始的最大不重复子串
        while ( i < len && j < len){
            if( !slide.contains(s.charAt(j))){
                slide.add(s.charAt(j++));
                if( j - i >= max_len){
                    max_len = j - i;
                    low = i; high = j;
                }
            }
            else
                slide.remove(s.charAt(i++));
        }
        System.out.println("max substring of " + s + "is : " + s.substring(low, high) + ", length is :" + max_len);
        return max_len;
    }


}
//leetcode submit region end(Prohibit modification and deletion)

    class Solution_1 {
        /**
         * 解法1，暴力解法
         * 主函数内遍历所有可能的子字符串，用另一个函数来判断子字符串里是否有重复字符
         * @param s
         * @return
         */
        public int lengthOfLongestSubstring(String s) {
            int len = s.length();
            int low = 0, high = 0, max_len = 1;
//            下面的这个for循环用于遍历从s[i]开始的最大不重复子串
            for(int i = 0; i < len; i++){
                for(int j = i+1; j <= len; j++){
//                这里由于s.substring(i,j)是不包括末尾的，所以j要取到最后一位
                    if( !isDuplicated(s.substring(i,j)) && j - i > max_len){
                        max_len = j - i;
                        low = i; high = j;
                    }
                }
            }

            if( len == 0 ) max_len = 0;

            System.out.println("max substring of " + s + "is : " + s.substring(low, high) + ", length is :" + max_len);
            return max_len;
        }

        public boolean isDuplicated(String s){
            boolean flag = false;
            HashSet<Character> set = new HashSet<>();
            for( char c: s.toCharArray()){
                if(!set.contains(c)){
                    set.add(c);
                }
                else{
                    flag = true;
                    break;
                }
            }
            return flag;
        }
    }


}