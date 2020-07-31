//Given two binary strings, return their sum (also a binary string). 
//
// The input strings are both non-empty and contains only characters 1 or 0. 
//
// Example 1: 
//
// 
//Input: a = "11", b = "1"
//Output: "100" 
//
// Example 2: 
//
// 
//Input: a = "1010", b = "1011"
//Output: "10101" 
//
// 
// Constraints: 
//
// 
// Each string consists only of '0' or '1' characters. 
// 1 <= a.length, b.length <= 10^4 
// Each string is either "0" or doesn't contain any leading zero. 
// 
// Related Topics Math String 
// 👍 1929 👎 282


package LeetCode.leetcode.editor.en;

/**
 * 这道题有点难度，主要是要熟悉Java中的字符串和字符，刚好复习了Java里字符和字符串的一些内容
 */
public class Q67_AddBinary{
  public static void main(String[] args) {
       Solution solution = new Q67_AddBinary().new Solution();
//       String a = "11", b = "1";
//       String a = "1010", b = "1011";
//      下面这个测试用例是重点
      String a = "10100000100100110110010000010101111011011001101110111111111101000000101111001110001111100001101";
      String b = "110101001011101110001111100110001010100001101011101010000011011011001011101111001100000011011110011";
//      int a_int = Integer.valueOf(a, 2);
//      System.out.printf(String.valueOf(a_int));

//      Integer.valueOf 和 Integer.parseInt 有细微差别
//      System.out.println(Integer.valueOf('1'));  // 对于char，提取的是ASCII码数值，相当于类型转换
//      System.out.println(Integer.valueOf("1"));  // 对于String, 表现和parseInt一致
//      System.out.println(Integer.parseInt("1"));
//      System.out.println((int)('1'));
//      System.out.println("-----------------");
////      char的加法和String的加法不一样，char的加法相当于先做类型转换之后，再进行加法，执行的是字符ASCII码的加法
//      System.out.println('0' + 0);  // 字符 0 的ASCII码是 48
//      System.out.println('1' + 0);  // 字符 1 的ASCII码是 49
//      System.out.println('0' + '0');
//      System.out.println('0' + '1');
//      System.out.println('1' + '1');
//      System.out.println("------------------");
////      StringBuilderd 的使用
//      StringBuilder sb = new StringBuilder();
//      sb.append((char)48);
//      sb.append(48);
//      System.out.println(sb);
//      System.out.println("------------------");
      System.out.println("result is : " + solution.addBinary(a,b));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String addBinary(String a, String b) {
//        StringBuilder是可变的字符串对象，不像字符串数组那样长度固定
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1;
        int carry = 0, sum = 0;
        while (i >= 0 | j >= 0 ){
//            每轮开始,sum都要初始化为0，然后加上上一轮进位的结果carry，这里合并了两个步骤
            sum = carry;
            if( i >= 0) sum  += a.charAt(i--) - '0';
            if( j >= 0) sum  += b.charAt(j--) - '0';
            switch (sum){
                case 2:
                    carry = 1; sb.append('0'); break;
                case 3:
                    carry = 1; sb.append('1'); break;
                case 1:
                    carry = 0; sb.append('1');break;
                default:
                    carry = 0; sb.append('0');
            }
        }

//      这里检查最后是否还需要进位
        if(carry == 1) sb.append('1');

        String result = sb.reverse().toString();
        return result;
//        直接用下面的这个在字符串很长时会报错
//        long a_int = Long.parseLong(a,2);
//        long b_int = Long.parseLong(b,2);
//        long  result = a_int + b_int;
//        return String.valueOf(Long.toBinaryString(result));
    }
}
//leetcode submit region end(Prohibit modification and deletion)


    /**
     * 下面这个解答比较简洁
     */
    public class Solution_2 {
        public String addBinary(String a, String b) {
            StringBuilder sb = new StringBuilder();
            int i = a.length() - 1, j = b.length() -1, carry = 0;
            while (i >= 0 || j >= 0) {
                int sum = carry;
                if (j >= 0) sum += b.charAt(j--) - '0';
                if (i >= 0) sum += a.charAt(i--) - '0';
                sb.append(sum % 2);
                carry = sum / 2;
            }
            if (carry != 0) sb.append(carry);
            return sb.reverse().toString();
        }
    }

}