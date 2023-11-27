package BasicGrammars.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class P5_DataInputOutputDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
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
