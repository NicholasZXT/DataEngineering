package Coding.sword.offer;

import java.util.Arrays;

/**
 * 顺时针由外圈向内圈打印矩阵
 */
public class Q20PrintMatrix {
    public static void main(String[] args){
        // 测试用例
        //int[][] array1 = {{1,2,3}, {4,5,6}};
        //int[][] array1 = {{1,2,3}};
        int[][] array1 = {{}};
        System.out.println(array1.length);
        System.out.println(array1[0].length);
        System.out.println(Arrays.toString(array1[0]));

    }

    public void printMatrix(int[][] matrix){
        int row_num = matrix.length;
        int col_num = matrix[0].length;
    }
}
