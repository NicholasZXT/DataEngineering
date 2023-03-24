package BasicGrammars.reflection;

import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        //通过字符串获得Class
        Class clazz1 = Class.forName("BasicGrammars.reflection.Bean");
        System.out.println(clazz1);
        //第二种
        Class clazz2 = Bean.class;
        System.out.println(clazz2);
        //第三种
        Bean obj = new Bean();
        Class clazz3 = obj.getClass();
        System.out.println(clazz3);
    }

    @Test
    public void demo2() throws Exception {
        //通过无参构造方法获得实例对象
        //获得class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //获得构造对象
        Constructor cons = clazz.getConstructor();
        //获得实例，相当于 new Bean()
        Object obj = cons.newInstance();
    }

    @Test
    public void demo3() throws Exception {
        //通过有参构造方法获得实例对象
        //获得class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //获得构造对象，指定形参
        Constructor cons = clazz.getConstructor(String.class);
        //获得实例，传入实参
        Object obj = cons.newInstance("2020");
    }

    @Test
    public void demo4() throws Exception {
        /*
        通过无参构造方法 快速 获得实例对象
        * */
        //获得class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //获得clazz 直接创建对象
        Object obj = clazz.newInstance();
    }

    @Test
    public void demo5() throws Exception {
    /*
    通过 私有 构造方法来创建对象
    */
        //获得class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //获得构造对象，指定形参
        //clazz.getConstructor获取的是指定对象的 公有 构造方法
        //Constructor cons = clazz.getConstructor(String.class);
        //下面的clazz.getDeclaredConstructor获取的是指定对象的 任意 构造方法
        Constructor cons = clazz.getDeclaredConstructor(String.class, String.class);
        //通知JVM，运行私有化实例构造（默认不允许）
        cons.setAccessible(true);
        //获得实例，传入实参
        Object obj = cons.newInstance("2020","zxt");
        System.out.printf(obj.toString());
    }

    @Test
    public void MethodDemo1() throws Exception {
    /*
    通过 反射 来使用公共方法，比如set和get方法
    */
        //获得Class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //获得clazz 直接创建实例对象
        Object obj = clazz.newInstance();
        //获得方法，需要明确形参列表
        //这里获取的是setID方法
        Method method1 = clazz.getMethod("setId", String.class);
        //执行方法，传入实例对象和相应的参数
        method1.invoke(obj, "2020");
        System.out.println(obj.toString());

        //通过getID获得数据
        Method method2 = clazz.getMethod("getId");
        //这里需要做一次类型强转
        String id = (String) method2.invoke(obj);
        System.out.println(id);
    }

    @Test
    public void MethodDemo2() throws Exception {
    /*
    通过 反射 来执行静态方法 main
    */
        //获得Class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        //正常是需要通过Class新建一个实例对象，但是由于main是静态方法，所以不需要类的实例对象
        //直接获得静态方法main
        Method method = clazz.getMethod("main", String[].class);
        //运行main方法
        //这里不需要传入实例对象，所以这里为 null
        //参数为可变参数，也就是数组，此时JVM会将传入的参数打散变为多个
        String [] args = {"arg1","arg2"};
        //method.invoke(null, args);  这个会报错
        //方式1：将args强转成Object
        method.invoke(null, (Object)args );
        //方式2：提供二维数组，将args作为二维数组的第一个数
        method.invoke(null,new Object[] {args});
    }

    @Test
    public void FieldDemo1() throws Exception {
    /*
    通过 反射 来获取 公共 字段
    */
        //获得Class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        Object obj = clazz.newInstance();
        //获取 公共 字段
        Field field = clazz.getField("description");
        field.set(obj,"获取字段");
        System.out.println(obj.toString());
        //获取字段值
        String des = (String) field.get(obj);
        System.out.println(des);

    }

    @Test
    public void FieldDemo2() throws Exception {
        /*通过 反射 来获取 私有 字段*/
        //获得Class
        Class clazz = Class.forName("BasicGrammars.reflection.Bean");
        Object obj = clazz.newInstance();
        //获取私有字段
        Field field = clazz.getDeclaredField("id");
        //设置访问私有
        field.setAccessible(true);
        //然后继续操作
        field.set(obj,"2019");
        System.out.println(obj.toString());
        //获取字段值
        String des = (String) field.get(obj);
        System.out.println(des);
    }
}
