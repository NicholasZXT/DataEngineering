package Coding.sword.offer;

import java.util.PriorityQueue;

/**
 * 输入n个整数，找出其中最小的 k 个数.
 * 比如输入 {4,5,1,6,2,7,3,8} 共8个数，最小的4个是 1,2,3,4
 */
public class Q30KLeastNum {
    public static void main(String[] args) {
        int[] array = {4,5,1,6,2,7,3,8};
        findKLeastNum(array, 5);
    }

    // 使用最大堆或者优先队列，如果允许的话
    public static void findKLeastNum(int[] array, int k){
        PriorityQueue<Integer> pq = new PriorityQueue<>(k);
        for(int value: array){
            pq.add(value);
        }
        int i = 0;
        while (!pq.isEmpty() & i < k){
            int value = pq.poll();
            System.out.print(value + ", ");
            i++;
        }
        System.out.println();
    }

    // 不使用优先队列的解法，结合Q29一起看
    public static void findKLeastNum2(int[] array, int k){

    }
}
