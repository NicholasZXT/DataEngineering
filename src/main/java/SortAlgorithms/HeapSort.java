package SortAlgorithms;


/**
 * 堆排序算法
 * 这里的堆是用数组实现的二叉堆的优先队列
 */
public class HeapSort<Key extends Comparable<Key>> {

//    main函数测试用例
    public static void main(String[] args) {
        Integer [] array = new Integer[]{9,8,3,7,1,2,5,6};
        int size = array.length;
        System.out.println("输入的数组为：");
        HeapSort.show(array);

        HeapSort.sort(array);

        System.out.println("快速排序后为：");
        HeapSort.show(array);

    }

// ----------- 以下为堆排序的实现-------------------

    /**
     * 排序算法的核心
     * 所有的排序算法都在这个函数实现
     * 注意，这里和二叉堆实现的优先序列不一样，那里自己定义数组时，第一个索引0是空出来的
     * 但是传入的排序数组第一个索引0是使用的，执行exchange和less函数的时候需要注意
     */
    public static void sort(Comparable[] array){
        int N = array.length;
//        下面这个for循环用于根据给定数组创建一个二叉堆
        for (int k = N/2; k >= 1; k--){
            sink(array, k, N);
        }
//        下面这for循环用于不断取出二叉堆中的最大元素，并放到末尾
        while (N > 1){
            exchange(array, 1, N--);
            sink(array, 1 ,N);
            System.out.print("排序中：");
            HeapSort.show(array);
        }
    }

    /**
     *二叉堆优先队列的下沉函数
     */
    private static void sink(Comparable[] array, int k, int N){
        while (2*k <= N){
//            2*k是k结点对应的左子节点
            int j = 2*k;
//            下面这句找出k节点左子节点和右子节点中较大的那个
//            由于传入的数组index=0是使用的，所以j实际对应的元素位置要-1
            if(j < N && less(array[j-1], array[j+1-1])) j++;
//            再判断子节点是否比父节点大
            if(!less(array[k-1], array[j-1])) break;
//            然后将k节点和较大的子节点交换
            exchange(array, k, j);
            k = j;
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
     * 真正执行交换时，i和j都减1，是因为i、j是用于计算使用，实际由于数组使用了index=0的位置，所以要减1
     */
    private static void exchange(Comparable[] array, int i, int j){
        Comparable t = array[i-1];
        array[i-1] = array[j-1];
        array[j-1] = t;
    }

//  ---------下面的方法都是次要的-------------
    /**
     * 用于打印
     * @param array
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
     * @param array
     * @return
     */
    private static boolean isSorted(Comparable[] array){
        for(int i = 1; i < array.length; i++){
//            这里直接使用less方法，没有创建对象，就是因为less是静态方法
            if ( less(array[i], array[i-1])) return false;
        }
        return true;
    }

}
