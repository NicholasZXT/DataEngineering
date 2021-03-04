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


package LeetCode.solutions;
public class Q21_MergeTwoSortedLists{
  public static void main(String[] args) {
       Solution solution = new Q21_MergeTwoSortedLists().new Solution();
       ListNode l1 = new ListNode(1, new ListNode(2, new ListNode(4)));
       ListNode l2 = new ListNode(1, new ListNode(3, new ListNode(4)));
       Q21_MergeTwoSortedLists.listPrint(l1);
       Q21_MergeTwoSortedLists.listPrint(l2);

       ListNode l3 = solution.mergeTwoLists(l1,l2);

       Q21_MergeTwoSortedLists.listPrint(l3);
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
//        先初始化合并后的头结点，并将其指向next置为null
//        这个头结点的val是始终是空的
        ListNode listNode = new ListNode() ;
        listNode.next = null;
//        pointer用于指向listNode的最后一个节点
        ListNode pointer = listNode;
        while(l1 != null && l2 != null){
            if( l1.val <= l2.val){
                pointer.next = l1;
                pointer = pointer.next;
                l1 = l1.next;
            }else {
                pointer.next = l2;
                pointer = pointer.next;
                l2 = l2.next;
            }
        }
        if(l1 == null){ pointer.next = l2; }
        if(l2 == null){ pointer.next = l1; }
//        为了避免头结点里的val为空，所以往后移动一个
        listNode = listNode.next;
        return listNode;
    }
}
//leetcode submit region end(Prohibit modification and deletion)

   static class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }

    public static void listPrint(ListNode ls){
        while(ls != null){
            System.out.print(ls.val + "  ");
            ls = ls.next;
        }
        System.out.println();
    }
}