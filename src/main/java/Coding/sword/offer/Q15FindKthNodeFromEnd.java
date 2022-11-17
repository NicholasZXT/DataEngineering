package Coding.sword.offer;

public class Q15FindKthNodeFromEnd {
    public static void main(String[] args) {
        // 测试链表
        //Integer[] array = {1,2,3,4,5};
        Integer[] array = {1};
        LinkList<Integer> list = new LinkList<>();
        list.CreateLinkListFromArray(array);
        list.show();
        // 测试
        Q15FindKthNodeFromEnd solver = new Q15FindKthNodeFromEnd();
        solver.findKthToTail(list, 3);
        solver.findKthToTail(list, 0);
        solver.findKthToTail(list, 1);
        solver.findKthToTail(list, 5);
        solver.findKthToTail(list, 6);
    }

    /**
     * 输入一个链表，返回这个链表的倒数第K个节点
     */
    public void findKthToTail(LinkList<Integer> list, int k){
        Node<Integer> p1 = list.head;
        Node<Integer> p2 = list.head;
        if (k <= 0){
            System.out.println("invalid index: " + k);
            return;
        }
        int distance = k-1;
        while (p1.next !=null){
            p1 = p1.next;
            if (distance <= 0){
                p2 = p2.next;
            }else {
                distance -= 1;
            }
        }
        if (distance > 0){
            System.out.println("not found '" + k + "' element from tail.");
        }else {
            System.out.println("found value of '" + k + "' from tail: " + p2.value);
        }
    }
}
