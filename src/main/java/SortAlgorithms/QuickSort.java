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
        Integer [] array = new Integer[]{9,8,3,7,1,2,5,6};
        int size = array.length;
//        int size = 0;
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

    /**
     * 这个方法是执行排序的主要部分
     */
    public static void sort(Comparable[] array, int left, int right){
        if ( left >= right) return;
//        首先找到用于切分的主轴元素——也就是该元素的index
//        partition函数执行后，有array[left: split_index-1] <= array[split_index] <= array[split_index+1: right]
        int split_index = partition(array, left, right);
        System.out.print("中间排序结果：");
        show(array);
//        递归的排序左边的子数组和右边的子数组
        sort(array, left, split_index-1);
        sort(array, split_index+1, right);
    }

    /**
     * 这个方法用于切分数组
     */
    private static int partition(Comparable[] array, int left, int right){
//        low和high是扫描的指针
        int low = left;
        int high = right + 1; //这里right要加1,是为了避免第一次使用右指针时跳过最右边的元素
//       这里选择最左边的第一个元素作为主轴元素
        Comparable split_element = array[left];
        System.out.print("split_element: ");
        System.out.println(split_element);
//        循环扫描
        while (true){
//            下面的两个指针移动时，++和--必须在前，因为后续要使用这个值
//            左指针向右移动，寻找第一个比split_element 大 的元素（这个元素会被交换到split_element之后）
            while (less(array[++low], split_element)) if(low == right) break;
//            右指针向左移动，寻找第一个比split_element 小 的元素（它会被交换到split_element之前）
            while (less(split_element, array[--high])) if(high == left) break;
//            如果左右指针相等了，就跳出while循环，不执行下面的exchange，此时的high就是split_element应该在的位置
            if(low >= high) break;
            exchange(array, low, high);
        }
//        由于split_element = array[left]，扫描结束后high是split_element的位置，交换过去
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
     * 用于交换数组array中i和j位置的元素
     */
    private static void exchange(Comparable[] array, int i, int j){
        Comparable t = array[i];
        array[i] = array[j];
        array[j] = t;
    }

// ----------下面的方法都是次要的-----------
    /**
     * 用于打印
     */
    private static void show(Comparable[] array){
        for ( int i =0 ; i < array.length; i++){
            System.out.print(array[i]);
            System.out.print(" ");
        }
        System.out.print("\n");
    }

    /**
     * 测试数组元素是否有序
     */
    private static boolean isSorted(Comparable[] array){

        for(int i = 1; i < array.length; i++){
            if ( less(array[i], array[i-1])) return false;
        }
        return true;
    }

}
