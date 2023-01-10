package Coding.sword.offer;

/**
 * 丑数指的是只包含因子 2、3、5 的数.
 * 例如 6、8 都是丑数，但 14 不是，因为它包含因子 7。习惯上我们把 1 当做第1个丑数。
 * 求从小到大顺序的第1500个丑数.
 * ---------------------------------
 * 这一题的解法也有难度
 */
public class Q34_UglyNumber {
    public static void main(String[] args) {
        Q34_UglyNumber solver = new Q34_UglyNumber();
        int index = 1500;
        int num = solver.findUglyNumber(index);
        System.out.println("ugly number in " + index + " is : " + num);
    }

    public int findUglyNumber(int index){

        return 1;
    }

    public boolean isUglyNumber(int num){
        /**
         * 判定一个数是不是丑数
         */
        boolean flag = false;
        while (num % 2 == 0)
            num /= 2;
        while (num % 3 == 0)
            num /= 3;
        while (num % 5 == 0)
            num /= 5;
        if (num == 1)
            flag = true;
        return flag;
    }
}
