package DataStructures;

import java.util.Iterator;
import java.util.Scanner;

/**
 * 
 */

/**
 * 顺序栈的示例，使用数组实现
 * 自动扩容，使用了泛型
 * 继承了Iterable接口，可以被for-each调用
 * @author danielzhang
 *
 */
public class IterableSqStack<Item> implements Iterable<Item> {
//	注意，这里的类声明里，自定义类名IterableStack和接口名称Iterable后面都跟上了泛型<Item>

//	main函数，测试用例
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入栈的初始容量：");
		int cap = scan.nextInt();
//		存贮String的栈	
		System.out.println("存储String的栈示例：");
//		注意泛型栈的使用
		IterableSqStack<String> stackStr = new IterableSqStack<String>(cap);
		System.out.println("请输入字符串(eof结束):");
		while(!scan.hasNext("eof")) {
			stackStr.Push(scan.next());
		}
		scan.close();
		System.out.println("栈的大小为：" + stackStr.Size());
		
//		测试是否实现Iterable接口,注意，测试之前不能执行出栈过程，否则栈中就什么都没有了
		System.out.println("测试Iterable：");
		for(String item : stackStr) {
			System.out.println(item);
		}
		
//		测试完Iterable之后才能演示所有元素出栈过程
		System.out.println("出栈过程：");
		while(!stackStr.isEmpty()) {
			System.out.println(stackStr.Pop());
		}
	}
	
//	Iterable接口的实现
//	这里的Iteration顺序就是出栈顺序
//	这个iterator方法不是泛型方法，它只是返回了一个实现了Iterator接口的对象而已
	public Iterator<Item> iterator() {
//		返回一个Itr类的对象，这个对象是实现了Iterator接口的类对象
//		这个Itr对象记录了当前实例变量的数据状态，之后的遍历就在这个Itr对象中进行，而不影响原实例变量的内容
		return new Itr();
	}
//	以内部类的形式实现Itr类，Itr类实现Iterator接口，从而必须实现hanNext和Next方法
//	但是Itr类本身不是一个泛型类
	private class Itr implements Iterator<Item>{
		private int cursor;
//		构造函数这里通常用于cursor的初始化
		public Itr() {
//			内部类可以直接访问外部类实例的变量——top
//			这里的cursor记录了当前top的值
			cursor = top;
		}

		public boolean hasNext() {
			// TODO Auto-generated method stub
			if(cursor<0)
				return false;
			else
				return true;
		}

		public Item next() {
			// TODO Auto-generated method stub
			return stack[cursor--];
		}

	public void remove() {

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
	public IterableSqStack(int cap) {
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
