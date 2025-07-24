package Basics.reflection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MethodDemo2 {
	public static void main(String[] args) {
		MethodDemo2.demo1(args);
		MethodDemo2.demo2(args);
		MethodDemo2.demo3(args);
	}

	public static void demo1 (String[] args) {
		UserService us = new UserService();
		/*
		 * 通过键盘输入命令执行操作
		 * 输入update命令就调用update方法
		 * 输入delete命令就调用delete方法
		 * ...
		 */
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("请输入命令:");
			String action = br.readLine();
			/*
			if("update".equals(action)){
				us.update();
			}
			if("delete".equals(action)){
				us.delete();
			}
			if("find".equals(action)){
				us.find();
			}
			*/
			/*
			 * action就是方法名称，都没有参数--->通过方法的反射操作就会简单很多
			 * 通过方法对象然后进行反射操作
			 */
			Class c = us.getClass();
			Method m = c.getMethod(action);
			m.invoke(us);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void demo2 (String[] args) {
		User u1 = new User("zhangsan", "123456", 30);
		System.out.println(BeanUtil.getValueByPropertyName(u1, "username"));
		System.out.println(BeanUtil.getValueByPropertyName(u1, "userpass"));
	}

	public static void demo3 (String[] args) {
		ArrayList list1 = new ArrayList();
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("hello");
		//list2.add(20)  //代码中不能这样操作
		Class c1 = list1.getClass();
		Class c2 = list2.getClass();
		System.out.println(c1);
		System.out.println(c2);
		System.out.println(c1 == c2);
		//反射的操作都是编译之后的操作
		/*
		 * c1==c2结果返回true说明编译之后集合的泛型是去泛型化的
		 * Java中集合的泛型，是防止错误输入的，只在编译阶段有效，
		 * 绕过编译就无效了
		 * 验证：我们可以通过方法的反射来操作，绕过编译
		 */
		try {
			Method m = c2.getMethod("add", Object.class);
			m.invoke(list2, 20);  //绕过编译操作就绕过了泛型
			System.out.println(list2.size());
			System.out.println(list2);
			//虽然 20 这个元素放进去了 list2，但是现在不能使用下面的方式遍历了
			for (String string: list2) {
				System.out.println(string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
