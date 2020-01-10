package Reflection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JunitDemo {

    public void demo1(){
        System.out.println("demo1");
    }

    /*正常情况下要运行上面的方法，需要在main函数中创建一个对象，通过对象调用方法

     */
    public static void main(String[] args) {
        JunitDemo junit = new JunitDemo();
        junit.demo1();
    }

    /*Junit使用
    1. 在方法上方添加注解@Test
    2. 导入Junit包，右键Junit运行
     */
    @Test
    public void demo2(){
        System.out.println("demo2");
    }

//    下面这两个方法会在目标方法Test标注前后运行
    @Before
    public void MyBefore(){
        System.out.println("MyBefore");
    }

    @After
    public void MyAfter(){
        System.out.println("MyAfter");
    }
}
