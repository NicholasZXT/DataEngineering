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

public class Q67_AddBinary{
  public static void main(String[] args) {
       Solution solution = new Q67_AddBinary().new Solution();
//       String a = "11", b = "1";
//       String a = "1010", b = "1011";
      String a = "10100000100100110110010000010101111011011001101110111111111101000000101111001110001111100001101";
      String b = "110101001011101110001111100110001010100001101011101010000011011011001011101111001100000011011110011";
//      int a_int = Integer.valueOf(a, 2);
//      System.out.printf(String.valueOf(a_int));
      System.out.println("result is : " + solution.addBinary(a,b));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String addBinary(String a, String b) {
//        逆转字符串，没必要
//        String a_rev = new StringBuilder(a).reverse().toString();
//        String b_rev = new StringBuilder(b).reverse().toString();
        int len_max = Math.max(a.length(), b.length());
        int diff = a.length() - b.length();
        char[] result = new char[len_max + 1];
        int temp_a, temp_b;
        for(int i = len_max; i >= ; i--){
            temp_a = Integer.valueOf(a.charAt(i));
            temp_b = Integer.valueOf(b.charAt(i));
            if( temp_a + temp_b > 2){
                result[i] = '0';
            }
        }

        return "";
//        直接用下面的这个在字符串很长时会报错
//        long a_int = Long.parseLong(a,2);
//        long b_int = Long.parseLong(b,2);
//        long  result = a_int + b_int;
//        return String.valueOf(Long.toBinaryString(result));
    }
}
//leetcode submit region end(Prohibit modification and deletion)

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