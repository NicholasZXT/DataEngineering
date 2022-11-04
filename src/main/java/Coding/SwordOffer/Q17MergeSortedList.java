package Coding.SwordOffer;

/**
 * 合并两个递增排序的链表
 */
public class Q17MergeSortedList<T extends Comparable> {
    public static void main(String[] args) {
        Q17MergeSortedList<Integer> solver = new Q17MergeSortedList<>();
        // 创建链表
        Integer[] array1 = {1,2,5,9,15};
        Integer[] array2 = {1,4,6,8,12};
        Node<Integer> list1 = new Node<Integer>(null, null);
        Node<Integer> list2 = new Node<Integer>(null, null);
        //list1.CreateLinkListFromArray(array1);
        //list2.CreateLinkListFromArray(array2);
        //list1.show();
        //list2.show();
        // 测试
        Node<Integer> merge_list;
        merge_list = solver.MergeSortedList(list1, list2);
        //merge_list.show();
    }

    public Node<T> MergeSortedList(Node<T> list1, Node<T> list2){
        Node<T> merged_list = new Node<>(null, null);
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

}
