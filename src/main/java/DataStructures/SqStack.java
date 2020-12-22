package DataStructures;

import java.util.Scanner;


/**
 * 顺序栈的示例，使用数组实现
 * 自动扩容，使用了泛型
 * @author danielzhang
 *
 */
public class SqStack<Item> {

//	main函数，测试用例
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入栈的初始容量：");
		int cap = scan.nextInt();
//		存贮String的栈	
		System.out.println("存储String的栈示例：");
//		注意泛型栈的使用
		SqStack<String> stackStr = new SqStack<String>(cap);
		System.out.println("请输入字符串(eof结束):");
		while(!scan.hasNext("eof")) {
			stackStr.Push(scan.next());
		}
		scan.close();
		System.out.println("栈的大小为：" + stackStr.Size());
		
//		System.out.println(stackStr.Item<String>[stackStr.top]);
		
		System.out.println("出栈过程：");
		while(!stackStr.isEmpty()) {
			System.out.println(stackStr.Pop());
		}
	}
	
//	栈的内容，使用泛型数组，但是要注意，实际上Java是不支持泛型数组的，这里只是申明
	private Item[] stack;
//	栈顶指针
	private int top;
//	栈的最大容量
	private int capacity;
	
//	构造函数
	@SuppressWarnings("unchecked")
	public SqStack(int cap) {
//	对于泛型数组不能直接构造，只能先构造Object的数组，然后进行类型转换
		this.stack = (Item[]) new Object[cap];
		this.top = -1;
		this.capacity = cap;
	}
	
//	检查栈是否为空
	public boolean isEmpty() {
		return this.top == -1;
	}
	
//	检查是否栈满
	public boolean isFull() {
		return this.top+1 == this.capacity;
	}
	
//	返回栈大小
	public int Size() {
		return this.top+1;
	}
	
//	扩大栈容量
	public void Resize(int add) {
		@SuppressWarnings("unchecked")
		Item[] temp = (Item[]) new Object[this.capacity+add];
		for(int i = 0;i<this.Size();i++) {
			temp[i]=this.stack[i];
		}
		this.stack = temp;
	}
	
//	入栈
	public void Push(Item item) {
		if(isFull()) {
			Resize(10);
			System.out.println("栈已扩容！");
			stack[++top]=item;
		}else {
			stack[++top]=item;
		}
	}
	
//	出栈
	public Item Pop(){
		if(isEmpty()) {
			System.out.println("栈为空");
			return null;
		}else {
			return stack[top--];
		}
	}

}
