//Write a function to find the longest common prefix string amongst an array of 
//strings. 
//
// If there is no common prefix, return an empty string "". 
//
// Example 1: 
//
// 
//Input: ["flower","flow","flight"]
//Output: "fl"
// 
//
// Example 2: 
//
// 
//Input: ["dog","racecar","car"]
//Output: ""
//Explanation: There is no common prefix among the input strings.
// 
//
// Note: 
//
// All given inputs are in lowercase letters a-z. 
// Related Topics String 
// 👍 2596 👎 1863


package Coding.solutions;

public class Q14_LongestCommonPrefix{
  public static void main(String[] args) {
       Solution solution = new Q14_LongestCommonPrefix().new Solution();
//      System.out.println("flow".indexOf("flo"));
//      System.out.println("flow".indexOf("floww"));
      System.out.println(solution.longestCommonPrefix(new String[]{"flower","flow","flight"}));
      System.out.println(solution.longestCommonPrefix(new String[]{"flower","flow","flowight"}));
      System.out.println(solution.longestCommonPrefix(new String[]{"dog","racecar","car"}));
      System.out.println(solution.longestCommonPrefix(new String[]{"dog","dogdog","dogg"}));
//      边界条件
//      System.out.println(solution.longestCommonPrefix(new String[]{"dog"}));
//      System.out.println(solution.longestCommonPrefix(new String[]{}));
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
//      下面这两个解法的性能瓶颈在于：如果最后一个字符串非常短，那么前面很多次比较都是不必要的
//      官方的解法一：大体思路和我一开始的解法是一样的，但是在循环数组的内层里和我不太一样
      public String longestCommonPrefix(String[] strs) {
          if (strs.length == 0) return "";
          String prefix = strs[0];
          for (int i = 1; i < strs.length; i++){
//              System.out.print(i +"-" + strs[i] + ", prefix-" + prefix + ": ");
//              这里如果strs[i]的开头包含prefix，就会返回-1或者其他数，此时进入while循环，对当前的prefix截断末尾的一个字符
//              while循环知道在strs[i]里找到prefix，或者prefix为空
              while (strs[i].indexOf(prefix) != 0) {
//                  prefix = prefix.substring(0, prefix.length() - 1);
                  System.out.print(prefix + ",");
                  if (prefix.isEmpty()) return "";
              }
//              System.out.println();
          }

          return prefix;
      }

//      下面这个是最朴素的解法，循环变量
//    public String longestCommonPrefix(String[] strs) {
//        if( strs.length == 0) return "";
//        String prefix = strs[0];
//        if (strs.length == 1) return prefix;
//        int maxlen = 0;
//        String temp = "";
//        for (int i = 1; i < strs.length ; i++){
//            temp = "";
//            //内层的循环里，是对prefix和strs[i]进行逐个比较，得到最大的prefix
//            maxlen = Math.min(prefix.length(), strs[i].length());
//            for (int j = 0; j < maxlen; j++){
//                if(prefix.charAt(j) == strs[i].charAt(j))  {
//                    temp += prefix.charAt(j);
//                } else{
//                    break;
//                }
//            }
//            prefix = temp;
//        }
//        return prefix;
//    }
}
//leetcode submit region end(Prohibit modification and deletion)

}