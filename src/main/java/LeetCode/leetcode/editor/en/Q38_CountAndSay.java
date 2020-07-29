//The count-and-say sequence is the sequence of integers with the first five ter
//ms as following: 
//
// 
//1.     1
//2.     11
//3.     21
//4.     1211
//5.     111221
// 
//
// 1 is read off as "one 1" or 11. 
//11 is read off as "two 1s" or 21. 
//21 is read off as "one 2, then one 1" or 1211. 
//
// Given an integer n where 1 ≤ n ≤ 30, generate the nth term of the count-and-s
//ay sequence. You can do so recursively, in other words from the previous member 
//read off the digits, counting the number of digits in groups of the same digit. 
//
//
// Note: Each term of the sequence of integers will be represented as a string. 
//
//
// 
//
// Example 1: 
//
// 
//Input: 1
//Output: "1"
//Explanation: This is the base case.
// 
//
// Example 2: 
//
// 
//Input: 4
//Output: "1211"
//Explanation: For n = 3 the term was "21" in which we have two groups "2" and "
//1", "2" can be read as "12" which means frequency = 1 and value = 2, the same wa
//y "1" is read as "11", so the answer is the concatenation of "12" and "11" which
// is "1211".
// 
// Related Topics String 
// 👍 1344 👎 9837


package LeetCode.leetcode.editor.en;

/**
 * 这道题的表述一开始有点难理解，解答需要一点时间，主要是起始的时候边界条件的处理
 */
public class Q38_CountAndSay{
  public static void main(String[] args) {
       Solution solution = new Q38_CountAndSay().new Solution();
//       int n = 3;
//       int n = 4;
       int n = 5;
      System.out.println("result is ：" + solution.countAndSay(n));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String countAndSay(int n) {
        if( n == 1 ) return "1";
        String pre = countAndSay(n - 1);
        int count = 1;
        char tempChar = pre.charAt(0);
        String say = "";
        for ( int i = 1; i < pre.length(); i++){
            if( pre.charAt(i) == tempChar )
                count += 1;
            else{
                say = say + count + tempChar;
                tempChar = pre.charAt(i);
                count = 1;
            }
        }
//        这里主要是处理 n=2 时的边界条件
        if( say.isEmpty())
            say = count + String.valueOf(tempChar);
        else
            say = say + count + tempChar;
        return say;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}