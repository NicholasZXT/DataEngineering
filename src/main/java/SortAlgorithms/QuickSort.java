package SortAlgorithms;


import sun.awt.geom.AreaOp;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *快速排序算法的实现
 */

public class QuickSort {

    //main函数测试实例
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Integer [] array = new Integer[]{1,9,3,7,5};
        int size = 5;
//        System.out.println("请输入数组长度：");
//        size = scan.nextInt();
//        Integer [] array = new Integer[size];
//        ArrayList没有实现Comparable接口，所以不行
//        ArrayList<Integer> array = new ArrayList<Integer>(0);
//        System.out.println("请输入一个整数数组(eof结束)：");
//        for ( int i = 0; i < size; i++){
//            array[i] = scan.nextInt();
//        }
        System.out.println("输入的数组为：");
        QuickSort.show(array);

        QuickSort.sort(array,0,size-1);

        System.out.println("快速排序后为：");
        QuickSort.show(array);


    }

    //    Comparable是一个接口
    public static void sort(Comparable[] array, int left, int right){

        if ( left >= right) return;
        int split_index = partition(array, left, right);
        sort(array, left, split_index-1);
        sort(array, split_index+1, right);
    }

    private static int partition(Comparable[] array, int left, int right){

        int low = left;
        int high = right + 1;
        Comparable split_element = array[left];
        System.out.print("split_element：  ");
        System.out.println(split_element);
        while (true){
            while (less(array[++low], split_element)) if(low == high) break;
            while (less(split_element, array[--high])) if(low == high) break;

            if(low >= high) break;
            exchange(array, low, high);
            System.out.print("排序过程：");
            show(array);
        }
        exchange(array, left, high);
        return high;
    }

    private static boolean less(Comparable v, Comparable w){
        /**
         * 用于比较两个元素，v < w 时返回compareTo返回负数——返回值为True
         */
        return v.compareTo(w) < 0;
    }

    private static void exchange(Comparable[] array, int i, int j){
        /**
         * 用于交换数组array中i和j位置的元素
         */
        Comparable t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

    //    下面的方法都是次要的
    private static void show(Comparable[] array){
        /**
         * 用于打印
         */
        for ( int i =0 ; i < array.length; i++){
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.print("\n");
    }

    private static boolean isSorted(Comparable[] array){
        /**
         * 测试数组元素是否有序
         */
        for(int i = 1; i < array.length; i++){
            if ( less(array[i], array[i-1])) return false;
        }
        return true;
    }

}
