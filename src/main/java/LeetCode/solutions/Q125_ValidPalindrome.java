//Given a string, determine if it is a palindrome, considering only alphanumeric
// characters and ignoring cases. 
//
// Note: For the purpose of this problem, we define empty string as valid palind
//rome. 
//
// Example 1: 
//
// 
//Input: "A man, a plan, a canal: Panama"
//Output: true
// 
//
// Example 2: 
//
// 
//Input: "race a car"
//Output: false
// 
//
// 
// Constraints: 
//
// 
// s consists only of printable ASCII characters. 
// 
// Related Topics Two Pointers String 
// 👍 1231 👎 2950


package LeetCode.solutions;

/**
 * 这个比较简单
 */
public class Q125_ValidPalindrome{
  public static void main(String[] args) {
       Solution solution = new Q125_ValidPalindrome().new Solution();
//       String s = "A man, a plan, a canal: Panama";
//       String s = "race a car";
//       String s = "a.";
//       String s = "ab@a";
       String s = "0P";
      System.out.println("result is : " + solution.isPalindrome(s));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPalindrome(String s) {
        if(s.isEmpty()) return true;
//        String s_reg = s.replaceAll("[, .:!?-]*", "").toLowerCase();
        String s_reg = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        System.out.println(s_reg);
//        System.out.println( 5/2);
//        System.out.println( 6/2 );
        int len = s_reg.length();
        System.out.println("len = " + len);
        for(int i = 0; i < len/2; i++){
            System.out.println("i = " + i + ", " + s_reg.charAt(i) + " vs " + s_reg.charAt(len-1-i));
            if(s_reg.charAt(i) != s_reg.charAt(len-1-i)) return false;
        }
        return true;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}