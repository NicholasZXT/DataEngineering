package BasicGrammars.io;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Path;


public class P2_BytesIO_Demo {
	public static void main(String[] args)throws IOException {
		Path path_in = Paths.get("src","main", "resources", "java_io", "stream_in.txt");
		System.out.println("file_in path: " + path_in);
		File file_in = new File(path_in.toString());
		System.out.println("file_in.exists: " + file_in.exists());
		System.out.println("----------------------------------");

		// 下面能正常打印，是因为文件中的内容都是英文，英文中的每个字母用一个byte存储
		InputStream fileStream_in = new FileInputStream(file_in.toString());
		//InputStream fileStream_in = new FileInputStream(file_in);  // 也可以直接传入 File 对象
		// read 读入的是 int 格式的 byte，结束符为 -1
		int data = fileStream_in.read();
		while (data != -1){
			System.out.print((char) data);
			data = fileStream_in.read();
		}
		// 读完之后，一定要关闭流
		fileStream_in.close();

		// 下面不能正常打印，是因为文件中的内容有中文，中文每个字用两个byte存储
		System.out.println("\n----------------------------------");
		InputStream fileStream_in2 = new FileInputStream(Paths.get("src","main", "resources", "java_io", "reader_in.txt").toFile());
		data = fileStream_in2.read();
		while (data != -1){
			System.out.print((char) data);
			data = fileStream_in2.read();
		}
		// 读完之后，一定要关闭流
		fileStream_in2.close();

		System.out.println("\n----------------------------------");
		// Buffer传入的是 FileInputStream 对象，而不是文件路径
		InputStream fileBuffer_in = new BufferedInputStream(new FileInputStream(file_in));
		data = fileBuffer_in.read();
		while (data != -1){
			System.out.print((char) data);
			data = fileBuffer_in.read();
		}
		fileBuffer_in.close();

		//System.out.println("\n----------------------------------");
		////如果该文件不存在，则直接创建，如果存在，删除后创建
		//OutputStream out = new FileOutputStream("demo/out.dat");
		//out.write('A');//写出了'A'的低八位
		//out.write('B');//写出了'B'的低八位
		//int a = 10;//write只能写八位,那么写一个int需要些4次每次8位
		//out.write(a >>> 24);
		//out.write(a >>> 16);
		//out.write(a >>> 8);
		//out.write(a);
		//byte[] gbk = "中国".getBytes("gbk");
		//out.write(gbk);
		//out.close();

		System.out.println("\n----------------------------------");
		String file = "demo/dos.dat";
		IOUtil.printHex(file);
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		int i = dis.readInt();
		System.out.println(i);
		i = dis.readInt();
		System.out.println(i);
		long l = dis.readLong();
		System.out.println(l);
		double d = dis.readDouble();
		System.out.println(d);
		String s = dis.readUTF();
		System.out.println(s);
		dis.close();

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.writeInt(10);
		dos.writeInt(-10);
		dos.writeLong(10l);
		dos.writeDouble(10.5);
		//采用utf-8编码写出
		dos.writeUTF("中国");
		//采用utf-16be编码写出
		dos.writeChars("中国");
		dos.close();
		IOUtil.printHex(file);

	}

}
