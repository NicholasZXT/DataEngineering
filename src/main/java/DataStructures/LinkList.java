package DataStructures;

import java.util.Scanner;


/**
 * 单链表的示例
 * 使用了泛型
 * 没有实现Iterable接口
 * @author danielzhang
 *
 */
public class LinkList<Item> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		初始化一个链表
		LinkList<String> linkList = new LinkList<String>();
//		开始输入
		Scanner scan = new Scanner(System.in);
		System.out.println("请逐个输入链表元素(eof为结束符):");
		while(!scan.hasNext("eof")){
			linkList.createReverseLinkList(scan.next());
		}

//		输出创建的链表
		System.out.println("遍历链表：");
		for(Node node = linkList.linkList; node != null; node = node.next){
			System.out.println(node.data);
		}

	}

//	定义表示节点的类——内部类
	private class Node{
//		内部类既可以访问自身的数据域，也可以访问创建它的外围类对象的数据域
//		内部类的变量可以不使用修饰符
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
//	每次插入一个元素
	public void createReverseLinkList(Item data){
		Node temp = new Node();
		temp.data = data;
//		插入
		temp.next = this.linkList.next;
		this.linkList.next = temp;
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
	public void addEndNode(Item data) {
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

