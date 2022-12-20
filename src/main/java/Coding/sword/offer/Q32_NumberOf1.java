package Coding.sword.offer;

/**
 * 从 1 到 n 整数中 1 出现的次数：
 * 输入一个整数 n ，求从 1 到 n 这 n 个整数的十进制表示中 1 出现的次数.
 * 例如输入 12，有 1, 10, 11, 12 共 4个数， 1 出现了 5 次.
 * --------------------------------------------------
 * 这一题的正常解法比较容易想到，但是优化的解法，比较难想到，也没有通用性可言
 */
public class Q32_NumberOf1 {
    public static void main(String[] args) {
        Q32_NumberOf1 solver = new Q32_NumberOf1();
        int n = 12;
        System.out.println("number of 1 in " + n + " is: " + solver.numberOf1Between1AndN(n));
        //System.out.println( 7 / 2);
        //System.out.println( 7 % 2);
        //System.out.println( 7 % 10);
        //System.out.println( 7 / 10);
        //System.out.println( 17 / 10);
        //System.out.println( 17 % 10);
    }

    /**
     * 解法1，比较平庸的写法
     */
    public int numberOf1Between1AndN(int n){
        int number = 0;
        for(int i = 1; i <= n; i++){
            int num = i;
            while( num >= 1){
                if (num % 10 == 1) number += 1;
                num = num / 10;
            }
        }
        return number;
    }
}
