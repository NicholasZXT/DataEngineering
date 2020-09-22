package LeetCode;


/**
 * 这里记录了刷LeetCode中使用过的一些Java技巧
 */
public class CommonTricks {

    public static void main(String[] args) {

        //1. Java数组建立之后，数组内的值默认初始化为0
        int[][] array = new int[3][3];
//        System.out.println(array[0][0]);

//        2. 如果要在for循环中多次修改一个String类型，应当使用StringBuilder类，这样不会产生一个新对象
        StringBuilder sBuffer = new StringBuilder();
        System.out.println(sBuffer);
        sBuffer.append('a');
        System.out.println(sBuffer);
        sBuffer.append("bcd");
        System.out.println(sBuffer);


    }
}
