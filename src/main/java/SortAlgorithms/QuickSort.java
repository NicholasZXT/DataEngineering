package SortAlgorithms;


/**
 *快速排序算法的实现
 */

public class QuickSort {
    //    Comparable是一个接口
    public static void sort(Comparable[] a){
        /**
         * 排序算法的核心
         * 所有的排序算法都在这个函数里实现
         */
    }

    private static boolean less(Comparable v, Comparable w){
        /**
         * 用于比较两个元素，v < w 时返回compareTo返回负数——返回值为True
         */
        return v.compareTo(w) < 0;
    }

    private static void exchange(Comparable[] a, int i, int j){
        /**
         * 用于交换数组a中i和j位置的元素
         */
        Comparable t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    //    下面的方法都是次要的
    private static void show(Comparable[] a){
        /**
         * 用于打印
         */
        for ( int i =0 ; i < a.length; i++){
            System.out.println(a[i]);
        }
    }

    private static boolean isSorted(Comparable[] a){
        /**
         * 测试数组元素是否有序
         */
        for(int i = 1; i < a.length; i++){
            if ( less(a[i], a[i-1])) return false;
        }
        return true;
    }

}
