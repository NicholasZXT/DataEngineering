/**
 * 这里记录了刷LeetCode中使用过的一些Java技巧
 */

import java.util.ArrayList;
import java.util.Arrays;

public class CommonTricks {

    public static void main(String[] args) {

        //1. Java数组建立之后，数组内的值默认初始化为0
        int[][] array = new int[3][3];
        System.out.println(array[0][0]);

        //复制一个数组
        int[] arr = {3, 1, 5, 6, 4, 8};
        int[] arr_copy = Arrays.copyOf(arr, arr.length);

        // 快速初始化数组
        int[] nums = new int[] {-1,0,1,2,-1,-4};  // 一维数组
        int[][] nums2 = new int[][] {{-1,0,1}, {2,-1,-4}};  // 二维数组
        // 另一个快速初始化方式，更加简便
        int[] nums3 = {-1,0,1,2,-1,-4};  // 一维数组
        int[][] nums4 = {{-1,0,1}, {2,-1,-4}};  // 二维数组
        // 使用ArrayList初始化数组
        ArrayList<Integer> array_1 = new ArrayList<Integer>(Arrays.asList(1,2,3));
        //ArrayList<Integer> array_2 = new ArrayList<Integer>({1,2,3});  // 这个不行

        // 快速打印 一维Array
        String array_string = Arrays.toString(array);
        // 快速打印 二维Array
        String nums2_string = Arrays.deepToString(nums2);
        System.out.println(nums2_string);
        // 快速打印 List
        //String array_string = Arrays.toString(list.toArray());
        //System.out.println(Arrays.toString(array_1.toArray()));

        //如果要在for循环中多次修改一个String类型，应当使用StringBuilder类，这样不会产生一个新对象
        StringBuilder sBuffer = new StringBuilder();
        System.out.println(sBuffer);
        sBuffer.append('a');
        System.out.println(sBuffer);
        sBuffer.append("bcd");
        System.out.println(sBuffer);


    }
}
