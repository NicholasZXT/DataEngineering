package Coding.sword.offer;

import java.util.Arrays;

/**
 * 输入一个整数数组，判断该数组是否为一个二叉树搜索树的后序遍历结果，是返回true，否返回false.
 * 假设输入数组的任意两个结果都不相同.
 * 注意，二叉搜索树表明该树是经过排序的，左子节点 < 根节点 < 右子节点
 * ---------------- 这一题有难度，自己花了不少时间 -----------------
 */
public class Q24SequenceOfBST {
    public static void main(String[] args) {
        int[] array1 = {5,7,6,9,11,10,8};  // 是
        int[] array2 = {7,4,6,5};          // 否
        System.out.println("check result for array " + Arrays.toString(array1) + " is : " + verifySequenceOfBST(array1));
        System.out.println("check result for array " + Arrays.toString(array2) + " is : " + verifySequenceOfBST(array2));
    }

    public static boolean verifySequenceOfBST(int[] array){
        if (array == null | array.length == 0)
            return false;
        return verifyArray(array,0, array.length-1);
    }

    public static boolean verifyArray(int[] array, int start, int root){
        // array 存放数组，start是当前子数组起始索引位置，root是根节点索引位置（也是当前子数组的结束位置）
        if (array == null | root-start < 0)
            return false;
        // 只有根节点一个节点，为true
        if (root - start == 0)
            return true;
        // 当前子数组内叶子节点的结束位置
        int end = root - 1;
        // 找到分割左右子树的位置索引（第一个就行）
        int split_index;
        for (split_index = start; split_index <= end; split_index++){
            if (array[split_index] >= array[root])
                break;
        }
        // 检查左子树元素是否都比根节点小  ---- 这一步检查没有必要，因为上的查找过程已经做过了
        //boolean left_check = true;
        //for(int index = start; index < split_index; index++){
        //    if (array[index] > array[root]){
        //        left_check = false;
        //        break;
        //    }
        //}
        // 检查右子树元素是否都比根节点大
        boolean right_check = true;
        for (int index = split_index+1; index <= end; index++){
            if (array[index] < array[root]){
                right_check = false;
                break;
            }
        }
        // 不满足，表示当前子序列不是后序输出
        if (!right_check)
            return false;
        // 递归检查左右子树内部
        boolean left_sub_check = verifyArray(array, start, split_index-1);
        boolean right_sub_check = verifyArray(array, split_index, end);
        // 返回结果
        return left_sub_check & right_sub_check;
    }
}
