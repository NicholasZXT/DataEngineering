//Given a string containing just the characters '(', ')', '{', '}', '[' and ']',
// determine if the input string is valid. 
//
// An input string is valid if: 
//
// 
// Open brackets must be closed by the same type of brackets. 
// Open brackets must be closed in the correct order. 
// 
//
// Note that an empty string is also considered valid. 
//
// Example 1: 
//
// 
//Input: "()"
//Output: true
// 
//
// Example 2: 
//
// 
//Input: "()[]{}"
//Output: true
// 
//
// Example 3: 
//
// 
//Input: "(]"
//Output: false
// 
//
// Example 4: 
//
// 
//Input: "([)]"
//Output: false
// 
//
// Example 5: 
//
// 
//Input: "{[]}"
//Output: true
// 
// Related Topics String Stack 
// 👍 5035 👎 218


package LeetCode.leetcode.editor.en;

import java.util.HashMap;
import java.util.Stack;

public class Q20_ValidParentheses{
  public static void main(String[] args) {
       Solution solution = new Q20_ValidParentheses().new Solution();
       solution.isValid("()");
       solution.isValid("()[]{}");
       solution.isValid("(]");
       solution.isValid("([)]");
       solution.isValid("{[]}");
       solution.isValid("]}");
       solution.isValid("((()(())))");
  }

  //leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isValid(String s) {
        System.out.println("括号字符串为: " + s);
        Stack<Character> stack = new Stack<Character>();
        HashMap<Character,Character> pair = new HashMap<Character, Character>();
        pair.put(')', '(');
        pair.put('}','{');
        pair.put(']','[');
        char temp ;
        for(char c:s.toCharArray()){
//            System.out.println(c);
            if (c == '(' || c == '[' || c == '{') stack.push(c);
            if (c == ')' || c == ']' || c == '}') {
                if(stack.size() > 0){
                    temp = stack.pop();
                }else {
                    System.out.println("false");
                    return false;
                }
//                System.out.println(temp);
                if ( pair.get(c) != temp){
                    System.out.println("false");
                    return false;
                }
            }
        }
        if( stack.size() == 0) {
            System.out.println("true");
            return true;
        }
        else {
            System.out.println("false");
            return false;
        }

    }
}
//leetcode submit region end(Prohibit modification and deletion)

}