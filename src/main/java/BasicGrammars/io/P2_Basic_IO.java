package BasicGrammars.io;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class P2_Basic_IO {
	public static void main(String[] args) throws IOException{
		FileReader fr = new FileReader("e:\\javaio\\imooc.txt");
		FileWriter fw = new FileWriter("e:\\javaio\\imooc2.txt");
		//FileWriter fw = new FileWriter("e:\\javaio\\imooc2.txt",true);
		char[] buffer = new char[2056];
		int c ;
		while((c = fr.read(buffer,0,buffer.length))!=-1){
			fw.write(buffer,0,c);
			fw.flush();
		}
		fr.close();
		fw.close();

		//如果该文件不存在，则直接创建，如果存在，删除后创建
		FileOutputStream out = new FileOutputStream("demo/out.dat");
		out.write('A');//写出了'A'的低八位
		out.write('B');//写出了'B'的低八位
		int a = 10;//write只能写八位,那么写一个int需要些4次每次8位
		out.write(a >>> 24);
		out.write(a >>> 16);
		out.write(a >>> 8);
		out.write(a);
		byte[] gbk = "中国".getBytes("gbk");
		out.write(gbk);
		out.close();

		IOUtil.printHex("demo/out.dat");
	}

}
