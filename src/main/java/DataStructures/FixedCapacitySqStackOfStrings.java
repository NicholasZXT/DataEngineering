package DataStructures;

import java.util.Scanner;
/**
 * 这个是顺序栈的示例，采用数组作为实现方式，
 * 没有使用泛型，栈的内容只能存放String类型数据.
 * 容量固定为N，
 * 没有继承接口Iterable，
 * @author danielzhang
 *
 */


public class FixedCapacitySqStackOfStrings {
	
//	main函数,用于测试这个栈
	public static void main (String[] args) {
		Scanner scan = new Scanner(System.in);
		String s = new String();
		System.out.println("请输入栈的容量：");
		int N = scan.nextInt();
		
		FixedCapacitySqStackOfStrings stack = new FixedCapacitySqStackOfStrings(N);
		System.out.println("请输入不超过"+ stack.capacity + "个字符串(eof结束)：");
		while(!scan.hasNext("eof")) {
			s = scan.next();
//			System.out.println(s);
			stack.Push(s);
		}
		scan.close();
		
		System.out.println("栈的大小为：" + stack.Size());
		System.out.println("出栈过程:");
		while(!stack.isEmpty()) {
			System.out.println(stack.Pop());
		}
		
	}
	
	
////////// Class栈的实例变量与方法
//	存贮栈的内容
	private String[] stack;
//	栈的固定容量
	private int capacity;
	
//	栈顶指针
//	栈顶指针指示的是栈尾元素所在位置，此时要特别注意的是，栈空时，栈顶指针为-1，而不是0
//	private int top;
//	栈的当前大小
//	使用栈的大小size来指示栈尾元素时，由于下标从0开始，栈尾元素实际位于 size-1 的位置，而不是size指定的位置
	private int size;

//	含参构造器
	public FixedCapacitySqStackOfStrings(int capacity) {
		this.stack = new String[capacity];
//		this.top = -1;
		this.size = 0;
		this.capacity=capacity;
	}
	
//	检查栈是否为空
	public boolean isEmpty() {
//		return this.top == -1;	
		return this.size == 0;
	}
	
//	返回栈的大小
	public int Size() {
//		return this.top+1;
		return this.size;
	}
	
//	入栈
	public void Push(String s) {
//		if(this.top+1 == this.capacity) {
//			System.out.println("栈已满");
//		}else {
//			stack[++this.top] = s;
//		}
		
		if(this.size == this.capacity) {
			System.out.println("栈已满");
		}else {
			stack[this.size++] = s;
		}
	}
	
//	出栈
	public String Pop() {
		if(this.isEmpty()) {
			System.out.println("栈为空");
			return null;
		}else {
//			String s = stack[this.top--];
			String s = stack[--this.size];
			return s;
		}
	}
}
