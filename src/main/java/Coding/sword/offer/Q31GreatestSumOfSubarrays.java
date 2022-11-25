package Coding.sword.offer;

import java.util.Arrays;

/**
 * 连续子数组的最大和：
 * 给定一个整形数组，有正有负，其中任意长度的多个整数组成一个子数组。
 * 求所有子数组的和的最大值，要求时间复杂度为 O(n)。
 * 注意，不用保存最大子数组的每个元素，只需要输出最大和即可
 * 比如给定数组 {1,-2,3,10,-4,7,2,-5}，和最大的子数组为 {3,10,-4,7,2}，和为 18.
 *
 * ------- 这一题有难度，值得好好看看 ---------
 */
public class Q31GreatestSumOfSubarrays {
    public static void main(String[] args) {
        int[] array = {1,-2,3,10,-4,7,2,-5};
        findGreatestSubarrays(array);
        findGreatestSubarraysWithDynamic(array);
    }

    // 解法一：一次遍历
    public static void findGreatestSubarrays(int[] array){
        if (array == null | array.length == 0)
            return;
        int maxSum = 0;
        int sum = 0;
        for(int i = 0; i < array.length; i++){
            sum = sum + array[i];
            // 注意，一旦相加之后的和比 array[i] 本身还小，那么 i 之前的子数组就都不用考虑了，
            // 因为最大和子数组一定 >= max(array) >= 数组中的任意一个元素
            if (sum <= array[i]){
                sum = array[i];
                maxSum = sum;
            }else if(sum >= maxSum){
                maxSum = sum;
            }
        }
        System.out.println("max subarray sum of " + Arrays.toString(array) + " is: " + maxSum);
    }

    // 解法二：动态规划，这个方式需要特别注意
    public static void findGreatestSubarraysWithDynamic(int[] array){
        if (array == null | array.length == 0)
            return;
        int[] maxSum = {0};  // 写成数组只是为了能够实现引用传递
        int last_max_sum = findNextSubarray(array, array.length-1, maxSum);
        System.out.println("max subarray sum of " + Arrays.toString(array) + " is: " + maxSum[0]);
    }

    public static int findNextSubarray(int[] array, int current, int[] maxSum){
        /**
         * 动态规划里，f(i) 表示的是该数组中以 i 结尾的和最大的子数组，但是，这并不是所有子数组中和最大的子数组
         * 这里传入的参数 maxSum 就是为了记录 f[0],f[1],...,f[n-1] 中最大的那个！！！
         */
        if (current < 0 | current >= array.length)
            return 0;
        int prev_max_sum = findNextSubarray(array, current-1, maxSum);
        if (prev_max_sum <=0){
            maxSum[0] = array[current];
            return array[current];
        }
        else {
            int cur_sum = prev_max_sum + array[current];
            if (cur_sum > maxSum[0])
                maxSum[0] = cur_sum;
            return cur_sum;
        }
    }
}
