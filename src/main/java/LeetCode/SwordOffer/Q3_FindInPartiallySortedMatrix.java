package LeetCode.SwordOffer;

import java.util.Arrays;

/*
二维数组中的查找
在一个二维数组中，每一行从左到右递增，每一列从上到下递增。
请完成一个函数，它接受这样一个二维数组和整数，判断二维数组中是否有该整数。
 */
public class Q3_FindInPartiallySortedMatrix {
    public static void main(String[] args){
        int [][] array = {{1,2,8,9}, {2,4,9,12}, {4,7,10,13}};
        System.out.println(Arrays.deepToString(array));
        Q3_FindInPartiallySortedMatrix solver = new Q3_FindInPartiallySortedMatrix();
        int target1 = 3;
        boolean res1 = solver.findArray(array, target1);
        System.out.println("search target '" + target1 +"' result: " + res1);
    }

    public boolean findArray(int [][] array, int target){
        return true;
    }
}
