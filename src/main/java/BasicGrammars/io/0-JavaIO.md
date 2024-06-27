[TOC]

# 文件与路径表示
java里对文件和路径的操作有如下选择：
1. `java.io.File`类，同时表示文件/目录，它**只用于表示文件（目录）的信息（名称、大小等），不能用于文件内容的访问**
2. `java.nio.file.Path`类，也同时表示文件/目录
3. **`java.nio.file.Files` 类封装了一系列操作文件内容的静态方法**

`nio`中的`Path`（配合`Paths`）相当于`io`中的`File`类，而`nio.Files`类在此之上还提供更多功能的封装，例如下面的方法：
+ `Files.readAllBytes()`
+ `Files.readAllLines()`
+ `Files.readAllBytes()` 
+ `Files.readAllLines()` 
+ `Files.createFile()` 
+ `Files.createDirectory()` 
+ `Files.createDirectory()`

`java.io`是jdk 1.0就存在的API，`java.nio`中的`nio`是 *new input output* 的缩写，是jdk 1.5 引入的新API。   
`nio` 相比于 `io`，主要有下面几个特点：
1. `io`主要是面向 *流(Stream)*的读写，`nio`主要是面向 *缓冲(Buffer)* 的读写，可以使用零拷贝的方式，效率更高
2. `io`主要是阻塞式IO，`nio`支持非阻塞式IO——Netty底层就是使用的NIO

-----------------------------------------

# 字节流读写

两个抽象基类：InputStream、OutputStream
+ `InputStream`抽象了应用程序读取数据的方式，它**表示一个可以从中读入字节序列（到用户程序）的对象**   
  输入流基本方法
  + `read()`: 从**流对象**中读取一个字节无符号填充到int低八位. 读到 EOF 时返回 -1。
  + `read(byte[] buf)`: 读取的字节直接填充到字节数组，并返回实际读取的字节数；读到 EOF 返回 -1；最多读取 buf.length 个字节
  + `read(byte[] buf, int start, int size)`: 读取的字节直接填充到字节数组的 start 位置开始
  + `int available()`: 返回非阻塞情况下可读取的字节数

+ `OutputStream`抽象了应用程序写出数据的方式，它**表示一个可以写入（来自用户程序）字节序列的对象**
  输出流基本方法
  + `write(int b)`: 写出一个byte到流，b的低8位
  + `write(byte[] buf)`: 将buf字节数组都写入到流
  + `write(byte[] buf,int start,int size)`

其他方法：
+ `flush()`输出流刷新缓存区，写出
+ `close()`关闭输入/输出流，释放资源

> 注意：
> 1. 上述的 `InputStream.read()` 和 `OutputStream.write()` 方法都是阻塞的，直至字节序列被读入/写出。
> 2. 上述的输入输出流也适用于网络编程的Socket对象

常用的输入/输出流实现类：
+ `FileInputStream`: 实现了在文件上读取数据
+ `FileOutputStream`: 实现了向文件中写出byte数据的方法
+ `DataInputStream`/`DataOutputStream`: 对"流"功能的扩展，可以更加方面的读取int,long，字符等类型数据
+ `BufferedInputStream`/`BufferedOutputStream`: 这两个IO类提供了带缓冲区的操作，一般打开文件进行写入或读取操作时，都会加上缓冲，这种流模式提高了IO的性能
+ `RandomAccessFile`: 支持随机访问文件，可以访问文件的任意位置，既可以读文件，也可以写文件。

从应用程序中把输入放入文件，相当于将一缸水倒入到另一个缸中:
`FileOutputStream.write()`方法相当于一滴一滴地把水“转移”过去
`DataOutputStream.writeXxx()`方法会方便一些，相当于一瓢一瓢把水“转移”过去
`BufferedOutputStream.write()`方法更方便，相当于一飘一瓢先放入桶中，再从桶中倒入到另一个缸中，性能提高了

注意：**文件读写完成以后一定要关闭（Oracle官方说明）**。

-----------------------------------------

# 字符流读写
文本和文本文件   
+ java的文本(char)是16位无符号整数，是字符的unicode编码（双字节编码)
+ 文件是byte byte byte ...的数据序列 
+ 文本文件是文本(char)序列按照某种编码方案(utf-8,utf-16be,gbk)序列化为byte的存储结果

字符流(Reader Writer)操作的是文本文本文件，一次处理一个字符，字符的底层仍然是基本的字节序列，基本抽象类如下：
+ `Reader`
+ `Writer`

字符流的基本实现
+ `InputStreamReader`，完成byte流解析为char流,按照编码解析
+ `OutputStreamWriter`，提供char流到byte流，按照编码处理
+ `FileReader`，它是 `InputStreamReader` 的子类
+ `FileWriter`，它是 `OutputStreamWriter` 的子类
+ `BufferedReader`，它的`readLine()`方法一次读一行
+ `BufferedWriter`，其中的`write()`一次写一行

-----------------------------------------

# 序列化/反序列化

序列化与基本类型序列化
1）将类型 int 转换成 4 byte 或将其他数据类型转换成 byte 的过程叫序列化
数据---->n byte
2)反序列化
将 n个byte 转换成一个数据的过程
nbyte ---> 数据
3)RandomAccessFile提供基本类型的读写方法，可以将基本类型数据
序列化到文件或者将文件内容反序列化为数据

3.对象的序列化，反序列化
1)对象序列化，就是将Object转换成byte序列，反之叫对象的反序列化
2)序列化流(ObjectOutputStream),是过滤流----writeObject
反序列化流(ObjectInputStream)---readObject

3)序列化接口(Serializable)
对象必须实现序列化接口 ，才能进行序列化，否则将出现异常
这个接口，没有任何方法，只是一个标准

4) transient关键字
   private void writeObject(java.io.ObjectOutputStream s)
   throws java.io.IOException
   private void readObject(java.io.ObjectInputStream s)
   throws java.io.IOException, ClassNotFoundException

   分析ArrayList源码中序列化和反序列化的问题

5)序列化中 子类和父类构造函数的调用问题

