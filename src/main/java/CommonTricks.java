/**
 * 这里记录了刷LeetCode中使用过的一些Java技巧
 */

import java.util.ArrayList;
import java.util.Arrays;

public class CommonTricks {

    public static void main(String[] args) {

        //1. Java数组建立之后，数组内的值默认初始化为0
        int[][] array = new int[3][3];
//        System.out.println(array[0][0]);

        // 快速打印 Array
        String array_string = Arrays.toString(array);
        // 快速打印 List
        //String array_string = Arrays.toString(list.toArray());


        // 快速初始化数组
        int[] nums = new int[]{-1,0,1,2,-1,-4};
        ArrayList<Integer> array_1 = new ArrayList<Integer>(Arrays.asList(1,2,3));
        //ArrayList<Integer> array_2 = new ArrayList<Integer>({1,2,3});  // 这个不行
        System.out.println(Arrays.toString(array_1.toArray()));

//      如果要在for循环中多次修改一个String类型，应当使用StringBuilder类，这样不会产生一个新对象
        StringBuilder sBuffer = new StringBuilder();
        System.out.println(sBuffer);
        sBuffer.append('a');
        System.out.println(sBuffer);
        sBuffer.append("bcd");
        System.out.println(sBuffer);


    }
}
