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
//		！！！！！在静态的main方法中使用内部类Node时，需要带上外部类的名称！！！！！
//		由于linkNode作为头结点，里面的data域没有值，所以要从第一个节点开始
		LinkList.Node node = linkList.linkNode.next;
		while (node!=null){
			System.out.println(node.data);
			node = node.next;
		}
//		使用for循环遍历时不太好，因为不好处理第一个元素
//		for(LinkList.Node node = linkList.linkNode.next; node != null; node = node.next){
//			System.out.println(node.data);
//		}

//		根据值查找元素


	}

//	定义表示节点的类——内部类
	private class Node{
//		内部类既可以访问自身的数据域，也可以访问创建它的外围类对象的数据域
//		内部类的变量可以不使用修饰符
		Item data;
		Node next;
	}


//	LinkNode的实例域
	private Node linkNode;
	
//	LinkNode类的构造函数，也就是创建一个单链表的头结点
//	这个头结点的data域是没有值的
	private LinkList(){
		this.linkNode = new Node();
	}


//	头插法逆序建立单链表
//	每次插入一个元素，这样插入的时候，linkNode这个头结点里的data域是没有值的
//	所以在遍历的时候，linkNode头结点要跳过，否则会输出null
	private void createReverseLinkList(Item data){
		Node temp = new Node();
		temp.data = data;
//		插入
		temp.next = this.linkNode.next;
		this.linkNode.next = temp;
	}
	
//	根据值的查找（第一个）节点的位置
	public Node searchByData(Item data) {
		Node temp = this.linkNode;
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
		Node temp = linkNode;
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
		Node last = linkNode;
		while(last.next!=null) {
			last = last.next;
		}
		last.next = temp;
	}
	
//	
	
}

