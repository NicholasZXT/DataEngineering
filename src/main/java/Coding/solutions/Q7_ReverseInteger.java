//Given a 32-bit signed integer, reverse digits of an integer. 
//
// Example 1: 
//
// 
//Input: 123
//Output: 321
// 
//
// Example 2: 
//
// 
//Input: -123
//Output: -321
// 
//
// Example 3: 
//
// 
//Input: 120
//Output: 21
// 
//
// Note: 
//Assume we are dealing with an environment which could only store integers with
//in the 32-bit signed integer range: [−231, 231 − 1]. For the purpose of this pro
//blem, assume that your function returns 0 when the reversed integer overflows. 
// Related Topics Math 
// 👍 3393 👎 5350


package Coding.solutions;
public class Q7_ReverseInteger{
  public static void main(String[] args) {
       Solution solution = new Q7_ReverseInteger().new Solution();
       System.out.println(Integer.SIZE);  //Java中一个int的位数
       System.out.println(Integer.MAX_VALUE);  //最大为2147483647
       System.out.println(Integer.MIN_VALUE);  //最小为-2147483648
//      由上面Java里存储的最大和最小整数可以看出，对于2147483647这个数，逆转之后，就会溢出，所以要考虑溢出这个问题
       solution.reverse(123);
       solution.reverse(-123);
       solution.reverse(120);
       solution.reverse(2147483647);
       solution.reverse(-2147483648);
  }
  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int reverse(int x) {
        System.out.println("整数为: " + x);
        int rev = 0;
        int pop = 0;
        while (x != 0){
//            通过模运算获取个位数，注意，Java（和C++）中的负数取模可以实现这个操作，但是Python中的负数取模和Java不一样
            pop = x % 10;
//            通过整除去掉已经获取到的最后一位
            x = x/10;
//            下面开始判断 rev * 10 + pop 是否会溢出
//            做这个溢出判断的时候，数学上应该是判断  rev * 10 + pop > Integer.MAX_VALUE，但是要注意的是，数值溢出可能在
//            rev*10 这个运算中就已经发生，所以不能使用 rev * 10 + pop > Integer.MAX_VALUE 进行判断，要对 rev*10是否溢出做判断
//            判断正数溢出
            if( (rev > Integer.MAX_VALUE/10) || (rev == Integer.MAX_VALUE/10 && pop > 7) ){rev = 0; break;}
//            判断负数溢出
            if( (rev < Integer.MIN_VALUE/10) || (rev == Integer.MIN_VALUE/10 && pop < -8) ) {rev = 0; break;}
//            下面这句用于组合结果，但是执行这个运算之前，需要在上面先判断是否会溢出
            rev = rev * 10 + pop;
        }
        System.out.println("逆转后是: " + rev);
        return rev;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}