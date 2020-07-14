//Roman numerals are represented by seven different symbols: I, V, X, L, C, D an
//d M. 
//
// 
//Symbol       Value
//I             1
//V             5
//X             10
//L             50
//C             100
//D             500
//M             1000 
//
// For example, two is written as II in Roman numeral, just two one's added toge
//ther. Twelve is written as, XII, which is simply X + II. The number twenty seven
// is written as XXVII, which is XX + V + II. 
//
// Roman numerals are usually written largest to smallest from left to right. Ho
//wever, the numeral for four is not IIII. Instead, the number four is written as 
//IV. Because the one is before the five we subtract it making four. The same prin
//ciple applies to the number nine, which is written as IX. There are six instance
//s where subtraction is used: 
//
// 
// I can be placed before V (5) and X (10) to make 4 and 9. 
// X can be placed before L (50) and C (100) to make 40 and 90. 
// C can be placed before D (500) and M (1000) to make 400 and 900. 
// 
//
// Given a roman numeral, convert it to an integer. Input is guaranteed to be wi
//thin the range from 1 to 3999. 
//
// Example 1: 
//
// 
//Input: "III"
//Output: 3 
//
// Example 2: 
//
// 
//Input: "IV"
//Output: 4 
//
// Example 3: 
//
// 
//Input: "IX"
//Output: 9 
//
// Example 4: 
//
// 
//Input: "LVIII"
//Output: 58
//Explanation: L = 50, V= 5, III = 3.
// 
//
// Example 5: 
//
// 
//Input: "MCMXCIV"
//Output: 1994
//Explanation: M = 1000, CM = 900, XC = 90 and IV = 4. 
// Related Topics Math String 
// 👍 2250 👎 3517


package LeetCode.leetcode.editor.en;

import java.util.HashMap;

public class Q13_RomanToInteger{
  public static void main(String[] args) {
       Solution solution = new Q13_RomanToInteger().new Solution();
      System.out.println("输入为：III: 3" + ", 输出为" + solution.romanToInt("III"));
      System.out.println("输入为：IV: 4" + ", 输出为" + solution.romanToInt("IV"));
      System.out.println("输入为：IX: 9" + ", 输出为" + solution.romanToInt("IX"));
      System.out.println("输入为：LVIII: 58" + ", 输出为" + solution.romanToInt("LVIII"));
      System.out.println("输入为：MCMXCIV: 1994" + ", 输出为" + solution.romanToInt("MCMXCIV"));
  }

//  这道题的重点是理解罗马数字的一个规则，从右往左遍历数字，如果遇到比前一个数小的，就减去这个，如果是大于等于，就加上

  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int romanToInt(String s) {
        int result = 0;
        if (s == null | s.isEmpty()) return 0;
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('I', 1);
        map.put('V', 5);
        map.put('X', 10);
        map.put('L', 50);
        map.put('C', 100);
        map.put('D', 500);
        map.put('M', 1000);
        char[] charArray = s.toCharArray();
        for(int i = s.length()-1 ; i >= 0; i--){
//            System.out.print(c);
            if( i == s.length()-1){
                result =  map.get(charArray[i]);
                continue;
            }
            if(map.get(charArray[i]) >= map.get(charArray[i+1])){
                result += map.get(charArray[i]);
            }else{
                result -= map.get(charArray[i]);
            }
        }
        return  result;
        
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}