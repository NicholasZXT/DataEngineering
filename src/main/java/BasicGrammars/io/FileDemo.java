package BasicGrammars.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class FileDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 了解构造函数的情况  查帮助
		File file = new File("E:\\javaio\\imooc");
		//System.out.println(file.exists());
		if(!file.exists())
			file.mkdir();
//		 多级目录使用下面这个
		//file.mkdirs()
		else
			file.delete();
		//是否是一个目录  如果是目录返回true,如果不是目录or目录不存在返回的是false
		System.out.println(file.isDirectory());
		//是否是一个文件
		System.out.println(file.isFile());
		
		//File file2 = new File("e:\\javaio\\日记1.txt");
		File file2 = new File("e:\\javaio","日记1.txt");
		if(!file2.exists())
			try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else 
			file2.delete();
         //常用的File对象的API
		System.out.println(file);//file.toString()的内容
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getName());
		System.out.println(file2.getName());
		System.out.println(file.getParent());
		System.out.println(file2.getParent());
		System.out.println(file.getParentFile().getAbsolutePath());

		File file3 = new File("e:\\example");
		/*String[] filenames = file.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				System.out.println(dir+"\\"+name);
				return name.endsWith("java");
			}
		});
		for (String string : filenames) {
			System.out.println(string);
		}*/
		/*File[] files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				System.out.println(dir+"\\"+name);

				return false;
			}
		});*/
		File[] files = file.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				System.out.println(pathname);

				return false;
			}
		});
	}

}
