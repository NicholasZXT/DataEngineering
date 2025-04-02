# Introduction

参考博客：
+ [稀土掘金-深入掌握Java日志体系，再也不迷路了](https://juejin.cn/post/6905026199722917902)
+ [Java日志框架（JCL,SLF4J,Log4J,Logback）](https://whatsrtos.github.io/Java/JavaEE-Log/)
+ [知乎-带你深入Java Log框架，彻底搞懂Log4J、Log4J2、LogBack，SLF4J](https://zhuanlan.zhihu.com/p/615102614)

个人感觉Java的日志框架有那么一点混乱，这里根据上述博客的介绍，简单梳理一下。

按照时间顺序，各个日志组件如下：
1. Log4j(Log for java) 1.x，出现的最早，作者是 Ceki，后面成为Apache项目
2. JUL(Java Util Logging)，jdk1.4 新增的日志库，Sun公司推出
3. JCL(Jakarta Commons Logging)，**Apache推出**，后面更名为 Commons Logging
4. Slf4j(Simple Logging Facade for JAVA) 和 Logback，**不属于Apache基金会**，Ceki 开发
5. Log4j 2.x，Apache于 2012年 推出的新版日志组件，它不兼容 Log4j 1.x

如果按照架构来分，主要分为如下两类：
+ 记录型日志框架，底层负责具体的日志输出
  + Log4j 1.x
  + JUL
  + Logback
  + Log4j 2.x
+ 门面型日志框架，上层用于封装底层的记录型日志框架
  + JCL，又叫 Commons Logging
  + Slf4j

实际中，常用的搭配组合有如下几种：
+ JCL(Commons Logging) + Log4j 1.x/2.x：这一套都是Apache的组件，多用于Apache的开源项目中，比如大数据相关的组件，使用比较广泛
+ **Slf4j + Logback**：这一套是 Log4j 1.x 的作者离开Apache之后，另立门户开发的日志框架组合，使用的广泛程度有后来居上的趋势
+ Slf4j + Log4j 1.x/2.x：需要在中间加一个适配层，也就是 slf4j -> slf4j-log4j12-version.jar -> log4j，性能没那么好。
+ JCL(Commons Logging) + Logback：也需要在中间加一个适配层 jcl-over-slf4j.jar

jdk自带的JUL使用场景比较少，远不如上面两套日志框架广泛，下面分阵营介绍各个日志组件。

---
# Apache阵营

## Log4j 1.x

官网地址：
+ [Apache->Logging Service->log4j->1.2](https://logging.apache.org/log4j/1.x)
+ [Introduction](https://logging.apache.org/log4j/1.x/manual.html)
> 根据官网文档，Log4j-1.x 已经于2015年停止维护（最高版本1.2.17，提交于2012.5），建议升级到 Log4j-2.x版本。

Log4j-1.x的Maven坐标如下：
```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

Log4j-1.x分为 3 个部分：
+ Logger：日志记录器，用于在代码中记录日志
+ Appender：日志输出组件，logger记录的日志会发送到（多个）appender，决定后续输出到何种地方，控制台，文件还是网络流
+ Layout：日志格式组件，用于控制日志格式

简单使用教程参考 [log4j1.2.17的使用与log4j.properties配置详解](https://blog.csdn.net/xiaoxianer321/article/details/108869506).

使用示例代码如下：
```java
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class logTest {
    // getLogger 里传入一个字符串表示日志名称就行，这里使用了当前类的类名
	private static Logger logger = Logger.getLogger(logTest.class);
	public static void main(String[] args) throws Exception {
		//使用项目根目录（与src同级）下的 log4j.properties 配置文件，其实不写也行，默认会寻找 src/resources/log4j.properties
		PropertyConfigurator.configure("log4j.properties");
        // 记录日志
	    logger.trace("我是 Trace Message!");
        logger.debug("我是 Debug Message!");
        logger.info("我是 Info Message!");
        logger.warn("我是 Warn Message!");
        logger.error("我是 Error Message!");
        logger.fatal("我是 Fatal Message!");
    }  
}
 
/**输出结果：
  2020-10-11 20:11:24,277 [main] DEBUG [com.cn.log4j.logTest] - 我是 Debug Message!
  2020-10-11 20:11:24,278 [main] INFO  [com.cn.log4j.logTest] - 我是 Info Message!
  2020-10-11 20:11:24,278 [main] WARN  [com.cn.log4j.logTest] - 我是 Warn Message!
  2020-10-11 20:11:24,278 [main] ERROR [com.cn.log4j.logTest] - 我是 Error Message!
  2020-10-11 20:11:24,278 [main] FATAL [com.cn.log4j.logTest] - 我是 Fatal Message!
*/
```

### 配置文件

配置文件支持 xml 和 properties 两种格式，这里以properties格式介绍。 

配置文件名必须为`log4j.properties`，存放在`resources`目录下：

```properties
################################################################################
#（1）配置根Logger，语法为：
# log4j.rootLogger = <日志级别>, <Appender1>, <Appender2>, ...
# level 是日志记录的优先级，分为 OFF, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL
#   Log4j 建议只使用四个级别，优先级从低到高分别是 DEBUG, INFO, WARN, ERROR
#   通过在这里定义的级别，您可以控制到应用程序中相应级别的日志信息的开关
#   比如在这里定义了INFO级别，则应用程序中所有DEBUG级别的日志信息将不被打印出来
# appenderName 就是指定日志信息输出到哪个地方的配置名称——名称任意取，但是要和下面对应，可同时指定多个输出目的
# 示例: 
log4j.rootLogger=INFO, console, file

################################################################################
#（2）配置日志信息输出目的地Appender，语法为：
#添加appender提供的实现类
# log4j.appender.appenderName = fully.qualified.name.of.appender.class
#添加appender提供的实现类的属性
# log4j.appender.appenderName.optionN = valueN

# 示例:
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.err
log4j.appender.file = org.apache.log4j.FileAppender选项属性
log4j.appender.file.File = .\\log4j.log
log4j.appender.file.Append = true

#Log4j提供的appender常用的有以下几种：
# 1) org.apache.log4j.ConsoleAppender, 输出到控制台
# 2) org.apache.log4j.FileAppender, 输出到文件
# 3) org.apache.log4j.DailyRollingFileAppender, 每天产生一个日志文件
# 4) org.apache.log4j.RollingFileAppender, 文件大小到达指定尺寸的时候产生一个新的文件
# 5) org.apache.log4j.WriterAppender, 将日志信息以流格式发送到任意指定的地方
#
#1) ConsoleAppender选项属性
# - Threshold = DEBUG, 指定日志消息的输出最低层次（来自父类：AppenderSkeleton.java的属性）
# - ImmediateFlush = true, 默认值是true,所有的消息都会被立即输出，设置为false则不会输出 （来自父类WriterAppender.java的属性）
# - Target = System.err, 默认值System.out,输出到控制台(err为红色,out为黑色)
#
#2) FileAppender选项属性
# - Threshold = INFO, 指定日志消息的输出最低层次 （来自父类：AppenderSkeleton.java的属性）
# - ImmediateFlush = true, 默认值是true,所有的消息都会被立即输出 （来自父类WriterAppender.java的属性）
# - Encoding = UTF-8, 可以指定文件编码格式 （来自父类WriterAppender.java的属性）
# - File = C:\log4j.log, 指定消息输出到C:\log4j.log文件
# - Append = false, 默认值true,将消息追加到指定文件中，false指将消息覆盖指定的文件内容
# - BufferedIO = = false, 是否启用缓冲区,默认为false
# - BufferSize = 8 * 1024, 缓冲区大小,默认为8KB
#
#3) DailyRollingFileAppender选项属性
# - Threshold = WARN, 指定日志消息的输出最低层次 （来自父类：AppenderSkeleton.java的属性）
# - ImmediateFlush = true, 默认值是true,所有的消息都会被立即输出 （来自父类WriterAppender.java的属性）
# - Encoding = UTF-8, 可以指定文件编码格式 （来自父类WriterAppender.java的属性）
# - File = C:\log4j.log, 指定消息输出到C:\log4j.log文件 （来自父类FileAppender.java的属性）
# - Append = false, 默认值true,将消息追加到指定文件中，false指将消息覆盖指定的文件内容 （来自父类FileAppender.java的属性）
# - DatePattern='.'yyyy-ww, 每周滚动一次文件,即每周产生一个新的文件。
#     还可以按用以下参数:
#     '.'yyyy-MM, 每月
#     '.'yyyy-ww, 每周
#     '.'yyyy-MM-dd, 每天
#     '.'yyyy-MM-dd-a, 每天两次
#     '.'yyyy-MM-dd-HH, 每小时
#     '.'yyyy-MM-dd-HH-mm, 每分钟
#
#4) RollingFileAppender选项属性
# - Threshold = ERROR, 指定日志消息的输出最低层次 （来自父类：AppenderSkeleton.java的属性）
# - ImmediateFlush = TRUE, 默认值是true,所有的消息都会被立即输出 （来自父类WriterAppender.java的属性）
# - Encoding = UTF-8, 可以指定文件编码格式 （来自父类WriterAppender.java的属性）
# - File = C:/log4j.log, 指定消息输出到C:/log4j.log文件 （来自父类FileAppender.java的属性）
# - Append = FALSE, 默认值true,将消息追加到指定文件中，false指将消息覆盖指定的文件内容 （来自父类FileAppender.java的属性）
# - MaxFileSize = 100KB, 后缀可以是KB,MB,GB.在日志文件到达该大小时,将会自动滚动.如:log4j.log.1
# - MaximumFileSize = 1024, 直接使用字节不带单位
# - MaxBackupIndex = 2, 指定可以产生的滚动文件的最大数

################################################################################
#（3）配置日志信息的格式(布局)，其语法为：
# 添加layout的实现类
# log4j.appender.appenderName.layout = fully.qualified.name.of.layout.class
# 添加layout的实现类的属性
# log4j.appender.appenderName.layout.optionN = valueN

# 示例:
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c - %m%n
log4j.appender.file.layout = org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern = %d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c - %m%n

#Log4j提供的layout有以下几种：
# 1) org.apache.log4j.HTMLLayout, 以HTML表格形式布局
# 2) org.apache.log4j.PatternLayout, 可以灵活地指定布局模式 —— 这个最常用
# 3) org.apache.log4j.SimpleLayout, 包含日志信息的级别和信息字符串
# 4) org.apache.log4j.TTCCLayout, 包含日志产生的时间、线程、类别等等信息
# 5) org.apache.log4j.xml.XMLLayout, 以XML形式布局
#
#1) HTMLLayout选项属性
# - LocationInfo = TRUE, 默认值false,输出java文件名称和行号
# - Title=Struts Log Message, 默认值 Log4J Log Messages
#
#2) PatternLayout选项属性
# - ConversionPattern = %m%n, 格式化指定的消息(参数格式见下面)
#
#3) XMLLayout选项属性
# - LocationInfo = TRUE, 默认值false,输出java文件名称和行号
#
#Log4J采用类似C语言中的printf函数的打印格式格式化日志信息，打印参数如下：
# %m 输出代码中指定的消息
# %p 输出优先级，即DEBUG,INFO,WARN,ERROR,FATAL
# %r 输出自应用启动到输出该log信息耗费的毫秒数
# %c 输出所属的类目,通常就是所在类的全名
# %t 输出产生该日志事件的线程名
# %n 输出一个回车换行符，Windows平台为“\r\n”，Unix平台为“\n”
# %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式
#    如：%d{yyyy年MM月dd日 HH:mm:ss,SSS}，输出类似：2012年01月05日 22:10:28,921
# %l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数
#    如：Testlog.main(TestLog.java:10)
# %F 输出日志消息产生时所在的文件名称
# %L 输出代码中的行号
# %x 输出和当前线程相关联的NDC(嵌套诊断环境),像java servlets多客户多线程的应用中
# %% 输出一个"%"字符
#
# 可以在%与模式字符之间加上修饰符来控制其最小宽度、最大宽度、和文本的对齐方式。如：
#  %5c: 输出category名称，最小宽度是5，category<5，默认的情况下右对齐
#  %-5c:输出category名称，最小宽度是5，category<5，"-"号指定左对齐,会有空格
#  %.5c:输出category名称，最大宽度是5，category>5，就会将左边多出的字符截掉，<5不会有空格
#  %20.30c:category名称<20补空格，并且右对齐，>30字符，就从左边交远销出的字符截掉

################################################################################
#（4）指定特定包的输出特定的级别
# 格式：log4j.logger.<包名>=<日志级别>
log4j.logger.org.springframework=DEBUG
```

---
## Log4j 2.x

官方文档 [Apache Log4j](https://logging.apache.org/log4j/2.x)。

Log4j-2.x的Maven坐标如下，分成了 core + api 两个依赖：
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.20.0</version>
</dependency>
```

Log4j-2.x 主要有如下组件：
+ `Logger`: 日志记录器
+ `Appender`: 日志输出目的地
+ `Layout`: 输出格式
+ `Filter`: 日志过滤
+ `Lookup`: 访问系统属性、环境变量等

log4j-2.x 的架构大致如下：
```text
Root Logger ——> Loggers
│
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
├──>│ Appender │───>│  Filter  │───>│  Layout  │───>│ Console  │
│   └──────────┘    └──────────┘    └──────────┘    └──────────┘
│
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
├──>│ Appender │───>│  Filter  │───>│  Layout  │───>│   File   │
│   └──────────┘    └──────────┘    └──────────┘    └──────────┘
│
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
└──>│ Appender │───>│  Filter  │───>│  Layout  │───>│  Socket  │
    └──────────┘    └──────────┘    └──────────┘    └──────────┘
```
+ Root Logger 下，可以配置多个 Logger，每个 Logger 有如下组件
  + Appender: 用于定义日志的输出位置，比如控制台(Console)，文件(File)还是远程计算机(Socket)
  + Filter: 用于对日志进行过滤，比如只输出 ERROR 级别日志
  + Layout: 定义日志的输出格式，比如日志的日期、时间、方法名称等

### 配置文件
log4j-2.x 主要是通过[配置文件](https://logging.apache.org/log4j/2.x/manual/configuration.html)进行设定，
默认会在classpath目录下按照如下优先级（不考虑测试配置）寻找配置文件（**注意文件名中间的2**）：   
1. `log4j2.properties` > `log4j2.yaml`/`log4j2.yml` > `log4j2.json`/`log4j.jsn` > `log4j2.xml`
2. 如果都没有找到，则会按默认配置输出，也就是输出到控制台.   

注意，从 version 2.4 版本开始，log4j 才开始支持 `.properties` 格式，考虑到 log4j 的配置是层次化的，最推荐的方式是 xml 格式.

log4j-2.x 的日志级别和优先级为：OFF > FATAL > ERROR > WARN> INFO > DEBUG > TRACE > ALL.   
打印日志时，会打印 >= 所设置级别的日志，设置的日志等级越高，打印出来的日志就越少。

示例参见 resources 目录下的 `log4j2.xml`.

---
## JCL(Commons Logging)



---
# Slf4j阵营

Slf4j 和 Logback 是同一个作者开发的，这两个组件一般一起使用，似乎没有单独使用 Logback 的教程——Logback官方文档也是配合Slf4j一起用，
因此这里就不分开介绍了。

官方文档:
+ [Slf4j](https://www.slf4j.org/index.html), 里面只有一个 [SLF4J user manual](https://www.slf4j.org/manual.html) 文档比较有用
+ [Logback](https://logback.qos.ch/index.html)


## Maven依赖
Logback分为 3 个依赖：
+ logback-core：核心依赖，它是其他两个依赖的基础
+ logback-classic：它实现了Slf4j的API，所以当想配合Slf4j使用时，需要将logback-classic加入classpath
+ logback-access：为了集成Servlet环境而准备的，可提供HTTP-access的日志接口

```xml
<!-- Slf4j依赖 -->
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-api</artifactId>
  <version>1.7.30</version>
  <scope>test</scope>
</dependency>
<!-- Logback依赖 -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-core</artifactId>
  <version>1.2.6</version>
</dependency>
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.2.6</version>
</dependency>
```

## 架构

Logback 整体是基于如下 3 个组件（和Log4j-1.x一样）：
+ `Logger`：日志记录器，属于 logback-classic 依赖
+ `Appender`：日志输出目的地，属于 logback-core 依赖
  + 比如输出到控制台、文件、远程socket服务器、MySQL等
  + 一个`Logger`可以配置多个`Appender`
+ `Layout`：日志格式，属于 logback-core 依赖
  + `Layout`对象需要附加在某个`Appender`上，用于设置输出的日志格式

Logback中所有的`Logger`都是由一个`LoggerContext`对象（默认名称为`default`）来管理的，它以一个树形的层次结构来组织所有的`Logger`，最顶层的是`Root`Logger。

Logback配置文件名称是`logback.xml`，配置语法参考官方文档 [Chapter3: Logback configuration ->Configuration file syntax](https://logback.qos.ch/manual/configuration.html#syntax)。    
有如下配置节点：
+ 根节点`<configuration>`
  + `<contextName>`节点：应用上下文名称，可选
  + `<root>`节点：最顶层的Logger（名称就是`ROOT`），单独一个节点，只能有一个
  + `<logger>`节点：具体的Logger，定义日志从哪里获取以及级别，可以有多个
  + `<appender>`节点：配置日志格式、如何过滤、文件处理，可以有多个，它内部有如下节点：
    + `<encoder>`节点：负责转换日志事件为字节数组并将字节数组写入到输出流中，class属性设置输出格式的全限定类名
    + `<layout>`节点：配置日志输出形式的类，class属性设置输出格式的全限定类名，详细的配置格式可以参考官方文档 [Chapter 6: Layouts](https://logback.qos.ch/manual/layouts.html)
    + `<filter>`节点
  + `<property>`节点：定义常用变量，可选

> Appender中的`encoder`是在 0.9.19 版本引入的，它的引入是因为 layout 只能将日志事件转换为成 string，
> 而且在日志事件写出时不能进行更细致的控制，不能将日志事件批量聚合。    
> 与之相反的是，encoder 不但可以完全控制字节写出时的格式，而且还可以控制这些字节什么时候被写出。

配置文件示例参见 resources 目录下的 `logback.xml`。

------

# 常用工具日志集成

+ Spark 2.x 使用的是 Slf4j，不过后端搭配的是 Log4j 1.x（最后一个版本1.2.17），见 `org.apache.spark.internal.Logging.scala` 源码。
