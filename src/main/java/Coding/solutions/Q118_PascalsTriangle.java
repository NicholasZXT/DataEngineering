//Given a non-negative integer numRows, generate the first numRows of Pascal's t
//riangle. 
//
// 
//In Pascal's triangle, each number is the sum of the two numbers directly above
// it. 
//
// Example: 
//
// 
//Input: 5
//Output:
//[
//     [1],
//    [1,1],
//   [1,2,1],
//  [1,3,3,1],
// [1,4,6,4,1]
//]
// 
// Related Topics Array 
// 👍 1495 👎 110


package Coding.solutions;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Q118_PascalsTriangle{
  public static void main(String[] args) {
       Solution solution = new Q118_PascalsTriangle().new Solution();
       int numRows = 5;

      List<List<Integer>> result = solution.generate(numRows);
      for (List<Integer> list: result){
          System.out.println(Arrays.toString(list.toArray()));
      }
  }
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> list = new ArrayList<>();
        if(numRows <= 0) return list;
        List<Integer> sublist = Arrays.asList(1);
        list.add(sublist);
        List<Integer> prelist;
        for(int i =1; i < numRows; i++){
            prelist = list.get(i-1);
            int prelist_size = prelist.size();
            sublist = new ArrayList<>(prelist_size + 1);
            for (int j = 0; j < prelist_size+1; j++){
                if(j == 0) sublist.add(prelist.get(j));
                if(j == prelist_size) sublist.add(prelist.get(j-1));
                if(j > 0 & j < prelist_size) sublist.add( prelist.get(j-1) + prelist.get(j) );
            }
            list.add(sublist);
        }
        return list;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

}