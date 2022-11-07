package Coding.sword.offer;

public class Q9Fibonacci {

    public static void main(String[] args){
        Q9Fibonacci solver = new Q9Fibonacci();
        int n = 0;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));
        n = 1;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));
        n = 2;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));
        n = 3;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));
        n = 4;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));
        n = 100;
        System.out.println("Fibonacci value of '" + n + "' is: " + solver.fibonacci(n));

    }

    /**
     * 斐波那契数列的非递归解法
     * @param n
     * @return
     */
    public long fibonacci(int n){
        int[] init_values = {0, 1};
        if (n < 2)
            return init_values[n];
        long fib_n_1 = init_values[1];
        long fib_n_2 = init_values[0];
        long fib_n = 0;
        for (int i = 2; i <= n; i++){
            fib_n = fib_n_1 + fib_n_2;
            fib_n_2 = fib_n_1;
            fib_n_1 = fib_n;
        }
        return fib_n;
    }

    /**
     * 一只青蛙可以一次跳 1 级台阶，也可以一次跳 2 级，求跳上 n 级台阶一共有多少种方式
     * 这个也可以转成斐波那契数列的问题：
     * n = 1时，只有一种跳法，f(1) = 1；n = 2时，有 f(2) = 2 种跳法
     * 考虑 n 级时，第一步可以跳 1 级，后面的 n-1 级跳法为 f(n-1)；第一步也可以直接跳 2 级，此时后面的 n-2 级跳法为 f(n-2)
     * 所以 f(n) = f(n-1) + f(n-2)，这个就是一个斐波那契数列。
     */

}
