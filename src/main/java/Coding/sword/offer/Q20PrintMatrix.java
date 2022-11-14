package Coding.sword.offer;

import java.util.Arrays;

/**
 * 顺时针由外圈向内圈打印矩阵
 * 这一题有点难度，需要仔细思考各种边界条件
 */
public class Q20PrintMatrix {
    public static void main(String[] args){
        // 测试用例
        int[][] array1 = {{1,2,3,4}, {5,6,7,8}, {9,10,11,12}, {13,14,15,16}};
        int[][] array2 = {{1,2,3,4}, {5,6,7,8}, {9,10,11,12}};
        int[][] array3 = {{1,2,3}, {4,5,6}, {7,8,9}, {10,11,12}};
        int[][] array4 = {{1,2,3}, {4,5,6}, {7,8,9}};
        int[][] array5 = {{1,2,3}, {4,5,6}};
        int[][] array6 = {{1,2,3}};
        int[][] array7 = {{1},{2},{3}};
        int[][] array8 = {{}};
        //System.out.println(array1.length);
        //System.out.println(array1[0].length);
        //System.out.println(Arrays.toString(array1[0]));
        // 测试
        Q20PrintMatrix solver = new Q20PrintMatrix();
        solver.printMatrix(array1);
        System.out.println("-----------------------");
        solver.printMatrix(array2);
        System.out.println("-----------------------");
        solver.printMatrix(array3);
        System.out.println("-----------------------");
        solver.printMatrix(array4);
        System.out.println("-----------------------");
        solver.printMatrix(array5);
        System.out.println("-----------------------");
        solver.printMatrix(array6);
        System.out.println("-----------------------");
        solver.printMatrix(array7);
        System.out.println("-----------------------");
        solver.printMatrix(array8);
    }

    public void printMatrix(int[][] matrix){
        if (matrix == null) return;
        int row_num = matrix.length;
        int col_num = matrix[0].length;
        // start 指定每一圈开始的位置，此时行列索引相等
        int start = 0;
        // 打印每一圈的终止条件
        while ( 2*start < row_num & 2*start < col_num){
            // 每次打印一圈
            //System.out.println("row_num: " + row_num + ", col_num: " + col_num);
            printCircle(matrix, start);
            start ++;
        }
        //System.out.println();
    }

    private void printCircle(int[][] matrix, int start){
        // 设置 start 开始的一圈的行、列索引最大值
        int row_num = matrix.length - 1 - start;
        int col_num = matrix[0].length - 1 - start;
        //System.out.println("row_num: " + row_num + ", col_num: " + col_num);
        //System.out.println("row_num: " + (row_num+1) + ", col_num: " + (col_num+1));
        System.out.print("print circle " + start + ":  ");
        // 打印从左到右的一行：每一圈一定有这一行，不需要判断
        System.out.print("left -> right: ");
        for (int j = start; j <= col_num; j++){
            System.out.print(matrix[start][j] + ", ");
        }
        // 打印从上到下的一列：需要保证 行数 >= 2
        System.out.print(" up -> down: ");
        if (row_num + 1 - start >= 2) {
            // 注意 i 是从 start+1 行开始
            for (int i = start+1; i <= row_num; i++){
                System.out.print(matrix[i][col_num] + ", ");
            }
        }
        // 打印从右到左的一行：需要保证 行数 >= 2 且 列数 >=2
        System.out.print(" right -> left: ");
        if ( row_num + 1 - start >= 2 & col_num + 1 - start >= 2) {
            // 注意 j 是从 col_num-1 开始
            for (int j = col_num-1; j >= start; j--){
                System.out.print(matrix[row_num][j] + ", ");
            }
        }
        // 打印从下到上的一行: 需要保证 列数 >=2 且 行数 >= 3
        System.out.print(" down -> up: ");
        if (col_num + 1 - start >= 2 & row_num + 1 - start >= 3) {
            // 注意 i 的 开始结束 索引都要 -1
            for (int i = row_num-1; i >= start+1; i--){
                System.out.print(matrix[i][start] + ", ");
            }
        }
        System.out.println();
    }
}
