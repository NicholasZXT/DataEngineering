package BasicGrammars;

import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertitiesDemo {

    @Test
    public void demo1(){
        //Propertities 是特殊的Map<String,String>
        //1. 创建对象
        Properties props = new Properties();
        //2. 设置数据
        props.setProperty("k1","v1");
        props.setProperty("k2","v2");
        //3. 获取指定名称的数据
        String v1 = props.getProperty("k1");
        System.out.println(v1);
        //4. 获取所有名称，并遍历值
        Set<String> keys = props.stringPropertyNames();
        System.out.println("-------- for print ----------");
        for (String name : keys){
            String value = props.getProperty(name);
            System.out.println(value);
        }
        // 5. 更方便的打印
        System.out.println("-------- forEach print ----------");
        props.forEach((k, v) -> {
            System.out.println("ENV : " + k + ", Value : " + v);
        });
        // 6. toString打印
        System.out.println("-------- toString print ----------");
        System.out.println(props);
    }

    @Test
    public void demo2() throws Exception{
        //Propertities 对应于一种以 .properties 结尾的文件
        //一行表示一个键值对，格式是 key=value
        //1. 创建对象
        Properties props = new Properties();
        //2. 设置数据
        props.setProperty("k1","v1");
        props.setProperty("k2","v2");
        //3. 写入到文件
        Writer writer = new OutputStreamWriter(new FileOutputStream("app.properties"),"UTF-8");
        //写入到当前工程下的目录
        props.store(writer,"description");
        writer.close();
        //4. 从properties文件中加载数据
        Reader reader = new InputStreamReader(new FileInputStream("app.properties"), "UTF-8");
        props.load(reader);
        for (String name : props.stringPropertyNames()){
            String value = props.getProperty(name);
            System.out.println(value);
        }
    }

    @Test
    public void demo3(){
        // 设置和获取系统配置
        System.setProperty("self-key", "self-value");
        Properties properties = System.getProperties();
        properties.forEach((k, v) -> {
            System.out.println("ENV : " + k + ", Value : " + v);
        });
    }

    @Test
    public void demo4(){
        // 设置环境变量 —— 这个方式没有用，也没找到其他的方式
        System.setProperty("SELF-ENV", "self-env-value");
        // 获取系统的环境变量
        Map<String, String> map = System.getenv();
        map.forEach((k, v) -> {
            System.out.println("ENV : " + k + ", Value : " + v);
        });
    }
}
