package SortAlgorithms;


/**
 * 起泡排序的实现
 * 步骤（）：
 * 1. 第 i 个记录和第 i+1 个记录比较，较大的记录往后移动
 * 2. 一轮过后，第 N 个元素为最大
 * 3. 第 2 轮 比较从 1 进行到 N-1
 * 4. 停止的标志是，某一轮中，没有发生过交换
 *
 * 平均时间复杂度 O(n^2)，空间复杂度 O(1)
 */
public class BubbleSort {
    //TODO 起泡排序待实现
    /**
     * 排序算法的核心
     * 所有的排序算法都在这个函数实现
     * Comparable是一个接口
     */
    public static void sort(Comparable[] array){

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
