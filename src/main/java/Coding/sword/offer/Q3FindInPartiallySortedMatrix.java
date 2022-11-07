package Coding.sword.offer;

import java.util.Arrays;

/*
二维数组中的查找
在一个二维数组中，每一行从左到右递增，每一列从上到下递增。
请完成一个函数，它接受这样一个二维数组和整数，判断二维数组中是否有该整数。
 */
public class Q3FindInPartiallySortedMatrix {
    public static void main(String[] args){
        int [][] array = {{1,2,8,9}, {2,4,9,12}, {4,7,10,13}};
        System.out.println("matrix to search:");
        System.out.println(Arrays.deepToString(array));
        Q3FindInPartiallySortedMatrix solver = new Q3FindInPartiallySortedMatrix();
        int target1 = 3;
        int target2 = 9;
        int target3 = 1;
        int target4 = 13;
        int target5 = 18;
        //boolean res1 = solver.findArray(array, target1);
        solver.findArray(array, target1);
        solver.findArray(array, target2);
        solver.findArray(array, target3);
        solver.findArray(array, target4);
        solver.findArray(array, target5);
    }

    public boolean findArray(int [][] array, int target){
        boolean result = false;
        int row_length = array.length;
        int col_length = array[0].length;
        //System.out.println(row_length + ", " + col_length);
        int i = 0;
        int j = col_length - 1;
        while (i < row_length && j >=0){
            //if (i >= row_length || j < 0) break;
            if (array[i][j] == target){
                result = true;
                break;
            }else if (array[i][j] < target){
                i++;
            }else {
                j--;
            }
        }
        System.out.println("search target '" + target +"' result: " + result);
        if (result) System.out.println("matching index is: [" + i + ", " + j + "].");
        return result;
    }
}
