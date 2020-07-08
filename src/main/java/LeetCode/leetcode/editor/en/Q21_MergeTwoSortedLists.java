//Merge two sorted linked lists and return it as a new sorted list. The new list
// should be made by splicing together the nodes of the first two lists. 
//
// Example: 
//
// 
//Input: 1->2->4, 1->3->4
//Output: 1->1->2->3->4->4
// 
// Related Topics Linked List 
// 👍 4251 👎 593


package LeetCode.leetcode.editor.en;
public class Q21_MergeTwoSortedLists{
  public static void main(String[] args) {
       Solution solution = new Q21_MergeTwoSortedLists().new Solution();
  }
  //leetcode submit region begin(Prohibit modification and deletion)
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode listNode = new ListNode();
        ListNode t = listNode;
        while(l1 != null && l2 != null){
            if( l1.val <= l2.val){
                t.val = l1.val;
                t = t.next;
                l1 = l1.next;
            }else {
                t.val = l2.val;
                t = t.next;
                l2 = l2.next;
            }
        }


        return listNode;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

  public class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

}