package DataStructures;


import java.util.Scanner;

/**
 * 二叉堆实现的最大值优先队列
 * 这里的二叉堆是用数组来实现的，而不是链表
 * 这个二叉堆实现的优先队列和堆排序有关
 */
public class HeapMaxPriorQueue<Key extends Comparable<Key>> {

    public static void main(String[] args) {
        Integer [] array = new Integer[]{2,8,3,7,1,9,5,6};
        int size = array.length;
        System.out.print("数组为：");
        for (int ele:array){ System.out.print(ele); System.out.print(" ");}
        System.out.println();

//        创建优先队列，注意，要指定泛型参数
        HeapMaxPriorQueue<Integer> hpq = new HeapMaxPriorQueue<Integer>(size);
        for (int ele:array){
            hpq.insert(ele);
            System.out.print("插入中，此时优先队列内元素为：");
            hpq.show();
        }
        System.out.print("最终优先队列内元素为：");
        hpq.show();

        System.out.println("-----执行删除最大元素--------");
        int max = hpq.delMax();
        System.out.print("最大元素为：");
        System.out.println(max);
        System.out.print("队列内剩余元素为：");
        hpq.show();
    }

//    ---------------优先队列的二叉堆实现-----------------

//    存储二叉堆的数组，用于表示优先队列
    private Key[] pq;
//    优先队列的里的元素个数
    private int N = 0;

    /**
     * 构造函数，创建一个最大容量为max的优先队列,这个队列里的元素为空
     * @param max
     */
    public HeapMaxPriorQueue(int max){
//        这里选择 max+1，是因为使用数组来表示二叉堆时，index=0的部分不使用
        pq = (Key[]) new Comparable[max+1];
    }

    /**
     * 插入元素
     * 创建优先队列的过程，就是不断调用这个插入函数的过程
     * @param v
     */
    public void insert(Key v){
//        这里用++N而不是N++
//        插入到最后一个节点，然后执行上浮操作
        pq[++N] = v;
//        只有这里用到了上浮函数，因为每次都是插入到最后一个叶子节点，然后通过执行上浮操作来恢复堆的状态
        swim(N);
    }

    /**
     * 返回最大的元素
     * @return
     */
    public Key max(){
        return pq[N];
    }

    /**
     * 删除最大的元素
     * @return
     */
    public Key delMax(){
        Key max = pq[1];
//        下面这句执行了好几个操作：
//        1. 交换根节点(max)和最后一个叶子节点
//        2. N自减1，减少了优先队列元素个数,这时候max所在的索引N实际上已经不被队列承认了
        exchange(1, N--);
//        手动释放max元素的空间
        pq[N+1] = null;
//        将交换到各节点的元素下沉
//        只有这里用到了这个下沉函数，因为删除最大元素之后，总是把最后一个元素调上来，
//        放到根节点的位置，然后调用下沉函数来恢复堆的状态
        sink(1);
        return max;
    }

//    下面这两个函数对于二叉堆的实现很重要

    /**
     * 由下至上的堆有序化——上浮
     * 用于处理由于某个结点比它的 父节点 更大 导致的堆的状态被打破
     * k是该节点在数组中的下标
     * 这个上浮函数实际上只用于优先队列的创建过程
     * @param k
     */
    private void swim(int k){
        while (k >1 && less(k/2, k)){
//            和 父节点 交换位置
            exchange(k/2, k);
            k = k/2;
        }
    }

    /**
     * 由上至下的堆有序化——下沉
     * 用于处理由于某个结点比它的 子节点 更小 导致的堆的状态被打破
     * k是该节点在数组中的下标
     * 这个下沉函数只用于优先队列删除最大元素之后，调整优先队列的过程
     */
    private void sink(int k){
        while (2*k <= N){
//            2*k是k结点对应的左子节点
            int j = 2*k;
//            下面这句找出k节点左子节点和右子节点中较大的那个
            if(j < N && less(j, j+1)) j++;
//            再判断子节点是否比父节点大
            if(!less(k,j)) break;
//            然后将k节点和较大的子节点交换
            exchange(k, j);
            k = j;
        }
    }

    private boolean less(int i, int j){
        /*用于比较两个元素，v < w 时返回compareTo返回负数——返回值为True*/
        return pq[i].compareTo(pq[j]) < 0;
    }

    private void exchange(int i, int j){
        /*用于交换元素*/
        Key t = pq[i];
        pq[i] = pq[j];
        pq[j] = t;
    }

// --------以下为辅助的方法--------
    public int size(){
        /*返回优先队列的元素个数*/
        return N;
    }

    public boolean isEmpty(){
        /*返回队列是否为空*/
        return N==0;
    }

    public void show(){
        for (int i = 1; i <= N; i++){
            System.out.print(pq[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

}
