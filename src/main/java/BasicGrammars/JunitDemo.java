package BasicGrammars;

// 开发人员编写单元测试用的注解
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
// Junit 内部具体执行测试的接口
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JunitDemo {

    /*正常情况下要运行 JunitDemo 里的方法，需要在main函数中创建一个对象，通过对象调用方法*/
    public static void main(String[] args) {
        // 手动测试流程
        JunitDemo junit = new JunitDemo();
        junit.demo1();

        // Junit 框架的实际调用流程如下所示
        // 不过在IDEA中使用时，为了能够实现可视化操作，IDEA还在Junit外封装了一层，主要是获取单元测试的信息，然后根据点击的按钮来执行具体测试
        System.out.println("------- Junit runner --------");
        Result result = JUnitCore.runClasses(JunitDemo.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }

    public void demo1(){
        System.out.println("demo1");
    }

    /*Junit使用
    1. 在方法上方添加注解@Test
    2. 导入Junit包，右键Junit运行
    需要注意的是，被测试的方法不能含有参数和返回值
     */
    @Test
    public void demo2(){
        System.out.println("demo2");
    }

    //下面这两个方法会在目标方法Test标注前后运行
    @Before
    public void MyBefore(){
        System.out.println("MyBefore");
    }

    @After
    public void MyAfter(){
        System.out.println("MyAfter");
    }
}
