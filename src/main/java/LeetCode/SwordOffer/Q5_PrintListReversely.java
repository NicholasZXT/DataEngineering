package LeetCode.SwordOffer;

import scala.Int;

public class Q5_PrintListReversely<Item> {
    public static void main(String[] args){
        Integer[] array = {1, 2, 3, 4, 5};
        Q5_PrintListReversely<Integer> solver = new Q5_PrintListReversely<Integer>();
        Q5_PrintListReversely<Integer>.Node head = solver.createList(array);
        solver.showList(head);
        solver.reversePrint(head);
    }

    public void reversePrint(Node head){
        // 递归的方式，逆序打印列表
        Node p = head.next;
        if(p != null){
            // 注意，这里不能再使用 p.next 了
            reversePrint(p);
            System.out.println(p.data);
        }
    }

    private class Node{
        Item data;
        Node next;
        public Node(Item data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    // 从数组创建一个链表
    public Node createList(Item[] array){
        Node head = new Node(null, null);
        Node p = head;
        for(Item data: array){
            Node node = new Node(data, null);
            p.next = node;
            p = node;
        }
        return head;
    }

    public void showList(Node head){
        Node p = head;
        while(p.next != null){
            p = p.next;
            System.out.print(p.data + ",");
        }
    }


}
