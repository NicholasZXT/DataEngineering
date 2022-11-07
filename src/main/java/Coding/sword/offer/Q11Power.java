package Coding.sword.offer;

public class Q11Power {
    public static void main(String[] args){

    }

    /**
     * 手动实现计算数字乘方的函数，也就是计算 base ^ n，
     * 特别要注意，n 可以是 0，负数，而不仅是整数，但不是小数
     * 这一题主要是考察边界和异常处理
     * @param n
     * @return
     */
    public double power(double base, int n){
        if ( equal(base, 0.0) && n < 0){
            System.out.println("Invalid input for base: " + base + ", and exponent: " + n);
            return 0.0;
        }


        return 1.0;
    }

    public double positivePower(double base, int n){
        // 只计算 正指数的 幂
        double result = 1;
        for (int i = 1; i <= n; i++){
            result = result * base;
        }
        return result;
    }

    public boolean equal(double a, double b){
        // 专门用于简单判断两个double 类型数字是否相等
        if ( a - b <= 0.00000001 && b - a <= -0.00000001)
            return true;
        else
            return false;
    }
}
