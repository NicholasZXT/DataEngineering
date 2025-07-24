package Algorithms.DataStructures;

import java.util.Scanner;
/**
 * 
 */

/**
 * 顺序栈的示例，采用数组作为实现方式
 * 使用了泛型，
 * 固定容量为N,
 * 没有继承接口Iterable
 * @author danielzhang
 *
 */
public class FixedCapacitySqStack<Item> {

//	main函数，测试用例
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
//		IDEA里的快捷键为sout
		System.out.println("请输入栈的容量：");
		int cap = scan.nextInt();
		
//		存贮String的栈	
		System.out.println("存储String的栈示例：");
//		注意泛型栈的使用
		FixedCapacitySqStack<String> stackStr = new FixedCapacitySqStack<String>(cap);
		System.out.println("请输入不超过" + stackStr.capacity + "个字符串(eof结束):");
		while(!scan.hasNext("eof")) {
			stackStr.Push(scan.next());
		}
		System.out.println("栈的大小为：" + stackStr.Size());
		System.out.println("出栈过程：");
		while(!stackStr.isEmpty()) {
			System.out.println(stackStr.Pop());
		}
		
//		存储整数的栈
		System.out.println("存储整数的栈示例：");
		FixedCapacitySqStack<Integer> stackInt = new FixedCapacitySqStack<Integer>(cap);
		System.out.println("请输入不超过" + stackInt.capacity + "个整数(eof结束):");
		scan.next(); //这一句是为了跳过之前收到的那个eof
		while(!scan.hasNext("eof")) {
			stackInt.Push(scan.nextInt());
		}
		System.out.println("栈的大小为：" + stackInt.Size());
		System.out.println("出栈过程：");
		while(!stackInt.isEmpty()) {
			System.out.println(stackInt.Pop());
		}
		
		scan.close();

	}

	
///////////Class 栈的实例变量与方法
//	栈的内容，使用泛型数组，但是要注意，实际上Java是不支持泛型数组的，这里只是申明
	private Item[] stack;
//	栈顶指针，它既用于表示栈顶元素位置，也用于表示栈的容量
	private int top;
//	栈的最大容量
	private int capacity;
	
//	构造函数
	@SuppressWarnings("unchecked")
	public FixedCapacitySqStack(int cap) {
//		对于泛型数组不能直接构造，只能先构造Object的数组，然后进行类型转换
		this.stack = (Item []) new Object[cap];
//		-1 表示栈空
		this.top = -1;
		this.capacity = cap;
	}
	
//	检查栈是否为空
	public boolean isEmpty() {
		return this.top == -1;
	}
	
//	返回栈大小
	public int Size() {
		return this.top+1;
	}
	
//	入栈
	public void Push(Item item) {
		if(this.top+1 == this.capacity) {
			System.out.println("栈已满！");
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
