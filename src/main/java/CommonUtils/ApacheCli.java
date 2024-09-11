package CommonUtils;

import java.util.Arrays;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;


public class ApacheCli {
    public static void main(String[] args) throws ParseException{
        // 解析命令行参数分为3步：

        // 第1步：定义要解析的参数信息，使用 Option 和 Options 两个类，前者定义单个参数，后者是所有参数项的容器
        Options options = new Options();
        // 有 3 种方式创建一个单个的 Option 对象
        // 1.1 直接 new，下面展示的是最全的一个构造函数，hasArg表示该参数是否必须有值
        Option p = new Option("p", "param", true, "param-<p, param>");
        // 其他的信息则需要调用对应的方法设置，下面设置该参数是必须要有的
        p.setRequired(true);
        p.setArgName("some-param");
        p.setType(String.class);
        // 向 Options 容器中添加设置好的一个选项
        options.addOption(p);
        // 1.2 直接添加设置选项，这种方式最快，但是灵活性欠佳
        options.addOption("y","yes", true, "param-<y, yes>");
        // 1.3 使用构造模式创建
        Option f = Option.builder("f")  //短key
                .longOpt("file")  //长key
                .hasArg(true)  //是否含有参数
                .argName("filePath")  //参数值的名称
                .required(true)  //命令行中必须包含此 option
                .desc("文件的路径")  //描述
                .optionalArg(false)  //参数的值是否可选
                //.numberOfArgs(3)  //指明参数有多少个参数值
                //.hasArgs() //无限制参数值个数
                //.valueSeparator(',')  // 参数值的分隔符
                .type(String.class)  //参数值的类型
                .build();
        options.addOption(f);

        // 这里手动设置需要解析的命令行参数
        String [] args_str = {"-p", "p-value", "-y", "y-value", "-f", "f-value"};

        // 第2步：使用解析器来解析命令行内容
        // CommandLineParser 接口有多个实现，一般用下面的即可
        CommandLineParser parser = new DefaultParser();
        // 传入设置的解析项，和待解析的命令行，返回 CommandLine 对象
        CommandLine cmd = parser.parse(options, args_str);

        // 第3步：使用 CommandLine 对象提供的方法检查有无参数，并获取参数值
        System.out.println("args_parse:");
        String[] args_parse = cmd.getArgs();
        System.out.println(Arrays.toString(args_parse));
        System.out.println("------------------------------------");
        System.out.println("has option p: " + cmd.hasOption("p"));
        System.out.println("option p value: " +cmd.getOptionValue("p"));
        System.out.println("has option y: " + cmd.hasOption("y"));
        System.out.println("option y value: " +cmd.getOptionValue("y"));
        System.out.println("has option f: " + cmd.hasOption("f"));
        System.out.println("option f value: " +cmd.getOptionValue("f"));

        //打印帮助信息
        System.out.println("-------------------------------------");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ApacheCli", options);
        formatter.printHelp("ApacheCli", options, true);

    }
}
