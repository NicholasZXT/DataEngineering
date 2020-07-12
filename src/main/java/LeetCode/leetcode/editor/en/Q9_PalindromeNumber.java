//Determine whether an integer is a palindrome. An integer is a palindrome when 
//it reads the same backward as forward. 
//
// Example 1: 
//
// 
//Input: 121
//Output: true
// 
//
// Example 2: 
//
// 
//Input: -121
//Output: false
//Explanation: From left to right, it reads -121. From right to left, it becomes
// 121-. Therefore it is not a palindrome.
// 
//
// Example 3: 
//
// 
//Input: 10
//Output: false
//Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
// 
//
// Follow up: 
//
// Coud you solve it without converting the integer to a string? 
// Related Topics Math 
// 👍 2318 👎 1549


package LeetCode.leetcode.editor.en;
public class Q9_PalindromeNumber{
  public static void main(String[] args) {
       Solution solution = new Q9_PalindromeNumber().new Solution();
       int i1 = 121;
       int i2 = -121;
       int i3 = 10;
       int i4 = 1234321;
       solution.isPalindrome(i1);
       solution.isPalindrome(i2);
       solution.isPalindrome(i3);
       solution.isPalindrome(i4);
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
      /**
       * 不转成字符串的解法，提取x中后一半的数，并逆转过来
       * @param x
       * @return
       */
      public boolean isPalindrome(int x) {
          System.out.println( "原始的数为：" + x);
//        特殊情况：
//         1. x 为负数， 直接是false;
//         2. x 的最后一位为0，那么第一位必须为0，只有0符合这个要求
          if (x < 0 || ( x%10==0 && x!=0)) {
              System.out.println(false);
              return false;
          }
//        提取x后面一半的数
          int reverseNum = 0;
//        这里判断提取到中间位置的方式是逆转的数比原有的数要大
          while(reverseNum < x){
              reverseNum = reverseNum * 10 + x % 10;
              x = x / 10;
          }
//        这里还要考虑 x 是奇数还是偶数，
//        x 为偶数时，上面的while结束后得到的 x 和 reverseNum 位数相等
//        x 为奇数时，比如12321，while结束后，x = 12, reverseNum = 123，需要处理中间位置上的3
          if ( reverseNum == x || (reverseNum/10 == x)) {
              System.out.println(true);
              return true;
          }else {
              System.out.println(false);
              return false;
          }
      }

      /**
       * 下面这个解法是通过将x转成字符串得到的
       * @param x
       * @return
       */
//    public boolean isPalindrome(int x) {
//        System.out.println( "原始的数为：" + x);
//        char[] charArray = Integer.toString(x).toCharArray();
//        boolean flag = true;
//        for (int i = 0; i < charArray.length; i++){
//            int j = charArray.length - 1 - i;
//            if(charArray[i] != charArray[j]) flag = false;
//        }
//        System.out.println(flag);
//        return flag;
//    }
}
//leetcode submit region end(Prohibit modification and deletion)

}