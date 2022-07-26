//Given a non-negative index k where k ≤ 33, return the kth index row of the Pas
//cal's triangle. 
//
// Note that the row index starts from 0. 
//
// 
//In Pascal's triangle, each number is the sum of the two numbers directly above
// it. 
//
// Example: 
//
// 
//Input: 3
//Output: [1,3,3,1]
// 
//
// Follow up: 
//
// Could you optimize your algorithm to use only O(k) extra space? 
// Related Topics Array 
// 👍 831 👎 197


package Coding.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


/**
 * 这道题解决不难，但是解决的漂亮有点难度，需要注意数组index的更新过程
 */
public class Q119_PascalsTriangleIi{
  public static void main(String[] args) {
       Solution solution = new Q119_PascalsTriangleIi().new Solution();
      int rowIndex = 3;
      rowIndex = 4;
      List<Integer> result = solution.getRow(rowIndex);
      System.out.println(Arrays.toString(result.toArray()));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

    public List<Integer> getRow(int rowIndex) {
        List<Integer> list = new ArrayList<Integer>();
        if (rowIndex < 0)  return list;

        for (int i = 0; i < rowIndex + 1; i++) {
            list.add(0, 1);
            for (int j = 1; j < list.size() - 1; j++) {
                list.set(j, list.get(j) + list.get(j + 1));
            }
        }
        return list;
    }

}
//leetcode submit region end(Prohibit modification and deletion)

//    这是我未能成功的解法，考虑的想法是每次从头(实际是从 index=1 开始)到尾，相邻两个迭代计算当前的位置的值，
//    也就是 a[j] = a[j-1] + a[j]，到达末尾时，末尾在加上最后一个元素1。
//    这样的问题是，下一层使用a[j]时，应该使用的是未被更新前的a[j]，所以需要保存更新前a[j]的值，
//    但是这个保存的过程很困难，特别是要考虑第一行和第二行的边界条件。
//    上面的成功的解法是，每次一开始就在list最前面加上一个1，然后使用a[j] = a[j] + a[j+1]的方式更新，这样就不用保存a[j]了，
//    因为更新之后 a[j] 就不再被使用了。
    public List<Integer> getRow(int rowIndex) {
        List<Integer> list  = new ArrayList<>();
        if( rowIndex < 0 ) return list;
//      第一行的值
        list.add(1);
        int temp;
//      for循环直接从第二行开始搞
        for(int i = 1; i <= rowIndex; i++){
            temp = i == 1 ? list.get(0) : list.get(1);
            for(int j = 1; j <= list.size(); j++){
                if(j == list.size()) {list.add(1);break;}
                if(j == 1) {list.set(j, list.get(j-1) + list.get(j)); break;}
                temp = list.get(j);
                list.set(j, temp + list.get(j));
            }
        }
        return list;
    }


}