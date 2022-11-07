package Coding.sword.offer;

public class Q17MergeSortedList<T extends Comparable> {
    public static void main(String[] args) {
        // 创建链表
        //Integer[] array1 = {1,2,5,9,15};
        //Integer[] array1 = {};
        //Integer[] array2 = {1,4,6,8,12};
        //Integer[] array2 = {};
        Integer[] array1 = {1,2,3,4};
        Integer[] array2 = {6,7,8};
        LinkList<Integer> list1 = new LinkList<Integer>();
        LinkList<Integer> list2 = new LinkList<Integer>();
        list1.createListFromArray(array1);
        list2.createListFromArray(array2);
        list1.show();
        list2.show();
        // 测试
        Q17MergeSortedList<Integer> solver = new Q17MergeSortedList<>();
        LinkList<Integer> merge_list;
        merge_list = solver.mergeSortedList(list1, list2);
        merge_list.show();
    }

    /**
     * 合并两个递增排序的链表
     */
    public LinkList<T> mergeSortedList(LinkList<T> list1, LinkList<T> list2){
        /**
         * 非递归方式
         */
        LinkList<T> merged_list = new LinkList<>();
        Node<T> head1 = list1.head.next;
        Node<T> head2 = list2.head.next;
        Node<T> current = merged_list.head;
        while( head1 != null & head2 != null){
            if(head1.value.compareTo(head2.value) <= 0){
                current.next = head1;
                head1 = head1.next;
            }else {
                current.next = head2;
                head2 = head2.next;
            }
            current = current.next;
        }
        if(head1 != null){
            current.next = head1;
        }
        if(head2 != null){
            current.next = head2;
        }
        return merged_list;
    }

    // 这里用内部类只是为了封装 Node，而 Node 本身并不需要访问外部类的成员变量，所以可以定义为静态的
    public static class Node<T>{
        // 这里没有使用修饰符，是默认访问控制权限——对同一包内可见
        Node<T> next;
        T value;
        public Node(Node<T>next, T value){
            this.next = next;
            this.value = value;
        }
    }

    // 带头结点的链表实现类
    public static class LinkList<T>{
        public Node<T> head;
        public LinkList(){
            this.head = new Node<T>(null, null);
        }

        public void createListFromArray(T[] array){
            Node<T> p = this.head;
            Node<T> temp;
            for (T value: array){
                temp = new Node(null, value);
                p.next = temp;
                p = p.next;
            }
        }

        public void show(){
            Node<T> head = this.head.next;
            while (head != null){
                System.out.print(head.value + ", ");
                head = head.next;
            }
            System.out.println();
        }
    }

}
