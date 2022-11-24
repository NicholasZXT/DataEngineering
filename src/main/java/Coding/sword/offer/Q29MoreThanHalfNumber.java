package Coding.sword.offer;

import java.util.HashMap;

/**
 * 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
 * 比如输入数组 {1,2,3,2,2,2,5,4,2}，输出2.
 *
 * ------- 这题作者的想法和我的不太一样，作者不考虑使用hashmap，让我有点困惑 --------
 */
public class Q29MoreThanHalfNumber {
    public static void main(String[] args) {
        int[] array = {1,2,3,2,2,2,5,4,2};
        moreThanHalfNum(array);
    }

    public static void moreThanHalfNum(int[] array){
        HashMap<Integer, Integer> map = new HashMap<>();
        int half = array.length / 2;
        for(int k: array){
            if (map.containsKey(k)){
                int value = map.get(k);
                map.replace(k, value+1);
            }else {
                map.put(k, 1);
            }
        }
        for(int k: map.keySet()){
            if(map.get(k) >= half)
                System.out.println("found value: " + k + " with count: " + map.get(k));
        }
    }
}
