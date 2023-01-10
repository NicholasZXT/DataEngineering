package Coding.sword.offer;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 在字符串中找出第一个只出现一次的字符。
 * 比如输入 “abaccdeff”，返回 "b"
 * -----------------------------------
 * 这一题如果能使用Java里的 有序map 就很好解决，但是如果按照题解使用 C 语言的话，难度就上升了不少
 */
public class Q35_FirstNotRepeatingChar {
    public static void main(String[] args) {
        Q35_FirstNotRepeatingChar solver = new Q35_FirstNotRepeatingChar();
        //String string = "abaccdeff";
        //String string = "abbacdeff";
        String string = "abbaff";
        System.out.println("first not repeating char of string '" + string + "' is: " + solver.findFirstNotRepeatingChar(string));
    }

    public char findFirstNotRepeatingChar(String strings){
        char[] char_array = strings.toCharArray();
        Map<Character, Integer> map = new LinkedHashMap<>();
        for(char item: char_array){
            if(map.containsKey(item)){
                map.remove(item);
            }else {
                map.put(item, 1);
            }
        }
        if (map.isEmpty())
            return '\0';
        else {
            // 这里的使用要特别注意
            Character[] chars = map.keySet().toArray(new Character[0]);
            return chars[0];
        }
    }

}
