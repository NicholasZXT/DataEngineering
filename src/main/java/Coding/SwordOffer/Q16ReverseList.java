package Coding.SwordOffer;


/**
 * 反转链表
 * 输入一个链表的头结点，反转该链表，并输出反转后链表的头结点
 */
public class Q16ReverseList<T> {
    public static void main(String[] args) {
        //创建列表
        //Integer[] array = {1,2,3,4,5};
        Integer[] array = {};
        LinkList<Integer> list = new LinkList<>();
        list.CreateLinkListFromArray(array);
        list.show();
        // 反转链表
        Q16ReverseList<Integer> solver = new Q16ReverseList<>();
        solver.ReverseList(list);
        list.show();

    }

    public void ReverseList(LinkList<T> list){
        /**
         * 直接使用链表的头插法进行反转
         */
        Node<T> root = new Node<T>(null, null);
        Node<T> temp = new Node<T>(null, null);
        if (list.head.next == null) return;
        root = list.head.next;
        list.head.next = null;
        while (root.next != null){
            temp = root;
            root = root.next;
            temp.next = list.head.next;
            list.head.next = temp;
        }
        root.next = list.head.next;
        list.head.next = root;
    }
}
