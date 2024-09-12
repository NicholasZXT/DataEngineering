package HadoopDemos;

import java.util.Arrays;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles;


public class HadoopUtilsDemo {
    public static void main(String[] args) throws Exception {
        Configuration config = showConfigurationUsage();
        System.out.println("*******************************************************");
        GenericOptionsParser.printGenericCommandUsage(System.out);
        System.out.println("*******************************************************");
        String [] args_str = {"-conf", "conf-value", "-D", "prop=prop-value", "other-arg", "some-arg"};
        // 这里直接使用上面返回的 Configuration 对象了
        showToolRunnerUsage(config, args_str);
    }

    /**
     * Hadoop生态里，配置文件使用了 Configuration 来进行封装，它底层主要是解析 xml 文件。
     * 此外，Hadoop 还提供了一个 Configurable 接口，用于表示某个类是可配置的：
     *   Configurable 接口里定义了一个 Configuration 变量的 get/set方法
     * 为了方便使用，也提供了一个实现 Configurable 接口的类 Configured，这个类的源码很简单，就是对 Configurable 接口方法的简单实现。
     */
    public static Configuration showConfigurationUsage() {
        Configuration config = new Configuration();
        //Configuration config = new Configuration(true);
        for(Map.Entry<String, String> entry: config){
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("---------------------------------------------------------");
        config.addResource("hadoop_conf/test.xml");
        config.set("test.add", "add-value");
        for(Map.Entry<String, String> entry: config){
            //System.out.println(entry.getKey() + " = " + entry.getValue());
            if(entry.getKey().startsWith("test")){
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }
        return config;
    }

    /**
     * （1）为了方便Hadoop生态组件解析命令行，提供了 GenericOptionsParser 类来定义了一些Hadoop组件用到的通用命令行选项，并进行解析。
     *     具体有哪些通用选项可以使用 GenericOptionsParser.printGenericCommandUsage(System.out) 方法查看；
     *     不过一般不直接使用这个类，而是使用下面的 ToolRunner 类.
     * （2）ToolRunner 类其实是一个工具类，它只有一系列的静态方法，其中最重要的就是 run() 方法，内部会调用 GenericOptionsParser 解析
     *     命令行选项，并设置到 Configuration 中，然后执行用户代码。
     *     配合使用的还有一个 Tool 接口。
     * （3）ToolRunner的使用如下：
     *      1. 创建一个类，继承 Configured，并实现 Tool 接口的 run 方法，里面定义自己的执行逻辑
     *      2. 使用 ToolRunner.run(Configuration conf, Tool tool, String[] args) 方法，执行上面自定义的Tool对象
     *  ToolRunner具体使用案例，可以看HBase的BulkLoad工具类 org.apache.hadoop.hbase.tool.LoadIncrementalHFiles.
     */
    static class ConfigurationPrinter extends Configured implements Tool {
        /**
         * 自定义一个简单的 Tool 类，用于打印当前环境里的所有配置信息
         * @param args
         * @return
         * @throws Exception
         */
        @Override
        public int run(String[] args) throws Exception {
            // 注意，这里的 args，不是原始的命令行参数，而是经过 GenericOptionsParser 后未能解析的参数
            System.out.println("<ConfigurationPrinter.run> args: " + Arrays.toString(args));
            // 此处只是简单打印了 Configuration 里的所有信息
            Configuration conf = getConf();
            for(Map.Entry<String, String> entry: conf){
                //System.out.printf("%s=%s\n", entry.getKey(), entry.getValue());
                if(entry.getKey().startsWith("test")){
                    System.out.printf("%s=%s\n", entry.getKey(), entry.getValue());
                }
            }
            return 0;
        }
    }

    public static void showToolRunnerUsage(Configuration conf, String [] args) throws Exception {
        // Configuration 对象可以是传入的，也可以新建一个
        //Configuration conf = new Configuration();
        //Configuration conf = HBaseConfiguration.create();
        // 第一个参数就是待配置的 Configuration 对象，第二个参数是自定义的 Tool 类，最后一个是传入的命令行参数
        // 注意，自定义Tool类里 run() 方法接受的参数是 GenericOptionsParser 里未解析剩下的参数
        int ret = ToolRunner.run(conf, new ConfigurationPrinter(), args);
        //System.exit(ret);
    }
}
