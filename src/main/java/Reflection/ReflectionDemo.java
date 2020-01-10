package Reflection;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionDemo {
    /*
    有三种方式使用获得Class对象
    1. 通过字符串(全限定类名)获得
        Class clazz = Class.forName("字符串")
        全限定类名：包名 + 类名
        用于从配置文件中获得全限定类名，并通过反射进行操作
    2. 通过Java类型获得
        Class clazz = 类型.class
        用于确定构造方法、普通方法形参列表时，需要通过类型进行获得
    3. 通过实例对象获得
        Class clazz = obj.getClass()
        方法内部通过变量名获得
     */

    @Test
    public void demo1() throws ClassNotFoundException {
//        通过字符串获得Class
        Class clazz1 = Class.forName("Reflection.Bean");
        System.out.println(clazz1);
//        第二种
        Class clazz2 = Bean.class;
        System.out.println(clazz2);
//        第三种
        Bean obj = new Bean();
        Class clazz3 = obj.getClass();
        System.out.println(clazz3);
    }

    @Test
    public void demo2() throws Exception {
//        通过无参构造方法获得实例对象
//        获得class
        Class clazz = Class.forName("Reflection.Bean");
//        获得构造对象
        Constructor cons = clazz.getConstructor();
//        获得实例，相当于 new Bean()
        Object obj = cons.newInstance();
    }

    @Test
    public void demo3() throws Exception {
//        通过有参构造方法获得实例对象
//        获得class
        Class clazz = Class.forName("Reflection.Bean");
//        获得构造对象，指定形参
        Constructor cons = clazz.getConstructor(String.class);
//        获得实例，传入实参
        Object obj = cons.newInstance("2020");
    }

    @Test
    public void demo4() throws Exception {
//        通过无参构造方法 快速 获得实例对象
//        获得class
        Class clazz = Class.forName("Reflection.Bean");
//        获得clazz 直接创建对象
        Object obj = clazz.newInstance();
    }

}
