package Basics.io;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class P3_CharIO_Demo {
	public static void main(String[] args) throws IOException{
		Path path_in = Paths.get("src","main", "resources", "java_io", "reader_in.txt");
		Path path_out = Paths.get("src","main", "resources", "java_io", "reader_out.txt");

		//默认项目的编码,操作的时候，要写文件本身的编码格式
		// 根据文件的编码，每次读取一个字符，而不是读取一个byte，所以能正常读取显示中文
		InputStreamReader isr = new InputStreamReader(new FileInputStream(path_in.toFile()), StandardCharsets.UTF_8);
		int data = isr.read();
		while (data != -1){
			System.out.print((char) data);
			data = isr.read();
		}
		isr.close();

		//InputStreamReader isr = new InputStreamReader(new FileInputStream(path_in.toFile()), StandardCharsets.UTF_8);
		//OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path_out.toFile()), StandardCharsets.UTF_8);
		//char[] buffer = new char[8*1024];
		//int c;
		///*批量读取，放入buffer这个字符数组，从第0个位置开始放置，最多放buffer.length个
		//  返回的是读到的字符的个数
		//*/
		//while(( c = isr.read(buffer,0,buffer.length))!=-1){
		//	String s = new String(buffer,0,c);
		//	System.out.print(s);
		//	osw.write(buffer,0,c);
		//	osw.flush();
		//}
		//isr.close();
		//osw.close();

		Reader fr = new FileReader(path_in.toFile());
		Writer fw = new FileWriter(path_out.toFile());
		//Writer fw = new FileWriter(path_out.toFile(), true); // 追加写
		char[] buffer = new char[2056];
		int c = fr.read(buffer,0, buffer.length);
		while(c != -1){
			System.out.println(new String(buffer,0, c));
			fw.write(buffer,0, c);
			fw.flush();
			c = fr.read(buffer,0, buffer.length);
		}
		fr.close();
		fw.close();

		//IOUtil.printHex("demo/out.dat");

		//对文件进行读写操作
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path_in.toFile())));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path_out.toFile())));
		//PrintWriter pw = new PrintWriter(path_out.toFile());
		String line ;
		while((line = br.readLine()) != null){
			//一次读一行，结尾并不包括换行符
			System.out.print(line);
			//System.out.print("\n");
			bw.write(line);
			bw.newLine(); //单独写出换行操作
			bw.flush();
			//pw.println(line);
			//pw.flush();
		}
		br.close();
		bw.close();
		//pw.close();
	}

}
