package Basics.io;

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
        System.out.println("path1.isAbsolute(): " + path1.isAbsolute());
        System.out.println("path1.normalize: " + path1.normalize());
        System.out.println("path2: " + path2);
        System.out.println("path2.isAbsolute(): " + path2.isAbsolute());
        System.out.println("path2.normalize: " + path2.normalize());
        System.out.println("path2.getRoot(): " +path2.getRoot());

        System.out.println("------------------------------");
        Path path_in = Paths.get("src","main", "resources", "hadoop_data", "wordcount_input", "wordcount.txt");
        System.out.println("path_in: " + path_in);
        // 路径拼接使用 Path 接口的 resolve 方法
		Path current_dir = Paths.get(System.getProperty("user.dir"));
        System.out.println("current_dir: " + current_dir);
        Path path_join = current_dir.resolve(path_in);
        System.out.println("path_join: " + path_join);
        System.out.println("path_join.getParent(): " + path_join.getParent());
        System.out.println("path_join.getFileName(): " + path_join.getFileName());
        System.out.println("path_join.startsWith(current_dir): " + path_join.startsWith(current_dir));
        System.out.println("path_join.endsWith(path_in): " + path_join.endsWith(path_in));
        // 注意下面这两个的返回
        System.out.println("path_join.endsWith('.txt'): " + path_join.endsWith(".txt"));    // 这个是 false
        System.out.println("path_join.endsWith('wordcount.txt'): " + path_join.endsWith("wordcount.txt"));  // true
        // 检查路径是否存在
        System.out.println("path_join.exists: " + Files.exists(path_join));

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
