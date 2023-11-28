package BasicGrammars.io;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Objects;
import java.net.URI;
/**
 * 演示如何跨平台读取 resources 目录下的资源
 */
public class ReadResources {
    public static void main(String[] args) {
        // 使用 src 的方式，只能在本地调试时使用，因为此时src的上级目录是当前目录——项目所在目录
        Path path1 = Paths.get("src", "main", "resources", "java_io", "stream_in.txt");
        // 打成 jar 包之后，部署到Linux上时，这种方式就访问不到了
        System.out.println("path1.exists: " + Files.exists(path1));

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

        // getPath 方法拿到的路径表示是 Linux 下的，路径分割符为 /，不适用于Windows平台
        String resources2 = Objects.requireNonNull(ReadResources.class.getClassLoader().getResource("")).getPath();
        System.out.println("resources2: " + resources2);
    }
}
