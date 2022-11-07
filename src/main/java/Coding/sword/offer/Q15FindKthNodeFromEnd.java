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

/*
 * 下面两个类的可见性是在 package 范围内，也就是说当前package下的其他文件也可以访问这两个类，并且不能再重复定义了
 */

// 链表的节点定义
class Node<T> {
    Node<T> next;
    T value;
    public Node(Node<T> next, T value){
        this.next = next;
        this.value = value;
    }
}

// 链表定义
class LinkList<T> {
    public Node<T> head;
    public LinkList(){
        // 链表头结点
        this.head = new Node<T>(null, null);
    }

    public void CreateLinkListFromArray(T[] array){
        Node<T> current = this.head;
        for (T value: array){
            System.out.println("add value: " + value);
            Node<T> node = new Node<T>(null, value);
            current.next = node;
            current = node;
        }
    }

    public void show(){
        Node<T> current = this.head;
        while (current.next != null){
            System.out.print(current.value + ", ");
            current = current.next;
        }
        System.out.println(current.value);
    }
}