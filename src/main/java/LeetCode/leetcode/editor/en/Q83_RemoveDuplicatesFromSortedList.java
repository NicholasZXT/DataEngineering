//Given a sorted linked list, delete all duplicates such that each element appea
//r only once. 
//
// Example 1: 
//
// 
//Input: 1->1->2
//Output: 1->2
// 
//
// Example 2: 
//
// 
//Input: 1->1->2->3->3
//Output: 1->2->3
// 
// Related Topics Linked List 
// 👍 1603 👎 112


package LeetCode.leetcode.editor.en;
public class Q83_RemoveDuplicatesFromSortedList{
  public static void main(String[] args) {
       Solution solution = new Q83_RemoveDuplicatesFromSortedList().new Solution();
//       测试案例一
//       ListNode head = new ListNode(1); head.next = new ListNode(1); head.next.next = new ListNode(2,null);
//       测试案例二, 1,1,2,3,3
      ListNode head = new ListNode(1); head.next = new ListNode(1); head.next.next = new ListNode(2,null);
      head.next.next.next = new ListNode(3); head.next.next.next.next = new ListNode(3,null);
      PrintList(head);


      System.out.println("result is :");
      PrintList(solution.deleteDuplicates(head));
  }

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode pre = head, cur = head.next;
        if(pre == null | cur == null) return pre;

        while (cur.next != null){
            if( cur.val == pre.val){
                pre.next = cur.next;
                pre = pre.next;
                cur = pre.next;
            }else {
                pre = pre.next;
                cur = cur.next;
            }
        }
//        处理末尾相等的
        if( pre.val == cur.val) pre.next = null;

        return head;
    }
}
//leetcode submit region end(Prohibit modification and deletion)


//  Definition for singly-linked list.
  static class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

  public static void PrintList(ListNode head){
      ListNode node = head;
      if( node == null) {
          System.out.println("null");
          return;
      }
      while (node != null){
          System.out.print(node.val + ", ");
          node = node.next;
      }
      System.out.println();
  }


}