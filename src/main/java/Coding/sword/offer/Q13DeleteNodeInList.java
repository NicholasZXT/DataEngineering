package Coding.sword.offer;

public class Q13DeleteNodeInList {
    public static void main(String[] args) {
        // 测试用例
        int[] a1 = {1,2,3,4,5};
        int[] a2 = {};
        int[] a3 = {10};
        Node pDelete = null;

        Q13DeleteNodeInList solver = new Q13DeleteNodeInList();
        solver.createListFromArray(a1);
        solver.show();
        pDelete = solver.root.next.next.next;
        solver.deleteNode(solver.root, pDelete);
        solver.show();

        solver.createListFromArray(a2);
        solver.show();
        pDelete = null;
        solver.deleteNode(solver.root, pDelete);
        solver.show();

        solver.createListFromArray(a3);
        solver.show();
        pDelete = solver.root.next;
        solver.deleteNode(solver.root, pDelete);
        solver.show();

    }

    /**
     * 从链表中以 O(1) 复杂度删除一个节点
     * @param head
     * @param pDeleted
     */
    public void deleteNode(Node head, Node pDeleted){
        if(head == null || pDeleted == null){
            System.out.println("noting to delete");
            return;
        }
        Node q = null;
        // 链表中有多个节点，p 是链表中间的节点
        if(pDeleted.next != null){
            // 使用下个节点的值覆盖当前节点，然后删除下个节点即可
            q = pDeleted.next;
            pDeleted.value = q.value;
            pDeleted.next = q.next;
            q = null;
        }else {
            // 要删除的是链表的尾部节点
            // 这里使用的是带头结点的链表，所以这里比较简单 ---- KEY
            q = head;
            while (q.next != pDeleted){
                q = q.next;
            }
            q.next = null;
            pDeleted = null;
        }

    }


    // 链表结构定义及操作
    private class Node{
        Node next;
        int value;
        public Node(){
            this.next = null;
            this.value = -1;
        }
    }
    public Node root;

    public void createListFromArray(int[] array){
        // 这里创建的是带头结点的链表
        this.root = new Node();
        if(array.length <=0)
            return;
        Node p = this.root;
        for(int num:array){
            Node newNode = new Node();
            newNode.value = num;
            p.next = newNode;
            p = p.next;
        }
    }

    public void show(){
        if(this.root == null || this.root.next == null){
            System.out.println("empty list");
            return;
        }
        Node p = this.root.next;
        while (p != null){
            System.out.print(p.value + " ");
            p = p.next;
        }
        System.out.println();
    }

}
