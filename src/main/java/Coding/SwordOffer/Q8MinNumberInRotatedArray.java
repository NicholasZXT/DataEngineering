package Coding.SwordOffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Q8MinNumberInRotatedArray {
    public static void main(String[] args) throws IOException {
        // 测试用例
        int[] arr1 = new int[] {3, 4, 5, 1, 2};  // [1,2, 3,4,5]的旋转
        int[] arr2 = new int[] {1, 2, 3, 4, 5};  // [1,2,3,4,5]的旋转，旋转了0个元素
        int[] arr3 = new int[] {1, 0, 1, 1, 1};  // [0,1,1,1, 1]的旋转，旋转了1个元素
        int[] arr4 = new int[] {1, 1, 1, 0, 1};  // [0,1, 1,1,1]的旋转，旋转了3个元素
        int[] arr5 = new int[] {2, 2, 2, 0, 1};  // [0,1, 2,2,2]的旋转，旋转了3个元素
        int res = -1;

        Q8MinNumberInRotatedArray solver = new Q8MinNumberInRotatedArray();
        System.out.println(Arrays.toString(arr1) + " min: " + solver.FindMin(arr1));
        System.out.println(Arrays.toString(arr2) + " min: "  + solver.FindMin(arr2));
        System.out.println(Arrays.toString(arr3) + " min: "  + solver.FindMin(arr3));
        System.out.println(Arrays.toString(arr4) + " min: "  + solver.FindMin(arr4));
        System.out.println(Arrays.toString(arr5) + " min: "  + solver.FindMin(arr5));
    }
    /*
    给定一个升序数组旋转后的数组，返回其中的最小值
     */
    public int FindMin(int[] array) throws IOException {
        if (array.length == 0)
            throw new IOException();
        int low = 0, high = array.length-1;
        // mid 初始化必须为0，否则测试用例2中直接跳过while循环时，就会报错
        int mid = 0;
        // 循环条件里，必须要带上 = ，这样第一次执行时，即使 low 和 high 位置的数相等也能进入循环
        while (array[low] >= array[high]){
            if (high - low == 1){
                mid = high;
                break;
            }
            mid = (low + high)/2;
            // 特别要注意 3 者都相等的情况！！！此时两边的数组，至少有一边是全部相等的，只能使用顺序查找法
            if (array[low] == array[mid] && array[mid] == array[high])
                return minSearchInOrder(array, low, high);
            // 下面两个条件，必须要包含等于号
            if (array[low] <= array[mid]){
                low = mid;
            }else if(array[mid] <= array[high]){
                high = mid;
            }
        }
        return array[mid];
    }

    public int minSearchInOrder(int[] array, int low, int high){
        int min = array[low];
        for(int i = low+1; i<=high; i++){
            if (min > array[i])
                min = array[i];
        }
        return min;
    }
}
