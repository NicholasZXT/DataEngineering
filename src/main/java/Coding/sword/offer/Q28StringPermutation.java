package Coding.sword.offer;

import java.util.Arrays;

/**
 * 字符串的排列：输入一个字符串，打印出该字符串中字符的所有排列.
 * 比如输入 abc，则打印出：abc, acb, bac, bcd, cab, cba
 *
 * ----- 这一题有难度，不好理解 ---------------
 */
public class Q28StringPermutation {
    public static void main(String[] args) {
        // 测试用例
        String input1 = "abc";
        String input2 = "ab";
        String input3 = "";
        permutationShow(input1);
        System.out.println("--------------");
        permutationShow(input2);
        System.out.println("--------------");
        permutationShow(input3);
    }

    public static void permutationShow(String input){
        char[] input_array = input.toCharArray();
        permutation(input_array, 0);
    }

    public static void permutation(char[] input, int start){
        if (input == null | start >= input.length)
            return;
        if (start == input.length-1)
            // 到达末尾的时候，就输出当前的 char 数组
            System.out.println(Arrays.toString(input));
        else {
            for(int i = start; i < input.length; i++){
                char temp = input[i];
                input[i] = input[start];
                input[start] = temp;
                permutation(input, start+1);
                // 注意和start位置的字符还需要换回来
                temp = input[i];
                input[i] = input[start];
                input[start] = temp;
                // 控制换行
                //if (start == 0)
                //    System.out.println();
            }
        }
    }
    public static void show(String input){
        for(int i = 0; i < input.length(); i++){
            System.out.print(input.charAt(i) + ", ");
        }
        System.out.println();
    }
}
