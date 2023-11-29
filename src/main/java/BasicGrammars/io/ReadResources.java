package BasicGrammars.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Objects;
import java.net.URI;
import java.net.URL;

/**
 * 演示如何跨平台读取 resources 目录下的资源
 */
public class ReadResources {
    public static void main(String[] args) throws IOException {
        // 使用 src 的方式，只能在本地调试时使用，因为此时src的上级目录是当前目录——项目所在目录
        Path path1 = Paths.get("src", "main", "resources", "java_io", "stream_in.txt");
        // 打成 jar 包之后，部署到Linux上时，这种方式就访问不到了
        System.out.println("path1.exists: " + Files.exists(path1));
        System.out.println("-----------------------------------------------");

        // 考虑使用 class.getResource() 方法和 classLoader.getResource() 方法，这两个方法返回的是一个 URL 对象
        URL resource = ReadResources.class.getResource("");
        // 以当前类的.class文件所在包目录为起点
        String p1 = ReadResources.class.getResource("").toString();
        // 以项目的根路径为起点，也就是class path根目录
        String p2 = ReadResources.class.getResource("/").toString();
        // 通过类加载器获取资源目录，空字符串表示从 class path 目录开始获取资源
        String p3 = ReadResources.class.getClassLoader().getResource("").toString();
        // 类加载器中的路径不能使用 / ，会有空指针异常
        //String p4 = ReadResources.class.getClassLoader().getResource("/").toString();
        System.out.println("p1: " + p1);
        System.out.println("p2: " + p2);
        System.out.println("p3: " + p3);
        // getPath 方法拿到的路径表示是 Linux 下的，路径分割符为 /，不适用于Windows平台
        String resources2 = Objects.requireNonNull(ReadResources.class.getClassLoader().getResource("")).getPath();
        System.out.println("resources2: " + resources2);
        System.out.println("-----------------------------------------------");

        // 跨平台访问resources目录的方法
        String resources = Objects.requireNonNull(ReadResources.class.getClassLoader().getResource("")).toString();
        // 如果类的实例方法，则可以使用 this 来获取类加载器，这里由于是在 main 方法中，就必须使用类名来访问
        //String resources = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).toString();
        // 上面拿到的 resources 目录的路径是一个 URI，格式为 file:/path/to/file，其中的 / 是固定的表示，不是目录分隔符的意思
        System.out.println("resources: " + resources);
        // 通过 URI 再转成 Path 对象
        URI resources_uri = URI.create(resources);
        Path resources_dir = Paths.get(resources_uri);
        System.out.println("resources_dir: " + resources_dir);
        System.out.println("resources_dir.exists: " + Files.exists(resources_dir));
        System.out.println("-----------------------------------------------");

        // ------------- 上面的方式，不适用于打成 jar 包之后的访问 --------------
        // 打成jar包后，好像只能通过 getResourceAsStream() 方法直接访问
        InputStream resourceStream = ReadResources.class.getResourceAsStream("/java_io/reader_in.txt");
        assert resourceStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
        String line = null;
        while ((line = reader.readLine()) != null){
            System.out.println(line);
        }
        reader.close();

    }
}
