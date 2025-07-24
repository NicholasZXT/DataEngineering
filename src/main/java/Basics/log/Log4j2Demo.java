package Basics.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 */
public class Log4j2Demo {
    public static void main(String[] args) {
        Log4j2Demo logDemo = new Log4j2Demo();
        logDemo.logging();
    }

    // 使用 LogManager 工厂函数 获取一个静态的 logger
    // LogManager.getLogger() 返回的是 Root Logger，也就是 log4j2.xml 中 Loggers.Root 配置的那个
    // LogManager.getLogger(name) 会在配置文件的 Loggers 中查找名为 name 的 Logger 配置，找到了就使用对应的配置，没找到，就使用 Root Logger
    static Logger logger = LogManager.getLogger(Log4j2Demo.class.getSimpleName());
    // 还有一个重载的API，直接传入类对象即可，内部会自动获取类名的字符串格式
    //static Logger logger = LogManager.getLogger(Log4j2Demo.class);
    // 如果要在记录日志时使用 String.format，则需要使用下面的 getFormatterLogger 方法创建Logger
    //static Logger logger = LogManager.getFormatterLogger(Log4j2Demo.class.getSimpleName());

    /**
     * 最常见的Log4j日志工具使用
     */
    public void logging(){
        System.out.println("Log4j2Demo.class.getName(): " + Log4j2Demo.class.getName());
        System.out.println("Log4j2Demo.class.getSimpleName(): " + Log4j2Demo.class.getSimpleName());
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
        logger.info("Info message for {}.", Log4j2Demo.class.getSimpleName());
        // 使用 String.format 的方式需要使用 LogManager.getFormatterLogger() 方法 —— 不过此时就不能使用上面的 {} 方式了
        //logger.info("Info message for %s.", Log4j2Demo.class.getSimpleName());

    }
}
