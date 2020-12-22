package DataStructures;

import java.util.Iterator;
import java.util.Scanner;


/**
 * 顺序栈的示例，使用数组实现
 * 自动扩容，使用了泛型
 * 继承了Iteration接口，不是Iterable接口
 * @author danielzhang
 *
 */
public class IterationSqStack<Item> implements Iterator<Item> {

//	main函数，测试用例
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入栈的初始容量：");
		int cap = scan.nextInt();
//		存贮String的栈	
		System.out.println("存储String的栈示例：");
//		注意泛型栈的使用
		IterationSqStack<String> stackStr = new IterationSqStack<String>(cap);
		System.out.println("请输入字符串(eof结束):");
		while(!scan.hasNext("eof")) {
			stackStr.Push(scan.next());
		}
		scan.close();
		System.out.println("栈的大小为：" + stackStr.Size());
		
//		测试是否实现Iterable接口,注意，测试之前不能执行出栈过程，否则栈中就什么都没有了
		System.out.println("测试Iterable：");
//		因为没有实现Iterable接口，所以不能使用for-each语句
//		for(String item : stackStr) {
//			System.out.println(item);
//		}
		
//		使用iteration接口的hasNext和next方法
		System.out.println(stackStr.hasNext());
		System.out.println(stackStr.next());
		
//		注意，即使未执行出栈过程，上面的next方法执行一次后，栈的大小已经小了一个，说明此时next就相当于出栈
		System.out.println("此时栈的大小为：" + stackStr.Size());
//		测试完Iterable之后才能演示所有元素出栈过程
		System.out.println("出栈过程：");
		while(!stackStr.isEmpty()) {
			System.out.println(stackStr.Pop());
		}
	}

//	实现Iteration接口的内容
//	实际上，这里的hasNext和next方法的逻辑与栈里面的isEmpty和Pop方法是一样的
//	由于直接实现的这两个方法，这两个方法调用的this.top和this.stack是实例变量的值，特别是next方法中
//	改变了实例变量top的值，就会影响到实例变量的状态——这意味只能遍历一次
//	而Iteration的接口本意只是想遍历该实例变量的数据，而不是影响该实例变量。
	public boolean hasNext() {
		if(this.top < 0)
			return false;
		else
			return true;
	}

	public Item next() {
//		这里调用this.top之后执行--，会改变实例变量的值，实际上相当于执行了出栈操作
		return this.stack[this.top--];
	}

//	空的remove方法
	public void remove() { }

	//	栈的内容，使用泛型数组，但是要注意，实际上Java是不支持泛型数组的，这里只是申明
	private Item[] stack;
//	栈顶指针
	private int top;
//	栈的最大容量
	private int capacity;
	
//	构造函数
	@SuppressWarnings("unchecked")
	public IterationSqStack(int cap) {
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
