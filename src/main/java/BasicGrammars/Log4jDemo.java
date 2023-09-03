package BasicGrammars;

import KafkaDemos.HelloConsumer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * 参考官方文档 https://logging.apache.org/log4j/2.x/manual/configuration.html
 * log4j 主要是通过配置文件进行设定，默认会在classpath目录下按照如下优先级寻址配置文件（注意中间的 2）：
 *   log4j2-disable.properties > log4j2.yaml/log4j2.yml > log4j2.json/log4j.jsn > log4j2.xml
 *   如果都没有找到，则会按默认配置输出，也就是输出到控制台.
 *   注意，从 version 2.4 版本开始，log4j 才开始支持 .properties 格式，考虑到 log4j 的配置是层次化的，最推荐的方式是 xml 格式.
 * log4j 的日志级别和优先级为：OFF > FATAL > ERROR > WARN> INFO > DEBUG > TRACE > ALL.
 * 打印日志时，会打印 >= 所设置级别的日志，设置的日志等级越高，打印出来的日志就越少
 *
 * log4j 的 架构大致如下：
 * Root Logger ——> Loggers
 *  │
 *  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
 *  ├──>│ Appender │───>│  Filter  │───>│  Layout  │───>│ Console  │
 *  │   └──────────┘    └──────────┘    └──────────┘    └──────────┘
 *  │
 *  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
 *  ├──>│ Appender │───>│  Filter  │───>│  Layout  │───>│   File   │
 *  │   └──────────┘    └──────────┘    └──────────┘    └──────────┘
 *  │
 *  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
 *  └──>│ Appender │───>│  Filter  │───>│  Layout  │───>│  Socket  │
 *      └──────────┘    └──────────┘    └──────────┘    └──────────┘
 * Root Logger 下，可以配置多个 Logger，每个 Logger 有如下组件
 * - Appender: 用于定义日志的输出位置，比如控制台(Console)，文件(File)还是远程计算机(Socket)
 * - Filter: 用于对日志进行过滤，比如只输出 ERROR 级别日志
 * - Layout: 定义日志的输出格式，比如日志的日期、时间、方法名称等
 */
public class Log4jDemo {
    public static void main(String[] args) {
        Log4jDemo logDemo = new Log4jDemo();
        logDemo.log4jLogging();
    }

    // 使用 LogManager 工厂函数 获取一个静态的 logger
    // LogManager.getLogger() 返回的是 Root Logger，也就是 log4j2.xml 中 Loggers.Root 配置的那个
    // LogManager.getLogger(name) 会在配置文件的 Loggers 中查找名为 name 的 Logger 配置，找到了就使用对应的配置，没找到，就使用 Root Logger
    static Logger logger = LogManager.getLogger(Log4jDemo.class.getSimpleName());
    // 如果要使用 String.format，则需要使用下面的方法
    //static Logger logger = LogManager.getFormatterLogger(HelloConsumer.class.getSimpleName());

    /**
     * 最常见的Log4j日志工具使用
     */
    public void log4jLogging(){
        System.out.println(Log4jDemo.class.getName());
        System.out.println(Log4jDemo.class.getSimpleName());
        logger.log(Level.INFO, "Info Level message ...");
        // 需要注意的是，如果 Root Logger 和 这里自定义的 Logger（假设 Loggers 配置中有对应配置）均设置了 Console 的 Appender，
        // 并且子 Logger 的配置里 additivity = "true"（默认配置），那么下面的信息再控制台里可能会输出两次，具体要看 Root Logger 和 当前
        // Logger 里设置的 Log Level 如何.
        logger.debug("Debug message ...");
        logger.info("Info message ...");
        logger.warn("Warn message ...");
        logger.error("Error message ...");
        logger.fatal("Fatal message ...");

        // 字符串占位符
        // 默认支持使用 {} 作为占位符
        logger.info("Info message for {}.", Log4jDemo.class.getSimpleName());
        // 使用 String.format 的方式需要使用 LogManager.getFormatterLogger() 方法 —— 不过此时就不能使用上面的 {} 方式了
        logger.info("Info message for %s.", Log4jDemo.class.getSimpleName());

    }
}
