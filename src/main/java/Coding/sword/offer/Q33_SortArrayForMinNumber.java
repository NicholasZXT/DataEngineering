package Coding.sword.offer;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 把数组排成最小的数：
 * 输入一个正整数数组，把数组里的所有数字拼接起来排成一个数，打印能拼接出的所有数字中，最小的那个.
 * 例如输入数组 {3, 32, 321}，这三个数组能排成的最小数字为 321 32 3.
 * ----------------------------------------------------------
 * 这一题的解答也比较困难，而且感觉不实用
 */
public class Q33_SortArrayForMinNumber {
    public static void main(String[] args) {
        Q33_SortArrayForMinNumber solver = new Q33_SortArrayForMinNumber();
        //int[] array = {3, 32, 321};
        int[] array = {1, 32, 321};
        System.out.println("solver result for " + Arrays.toString(array) + " is : " + solver.maxNumber(array));
    }

    public String maxNumber(int[] array){
        if(array == null | array.length == 0)
            return "";
        String[] strNum = new String[array.length];
        for(int i = 0; i < array.length; i++){
            strNum[i] = String.valueOf(array[i]);
        }
        StringComparator stringComparator = new StringComparator();
        Arrays.sort(strNum, stringComparator);
        StringBuilder stringBuilder = new StringBuilder();
        for(String str: strNum) stringBuilder.append(str);
        return stringBuilder.toString();
        //return "123";
    }
}

class StringComparator implements Comparator<String>{
    @Override
    public int compare(String o1, String o2) {
        String c1 = o1 + o2;
        String c2 = o2 + o1;
        return c1.compareTo(c2);
        //return 0;
    }
}
