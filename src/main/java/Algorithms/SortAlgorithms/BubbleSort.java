package Algorithms.SortAlgorithms;


import java.util.Arrays;

/**
 * 起泡排序的实现
 * 步骤（升序）：
 * 1. 第 i 个记录和第 i+1 个记录比较，较大的记录往后移动
 * 2. 一轮过后，第 N 个元素为最大
 * 3. 第 2 轮 比较从 1 进行到 N-1
 * 4. 停止的标志是，某一轮中，没有发生过交换
 *
 * 平均时间复杂度 O(n^2)，空间复杂度 O(1)
 *
 * 自己实现难度: easy
 */
public class BubbleSort {

    public static void main(String[] args){
        Integer[] arr1 = {1, 8, 5, 7, 11, 3};
        Integer[] arr2 = {};
        Integer[] arr3 = {1};
        BubbleSort.sort(arr1);
        System.out.println(Arrays.toString(arr1));
        BubbleSort.sort(arr2);
        System.out.println(Arrays.toString(arr2));
        BubbleSort.sort(arr3);
        System.out.println(Arrays.toString(arr3));

    }

    /**
     * 排序算法的核心
     * 所有的排序算法都在这个函数实现
     * Comparable是一个接口
     */
    public static void sort(Comparable[] array){
        int length = array.length;
        boolean exchange_flag;
        int cmp;
        for(int end = length - 1; end > 1; end--){
            // 标识本轮是否发生交换的flag
            exchange_flag = false;
            for(int i = 0; i < end; i++){
                cmp = array[i].compareTo(array[i+1]);
                if (cmp > 0) {
                    exchange(array, i, i+1);
                    exchange_flag = true;
                }
            }
            if (!exchange_flag) break;
        }

    }

    /**
     * 用于比较两个元素，v < w 时返回compareTo返回负数——返回值为True
     */
    private static boolean less(Comparable v, Comparable w){
        return v.compareTo(w) < 0;
    }

    /**
     * 用于交换元素
     */
    private static void exchange(Comparable[] array, int i, int j){
        Comparable t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

    //---------以下为辅助方法----------
    /**
     * 用于打印
     * @param array 待打印的数组
     */
    private static void show(Comparable[] array){
        for ( int i =0 ; i < array.length; i++){
            System.out.print(array[i]);
            if (i < array.length-1) System.out.print(", ");
        }
        System.out.print("\n");
    }

    /**
     * 检查数组元素是否有序
     * @param array 待检查的数组
     * @return boolean
     */
    private static boolean isSorted(Comparable[] array){
        for(int i = 1; i < array.length; i++){
            //这里直接使用less方法，没有创建对象，就是因为less是静态方法
            if ( less(array[i], array[i-1])) return false;
        }
        return true;
    }

}
