package Algorithms.SortAlgorithms;

import java.util.Arrays;

/**
 * 简单插入排序
 * 每次插入一个元素到末尾，检查此时是否有序，如果无序，就将新插入的元素逐个向前移动，直到序列重新有序
 *
 * 时间复杂度 ，空间复杂度
 *
 * 自己实现难度： easy
 */
public class InsertingSort {

    public static void main(String[] args) {
        Integer[] arr1 = {1, 8, 5, 7, 11, 3};
        Integer[] arr2 = {};
        Integer[] arr3 = {1};
        InsertingSort.sort(arr1);
        System.out.println(Arrays.toString(arr1));
        InsertingSort.sort(arr2);
        System.out.println(Arrays.toString(arr2));
        InsertingSort.sort(arr3);
        System.out.println(Arrays.toString(arr3));

    }

    /**
     * 插入排序的实现
     * @param array
     */
    public static void sort(Comparable[] array){
        int length = array.length;
        if (length < 2) return;
        for (int i = 1; i < length; i++){
            int cmp = array[i-1].compareTo(array[i]);
            while (cmp > 0){
                exchange(array, i-1, i);
                i--;
                cmp = array[i-1].compareTo(array[i]);
            }
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
