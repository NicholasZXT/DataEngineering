# Introduction

参考博客：
+ [稀土掘金-深入掌握Java日志体系，再也不迷路了](https://juejin.cn/post/6905026199722917902)
+ [Java日志框架（JCL,SLF4J,Log4J,Logback）](https://whatsrtos.github.io/Java/JavaEE-Log/)
+ [知乎-带你深入Java Log框架，彻底搞懂Log4J、Log4J2、LogBack，SLF4J](https://zhuanlan.zhihu.com/p/615102614)

个人感觉Java的日志框架有那么一点混乱，这里根据上述博客的介绍，简单梳理一下。

按照时间顺序，各个日志组件如下：
1. Log4j 1.x，出现的最早，后面成为Apache项目
2. JUL(Java Util Logging)，jdk1.4 新增的日志库
3. JCL(Jakarta Commons Logging)，Apache推出，后面更名为 Commons Logging
4. Slf4j(Simple Logging Facade for JAVA) 和 Logback，不属于Apache基金会
5. Log4j 2.x，Apache推出的新版日志组件

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
+ JCL（Commons Logging） + Log4j 1.x/2.x：这一套都是Apache的组件，多用于Apache的开源项目中，比如大数据相关的组件，使用比较广泛
+ Slf4j + Logback：这一套是 Log4j 1.x 的作者离开Apache之后，另立门户开发的日志框架组合，使用的广泛程度有后来居上的趋势
+ Slf4j + Log4j 1.x/2.x：需要在中间加一个适配层，也就是 slf4j -> slf4j-log4j12-version.jar -> log4j，性能没那么好。
+ JCL + Logback：也需要在中间加一个适配层 jcl-over-slf4j.jar

jdk自带的JUL使用场景比较少，远不如上面两套日志框架广泛。

---

# Log4j 1.x


---

# Log4j 2.x

---

# Logback

---

# Slf4j


---
# JCL(Commons Logging)


