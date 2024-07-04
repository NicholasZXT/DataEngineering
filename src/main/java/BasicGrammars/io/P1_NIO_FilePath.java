package BasicGrammars.io;

import java.nio.file.Path;  // 这个是接口
import java.nio.file.Paths;
import java.nio.file.Files;

public class P1_NIO_FilePath {
    public static void main(String[] args) {
        // Path 是一个接口，不能直接构造对象，需要通过 Paths 类提供的静态方法来创建 Path 接口实例
        // 可以传入路径的多个部分，自动用当前平台的路径分隔符连接
        // 相对路径
        Path path1 = Paths.get("parent_dir", "test");
        // 绝对路径
        Path path2 = Paths.get("D:", "parent_dir", "test.txt");
        System.out.println("path1: " + path1);
        System.out.println("path1.normalize: " + path1.normalize());
        System.out.println("path2: " + path2);
        System.out.println("path2.normalize: " + path2.normalize());
        System.out.println(path1.isAbsolute());
        System.out.println(path2.isAbsolute());
        System.out.println(path2.getRoot());

        Path path_in = Paths.get("src","main", "resources", "hadoop_data", "wordcount_input", "wordcount.txt");

        System.out.println("------------------------------");
        // Files类不需要使用构造方法来创建对象，它只是封装了一些操作文件/目录的静态方法
        boolean flag = Files.isRegularFile(path1);
        System.out.println(Files.exists(path2));
        // 其他的一些方法如下：
        //Files.readAllBytes();
        //Files.readAllLines();
        //Files.createFile();
        //Files.createDirectory();
        //Files.createDirectory();
        //Files.write();
        //Files.copy();
        //Files.delete();
        //Files.deleteIfExists();
    }
}
