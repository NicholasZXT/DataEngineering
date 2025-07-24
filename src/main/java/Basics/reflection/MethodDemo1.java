package Basics.reflection;

import java.lang.reflect.Method;

public class MethodDemo1 {
	public static void main(String[] args) {
		//1. 要获取一个方法就是获取类的信息，获取类的信息首先要获取类的类类型
		PrintClass p = new PrintClass();
		Class c = p.getClass();

		/*
		 * 2. 具体获取哪个方法，由 方法名称 和 参数列表 来决定
		 *   getMethod获取的是public的方法
		 *   getDelcaredMethod自己声明的方法
		 */
	    try {
			// 获取 print() 方法
			//Method m1 = c.getMethod("print", new Class[]{});
			Method m1 = c.getMethod("print");
			m1.invoke(p, new Object[]{});
			m1.invoke(p);
			System.out.println("==================");

			// 获取 print(int, int) 方法
			//Method m2 =  c.getMethod("print", new Class[]{int.class, int.class});
	    	Method m2 = c.getMethod("print", int.class, int.class);
	    	//方法的反射操作  
	    	//p.print(10, 20) 方法的反射操作是用 m 对象来进行方法调用，和 p.print 调用的效果完全相同
	        //方法如果没有返回值返回null,有返回值返回具体的返回值
	    	m2.invoke(p, new Object[]{10, 20});
	    	m2.invoke(p, 10, 20);
	    	System.out.println("==================");

	    	//获取 print(String,String) 方法
             Method m3 = c.getMethod("print", String.class, String.class);
             //用方法进行反射操作
             //p.print("hello", "WORLD");
             m3.invoke(p, "hello","WORLD");
             System.out.println("===================");

		} catch (Exception e) {
			e.printStackTrace();
		} 
     
	}
}

class PrintClass{
	public void print(){
		System.out.println("helloworld");
	}
	public void print(int a,int b){
		System.out.println(a+b);
	}
	public void print(String a,String b){
		System.out.println(a.toUpperCase()+","+b.toLowerCase());
	}
}
