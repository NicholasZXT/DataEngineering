package Coding.sword.offer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 复制复杂链表，在链表中，每个节点除了有一个next指针指向下一个节点外，还有一个pSibling指向链表中的任意节点或者null
 * ------ 这一题难度有，值得一看，方法很巧妙，也有难度 --------------
 */
public class Q26CopyComplexList {
    public static void main(String[] args){
        // 手动创建测试用例
        ComplexListNode node1 = new ComplexListNode(1);
        ComplexListNode node2 = new ComplexListNode(2);
        ComplexListNode node3 = new ComplexListNode(3);
        ComplexListNode node4 = new ComplexListNode(4);
        ComplexListNode node5 = new ComplexListNode(5);
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node5;
        node1.pSibling = node3;
        node2.pSibling = node5;
        node4.pSibling = node2;
        System.out.println("origin list: ");
        node1.show();
        System.out.println("cloneList: ");
        ComplexListNode copied_node = cloneList(node1);
        copied_node.show();
        System.out.println("cloneListWithMap: ");
        ComplexListNode copied_node_map = cloneListWithMap(node1);
        copied_node_map.show();
        System.out.println("cloneListFast: ");
        ComplexListNode copied_node_fast = cloneListFast(node1);
        node1.show();
        copied_node_fast.show();
    }

    // 更快的实现，时间复杂度 O(n) ———— 这个方法很巧妙，但是也比较难，特别考察链表节点的操作
    public static ComplexListNode cloneListFast(ComplexListNode old_nodes){
        ComplexListNode new_nodes = null;
        ComplexListNode new_tail = null;
        ComplexListNode mix_nodes = null;
        ComplexListNode mix_head = null;
        ComplexListNode old_tail = old_nodes;
        ComplexListNode temp;
        // 第1步，仍然是顺序复制节点，但是有一点不一样，这次是把新旧链表节点混在一起，新复制的节点跟在旧节点后面
        // 混合方式为 old1, new1, old2, new2, ...
        while (old_tail != null){
            if (mix_nodes == null){
                mix_nodes = old_tail;
                mix_head = mix_nodes;
            }else {
                mix_head.next = old_tail;
                mix_head = mix_head.next;
            }
            // 下面这个顺序很重要
            // 创建新节点，放到对应旧节点后面
            temp = new ComplexListNode(old_tail.value);
            old_tail = old_tail.next;
            mix_head.next = temp;
            mix_head = mix_head.next;
        }
        System.out.println("cloneListFast.step 1:");
        mix_nodes.show();
        // 第2步，在混合链表复制复杂链接
        mix_head = mix_nodes;
        while (mix_head != null){
            if (mix_head.pSibling != null){
                // 旧节点
                temp = mix_head;
                // 旧节点后面跟的对应新节点，复制链接
                temp.next.pSibling = temp.pSibling.next;
            }
            // 向后移动两个节点，到达下一个旧节点
            mix_head = mix_head.next.next;
        }
        System.out.println("cloneListFast.step 2:");
        mix_nodes.show();
        // 第3步，从混合链表中拆分新旧链表，
        // 注意，这一步不能和第2步混合在一起，因为后面的节点可能会引用前面的节点
        new_nodes = null;
        old_nodes = null;
        new_tail = null;
        old_tail = null;
        mix_head = mix_nodes;
        while (mix_head != null){
            if (old_nodes == null){
                old_nodes = mix_head;
                new_nodes = mix_head.next;
                mix_head = mix_head.next.next;
                old_tail = old_nodes;
                new_tail = new_nodes;
                old_tail.next = null;
                new_tail.next = null;
            }else {
                old_tail.next = mix_head;
                new_tail.next = mix_head.next;
                mix_head = mix_head.next.next;
                old_tail = old_tail.next;
                new_tail = new_tail.next;
                old_tail.next = null;
                new_tail.next = null;
            }
        }
        return new_nodes;
    }

    // 使用哈希表空间换时间，时间复杂度 O(n^2)
    public static ComplexListNode cloneListWithMap(ComplexListNode old_nodes){
        ComplexListNode new_nodes = null;
        ComplexListNode new_temp = new_nodes;
        ComplexListNode old_temp = old_nodes;
        Map<ComplexListNode, ComplexListNode> map = new HashMap<>();
        // 顺序复制链表每个节点，同时使用map记录新旧节点映射
        while (old_temp != null){
            if (new_nodes == null){
                new_nodes = new ComplexListNode(old_temp.value);
                new_temp = new_nodes;
                map.put(old_temp, new_temp);
            }else {
                new_temp.next = new ComplexListNode(old_temp.value);
                map.put(old_temp, new_temp);
                new_temp = new_temp.next;
            }
            old_temp = old_temp.next;
        }
        old_temp = old_nodes;
        new_temp = new_nodes;
        while (old_temp != null){
            if (old_temp.pSibling != null){
                new_temp.pSibling = map.get(old_temp);
            }
            old_temp = old_temp.next;
            new_temp = new_temp.next;
        }
        return new_nodes;
    }

    // 我自己的实现，时间复杂度 O(n^3)
    public static ComplexListNode cloneList(ComplexListNode old_nodes){
        ArrayList<ComplexListNode> old_list = new ArrayList<>();
        ArrayList<ComplexListNode> new_list = new ArrayList<>();
        ComplexListNode new_nodes = null;
        ComplexListNode new_temp = new_nodes;
        ComplexListNode old_temp = old_nodes;
        // 顺序复制链表每个节点
        while (old_temp != null){
            old_list.add(old_temp);
            if (new_nodes == null){
                new_nodes = new ComplexListNode(old_temp.value);
                new_temp = new_nodes;
            }else {
                new_temp.next = new ComplexListNode(old_temp.value);
                new_temp = new_temp.next;
            }
            new_list.add(new_temp);
            old_temp = old_temp.next;
        }
        // 复制每个节点的复杂链接
        int length = old_list.size();
        ComplexListNode old_node = null;
        for(int i = 0; i < length; i++){
            old_node = old_list.get(i);
            if (old_node.pSibling == null)
                continue;
            for(int j = 0; j < length; j++){
                if (old_node.pSibling == old_list.get(j)){
                    new_list.get(i).pSibling = new_list.get(j);
                }
            }
        }
        return new_nodes;
    }

    // 复杂链表的节点定义
    public static class ComplexListNode{
        int value;
        ComplexListNode next;
        ComplexListNode pSibling;
        public ComplexListNode(int value){
            this.value = value;
            this.next = null;
            this.pSibling = null;
        }
        public void show(){
            ComplexListNode temp = this;
            while (temp != null){
                if (temp.pSibling != null)
                    System.out.print(temp.value + "(->" + temp.pSibling.value + "), ");
                else
                    System.out.print(temp.value +  ", ");
                temp = temp.next;
            }
            System.out.println();
        }

    }
}
