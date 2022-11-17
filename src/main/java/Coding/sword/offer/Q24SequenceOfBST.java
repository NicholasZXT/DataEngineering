package Coding.sword.offer;

/**
 * 输入一个整数数组，判断该数组是否为一个二叉树搜索树的后序遍历结果，是返回true，否返回false.
 * 假设输入数组的任意两个结果都不相同.
 * 注意，二叉搜索树表明该树是经过排序的，左子节点 < 根节点 < 右子节点
 */
public class Q24SequenceOfBST {
    public static void main(String[] args) {
        int[] array1 = {5,7,6,9,11,10,8};
        int[] array2 = {7,4,6,5};

    }

    public static boolean verifySequenceOfBST(int[] array){
        if (array == null | array.length == 0)
            return false;
        int root = array[array.length-1];

        return true;
    }
}
