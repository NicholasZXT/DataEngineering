package Coding.sword.offer;

import java.util.Arrays;

/*
请实现一个函数，把字符串（以char数组的形式给出）中的每个空格替换成%20，注意是就地替换，假设字符串数组大小始终满足。
例如输入 "We are happy"，替换后为 "We%20are%20happy"
 */
public class Q4ReplaceBlank {
    public static void main(String[] args){
        //char[] str = {'W', 'e', ' ', 'a', 'r', 'e', ' ', 'h', 'a', 'p', 'p', 'y'};
        char[] str_arr = new char[30];
        String strings = "We are happy.";
        for (int i=0; i<strings.length(); i++) str_arr[i] = strings.charAt(i);
        System.out.println("origin char array:");
        System.out.println(Arrays.toString(str_arr));
        //System.out.println( '\u0000');
        Q4ReplaceBlank solver = new Q4ReplaceBlank();
        solver.replace(str_arr);
        System.out.println("replaced char array:");
        System.out.println(Arrays.toString(str_arr));
    }

    public void replace(char[] str_arr){
        char[] char_replace = {'%', '2', '0'};
        int str_len = 0;
        int add_len = 0;
        for (char c : str_arr) {
            // 注意，这里每遇到一个空格，长度是 +2，而不是 +3，因为空格本身还占有一个位置
            if (c == ' ') add_len += 2;
            // char数组的默认值
            if (c == '\u0000'){
                break;
            }else {
                str_len += 1;
            }
        }
        System.out.println("str_len: " + str_len + ", add_len: " + add_len);
        // 注意这里 i 和 j 的初始值
        int i = str_len - 1;
        int j = str_len + add_len - 1;
        while (i>=0){
            if (str_arr[i] != ' '){
                str_arr[j--] = str_arr[i--];
            }else {
                for (int k=char_replace.length-1;  k>=0; k--){
                    str_arr[j--] = char_replace[k];
                }
                i--;
            }
        }
    }
}
