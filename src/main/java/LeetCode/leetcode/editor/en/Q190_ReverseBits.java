//Reverse bits of a given 32 bits unsigned integer. 
//
// 
//
// Example 1: 
//
// 
//Input: 00000010100101000001111010011100
//Output: 00111001011110000010100101000000
//Explanation: The input binary string 00000010100101000001111010011100 represen
//ts the unsigned integer 43261596, so return 964176192 which its binary represent
//ation is 00111001011110000010100101000000.
// 
//
// Example 2: 
//
// 
//Input: 11111111111111111111111111111101
//Output: 10111111111111111111111111111111
//Explanation: The input binary string 11111111111111111111111111111101 represen
//ts the unsigned integer 4294967293, so return 3221225471 which its binary repres
//entation is 10111111111111111111111111111111. 
//
// 
//
// Note: 
//
// 
// Note that in some languages such as Java, there is no unsigned integer type. 
//In this case, both input and output will be given as signed integer type and sho
//uld not affect your implementation, as the internal binary representation of the
// integer is the same whether it is signed or unsigned. 
// In Java, the compiler represents the signed integers using 2's complement not
//ation. Therefore, in Example 2 above the input represents the signed integer -3 
//and the output represents the signed integer -1073741825. 
// 
//
// 
//
// Follow up: 
//
// If this function is called many times, how would you optimize it? 
// Related Topics Bit Manipulation 
// 👍 1015 👎 381


package LeetCode.leetcode.editor.en;
public class Q190_ReverseBits{
  public static void main(String[] args) {
       Solution solution = new Q190_ReverseBits().new Solution();
       int num1 = 43261596;
       int num2 = Integer.parseUnsignedInt("11111111111111111111111111111101",2);
       int num3 = Integer.parseUnsignedInt("00000010100101000001111010011100",2);
       solution.reverseBits(num1);
       solution.reverseBits(num2);
       solution.reverseBits(num3);
  }
  //leetcode submit region begin(Prohibit modification and deletion)
public class Solution {
  /**
   *这个解法是逐个比特(bit)逆转
   * @param n
   * @return
   */
    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        System.out.println("数字为: " + n);
        System.out.println("二进制为: " + Integer.toBinaryString(n));
        int result = 0;
        int power = 31;
        int temp;
//        下面这个地方要特别注意，Java中必须要加上后面那个power的判断，要不然会陷入死循环
        while (n != 0 & power >= 0){
            temp = n & 1;
            result += temp << power;
            n = n >> 1;
            power -= 1;
        }
        System.out.println("逆转后的数字为: " + result);
        System.out.println("二进制为: " + Integer.toBinaryString(result));
        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}