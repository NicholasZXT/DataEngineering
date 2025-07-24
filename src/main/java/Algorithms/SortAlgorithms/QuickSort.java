package Algorithms.SortAlgorithms;

import java.util.Scanner;

/**
 *快速排序算法
 */
public class QuickSort {

    //main函数测试实例
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Integer [] array = new Integer[]{9,8,3,7,1,2,5,6};
        int size = array.length;
        //int size = 0;
        //System.out.println("请输入数组长度：");
        //size = scan.nextInt();
        //Integer [] array = new Integer[size];
        //ArrayList没有实现Comparable接口，所以不行
        //ArrayList<Integer> array = new ArrayList<Integer>(0);
        //System.out.println("请输入一个整数数组(eof结束)：");
        //for ( int i = 0; i < size; i++){
        //    array[i] = scan.nextInt();
        //}
        System.out.println("输入的数组为：");
        QuickSort.show(array);

        QuickSort.sort(array,0,size-1);

        System.out.println("快速排序后为：");
        QuickSort.show(array);

    }

    /**
     * 快速排序算法的实现
     * @param array 待排序数组
     * @param left 数组左索引
     * @param right 数组右索引
     */
    public static void sort(Comparable[] array, int left, int right){
        if ( left >= right) return;
        //首先找到用于切分的主轴元素——也就是该元素的index
        //partition函数执行后，有array[left: split_index-1] <= array[split_index] <= array[split_index+1: right]
        int split_index = partition(array, left, right);
        System.out.print("中间排序结果：");
        show(array);
        //递归的排序 左子数组 和 右子数组
        sort(array, left, split_index-1);
        sort(array, split_index+1, right);
    }

    /**
     * 快速排序的 关键方法，通过递归的调用此方法来实现排序数组的
     * 根据 array[left] 元素，将数组分隔成两部分，左边的部分比 array[left] 小，右边的部分比 array[left] 大，
     * 并返回 array[left] 元素最终的位置
     * @param array 待分割的数组
     * @param left 数组的 左界限 下标
     * @param right 数组的 右界限 下标
     * @return 返回 切分元素 array[left] 最终的位置下标
     */
    private static int partition(Comparable[] array, int left, int right){
        //low 和 high 是扫描的指针
        int low = left;
        int high = right + 1; //这里right要加1,是为了避免第一次使用右指针时跳过最右边的元素
       //这里选择最左边的第一个元素作为主轴元素
        Comparable split_element = array[left];
        System.out.print("split_element: " + split_element + "\n");
        //System.out.println(split_element);
        //循环扫描
        while (true){
            //下面的两个指针移动时，++和--必须在前：因为两个 while 循环之后，需要使用 low 和 high 的值执行 exchange
            //左指针 向右 移动，寻找 第一个 比split_element 大 的元素（这个元素会被交换到split_element之后）
            while (less(array[++low], split_element)) {
                if(low == right) break;
                // 不能在这里使用 low++，这样跳出 while 循环时，low 的值会比实际停止时多 + 1
            }
            //右指针 向左 移动，寻找第一个比split_element 小 的元素（它会被交换到split_element之前）
            while (less(split_element, array[--high])) {
                if(high == left) break;
            }
            //如果左右指针相等了，就跳出while循环，不执行下面的exchange，此时的high就是split_element应该在的位置
            if(low >= high) break;
            exchange(array, low, high);
        }
        //由于split_element = array[left]，扫描结束后high是split_element的位置，交换过去
        exchange(array, left, high);
        return high;
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
