package Coding.sword.offer;

import java.util.Arrays;

public class Q14ReorderArray {

    public static void main(String[] args) {
        Q14ReorderArray solver = new Q14ReorderArray();
        // 测试
        //System.out.println(solver.isOdd(0));
        //System.out.println(solver.isOdd(2));
        //System.out.println(solver.isOdd(3));
        //System.out.println(solver.isOdd(10));
        //System.out.println(solver.isOdd(13));
        // 测试用例
        int [] a1 = {};
        int [] a2 = {1,2,3,4};
        int [] a3 = {4,2,3,1};
        int [] a4 = {4,2,3,1,5};
        int [] a5 = {1,3,5,7};
        int [] a6 = {2,4,6,8};
        solver.ReorderOddEven(a1);
        solver.ReorderOddEven(a2);
        solver.ReorderOddEven(a3);
        solver.ReorderOddEven(a4);
        solver.ReorderOddEven(a5);
        solver.ReorderOddEven(a6);
        System.out.println(Arrays.toString(a1));
        System.out.println(Arrays.toString(a2));
        System.out.println(Arrays.toString(a3));
        System.out.println(Arrays.toString(a4));
        System.out.println(Arrays.toString(a5));
        System.out.println(Arrays.toString(a6));

    }

    /**
     * 输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有的奇数位于数组前半部分，所有的偶数位于数组的后半部分
     * @param array
     * @return
     */
    public void ReorderOddEven(int[] array){
        /**
         * 采用一前一后的两个指针的方式
         */
        if (array == null | array.length <=1)
            return ;
        int p1 = 0;
        int p2 = array.length-1;
        int temp;
        while (p1 < p2){
            if (!isOdd(array[p1]) & isOdd(array[p2])){
                temp = array[p1];
                array[p1] = array[p2];
                array[p2] = temp;
                p1 += 1;
                p2 -= 1;
            }else if (isOdd(array[p1])){
                p1 += 1;
            }else if(!isOdd(array[p2])){
                p2 -= 1;
            }else {
                p1 += 1;
                p2 -= 1;
            }
        }
    }

    public boolean isOdd(int number){
        return number % 2 != 0;
    }
}
