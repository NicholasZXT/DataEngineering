/**
 * 这里记录了刷LeetCode中使用过的一些Java技巧
 */

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Arrays 类中封装了一系列操作（各种类型）数组的静态方法，常用的一些方法有：
 * 1. 二分查找： BinarySearch()，它有多个类型签名，适用于整型，字符串等
 * 2. 数组复制： copyOf(), copyOfRange()，同样有多个类型签名
 * 3. 数组排序： sort()
 * 4. 返回数组的字符串形式： toString(), deepToString()
 * 5. 快速创建一个List： asList()，
 */


public class CommonTricks {

    public static void main(String[] args) {

        //1. Java数组建立之后，数组内的值默认初始化为0
        int[][] array = new int[3][3];
        System.out.println(array[0][0]);

        // 快速初始化数组
        int[] nums = new int[] {-1,0,1,2,-1,-4};  // 一维数组
        int[][] nums2 = new int[][] {{-1,0,1}, {2,-1,-4}};  // 二维数组
        // 另一个快速初始化方式，更加简便
        int[] nums3 = {-1,0,1,2,-1,-4};  // 一维数组
        int[][] nums4 = {{-1,0,1}, {2,-1,-4}};  // 二维数组

        // 二维数组里，第一个索引取的是第一行
        System.out.println(nums4.length);      // 打印行数
        System.out.println(nums4[0].length);   // 打印列数

        // 使用ArrayList初始化数组
        ArrayList<Integer> array_1 = new ArrayList<Integer>(Arrays.asList(1,2,3));
        //ArrayList<Integer> array_2 = new ArrayList<Integer>({1,2,3});  // 这个不行

        //复制一个数组
        int[] arr = {3, 1, 5, 6, 4, 8};
        int[] arr_copy = Arrays.copyOf(arr, arr.length);

        // 快速打印 一维Array
        String array_string = Arrays.toString(array);
        // 快速打印 二维Array
        String nums2_string = Arrays.deepToString(nums2);
        System.out.println(nums2_string);
        // 快速打印 List
        //String array_string = Arrays.toString(list.toArray());
        //System.out.println(Arrays.toString(array_1.toArray()));
        // 数组切片
        int[] array_slice;
        array_slice = Arrays.copyOfRange(arr, 0, 3);
        System.out.println(Arrays.toString(array));
        System.out.println(Arrays.toString(array_slice));

        //如果要在for循环中多次修改一个String类型，应当使用StringBuilder类，这样不会产生一个新对象
        StringBuilder sBuffer = new StringBuilder();
        System.out.println(sBuffer);
        sBuffer.append('a');
        System.out.println(sBuffer);
        sBuffer.append("bcd");
        System.out.println(sBuffer);

        // char 类型的加法实际上是转成它对应的 ASCII 码进行的
        System.out.println( '3' - '1');
        System.out.println( '3' - 1);
        System.out.println( (int)('3' - 1));
        System.out.println( '3' - '1' + 2);
        System.out.println( 'c' - 'a');


    }
}
