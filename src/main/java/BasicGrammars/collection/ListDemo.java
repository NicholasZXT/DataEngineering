package BasicGrammars.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import BasicGrammars.collection.beans.Course;


public class ListDemo {

	public static void main( String[] args) {
		ListDemo lt = new ListDemo();
		lt.testAdd();
		lt.testType();
		lt.testForEach();
		lt.testGet();
		lt.testIterator();
		lt.testForEach();
		lt.testModify();
		lt.testForEach();
		lt.testRemove();
	}

	/**
	 * 用于存放备选课程的List
	 */
	public List<Course> coursesToSelect;
	
	public ListDemo() {
		this.coursesToSelect = new ArrayList<Course>();
	}
	
	/**
	 * 用于往coursesToSelect中添加备选课程
	 */
	public void testAdd() {
		//创建一个课程对象，并通过调用add方法，添加到备选课程List中
		Course cr1 = new Course("1" , "数据结构");
		coursesToSelect.add(cr1);
		Course temp = coursesToSelect.get(0);
		System.out.println("添加了课程：" + temp.id + ":" + temp.name); 
		
		Course cr2 = new Course("2", "C语言");
		coursesToSelect.add(0, cr2);
		Course temp2 = coursesToSelect.get(0);
		System.out.println("添加了课程：" + temp2.id + ":" + temp2.name);
		
		coursesToSelect.add(cr1);
		Course temp0 = coursesToSelect.get(2);
		System.out.println("添加了课程：" + temp.id + ":" + temp.name); 

		//以下方法会抛出数组下标越界异常
		//Course cr3 = new Course("3", "test");
		//coursesToSelect.add(4, cr3);
		
		Course[] course = {new Course("3", "离散数学"), new Course("4", "汇编语言")};
		coursesToSelect.addAll(Arrays.asList(course));
		Course temp3 = coursesToSelect.get(2);
		Course temp4 = coursesToSelect.get(3);
		System.out.println("添加了两门课程：" + temp3.id + ":" + temp3.name + ";" + temp4.id + ":" + temp4.name);
		
		Course[] course2 = {new Course("5", "高等数学"), new Course("6", "大学英语")};
		coursesToSelect.addAll(2, Arrays.asList(course2));
		Course temp5 = coursesToSelect.get(2);
		Course temp6 = coursesToSelect.get(3);
		System.out.println("添加了两门课程：" + temp5.id + ":" + temp5.name + ";" + temp6.id + ":" + temp6.name);
		
	}
	
	/**
	 * 取得List中的元素的方法
	 */
	public void testGet() {
		int size = coursesToSelect.size();
		System.out.println("有如下课程待选：");
		for(int i= 0 ; i < size; i++) {
			Course cr = coursesToSelect.get(i);
			System.out.println("课程：" + cr.id + ":" + cr.name);
		}
	}
	
	/**
	 * 通过迭代器来遍历List
	 */
	public void testIterator() {
		//通过集合的iterator方法，取得迭代器的实例
		Iterator<Course> it = coursesToSelect.iterator();
		System.out.println("有如下课程待选(通过迭代器访问)：");
		while(it.hasNext()) {
			Course cr = it.next();
			System.out.println("课程：" + cr.id + ":" + cr.name);
		}
	}
	
	/**
	 * 通过 for each 语法访问集合元素
	 */
	public void testForEach() {
		System.out.println("有如下课程待选(通过for each访问)：");
		for (Course cr : coursesToSelect) {
			System.out.println("课程：" + cr.id + ":" + cr.name);
		}
	}
	
	/**
	 * 修改List中的元素
	 */
	public void testModify() {
		coursesToSelect.set(4, new Course("7", "毛概"));
	}
	
	/**
	 * 删除List中的元素
	 */
	public void testRemove() {
		//Course cr = coursesToSelect.get(4);
		System.out.println("即将删除4位置和5位置上的课程！");
		Course[] courses = {coursesToSelect.get(4), coursesToSelect.get(5)};
		coursesToSelect.removeAll(Arrays.asList(courses));
		//coursesToSelect.remove(4);
		System.out.println("成功删除课程！");
		testForEach();
	}
	
	/**
	 * 往List中添加一些奇怪的东西
	 */
	public void testType() {
		System.out.println("能否往List中添加一些奇怪的东西呢！？");
		// 设置了泛型的情况下，下面这句无法通过编译
		//coursesToSelect.add("我不是课程，我只是一个无辜的字符串！！");
	}

}
