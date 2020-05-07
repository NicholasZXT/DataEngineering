package DataStructures;

import java.util.Scanner;


/**
 * 单链表的示例
 * 使用了泛型
 * @author danielzhang
 *
 */
public class LinkList<Item> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private class Node{
		Item data;
		Node next;
	}
//	LinkNode的实例域
	Node linkList;
	
//	LinkNode类的构造函数，也就是创建一个单链表的头结点
	public LinkList(){
		this.linkList = new Node();
	}
	
//	头插法逆序建立单链表
	public void createReverseLinkList(){
		
	}
	
//	根据值的查找（第一个）节点的位置
	public Node searchByData(Item data) {
		Node temp = linkList;
		while(temp.next!=null) {
			if(temp.data!=data)
				temp = temp.next;
			else
				break;
		}
		return temp;
	}
	
	

	
//	根据位置查找节点内容
	public Item searchByLocation(int index) {
		Node temp = linkList;
		int i = 0;
		while(temp.next != null) {
			if(i < index) {
				temp = temp.next;
				++i;
			}
			else
				return temp.data;	
		}
		return null;	
	}
	
	
//	在链表尾部插入一个节点
	public void addNode(Item data) {
		Node temp = new Node();
		temp.data = data;
		Node last = linkList;
		while(last.next!=null) {
			last = last.next;
		}
		last.next = temp;
	}
	
//	
	
}

