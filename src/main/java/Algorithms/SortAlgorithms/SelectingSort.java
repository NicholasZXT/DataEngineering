package Algorithms.SortAlgorithms;


/**
 * 选择排序的实现类
 * 步骤（升序）：
 * 1. 找到数组中 最小 的元素
 * 2. 将它和第 1 个元素交换位置
 * 3. 在剩下的元素中找 最小 的元素，
 * 4. 将它和第 2 个元素交换位置
 * 5. 如此下去，直到第 N-1 个元素
 *
 * 时间复杂度 O(n^2)，空间复杂度 O(1)
 *
 * 自己实现难度：easy
 */
public class SelectingSort {
    /**
     * 选择排序的实现方法
     */
    public static void sort(Comparable[] array){
        int length = array.length;
        // 这里 i 循环到 length-2 就行了，不用到最后一个元素
        //for (int i = 0; i < length; i++){
        for (int i = 0; i < length-1; i++){
            // 只需要记录下标就行，不需要记录具体值
            //Comparable temp = array[i];
            int min_index = i;
            // 下面这个 for 循环找出剩余元素中最小位置的 下标，放入 temp 中
            for (int j = i+1; j < length; j++){
                if (less(array[j], array[min_index])) min_index = j;
            }
            exchange(array, i, min_index);
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

    //---------下面的方法都是次要的----------
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
