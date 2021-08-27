package BasicGrammars.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class IO_01_FileDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// 构造一个 File 对象时，传入文件路径即可：可以是绝对路径（以根目录开头的完整路径），也可以是相对路径
		// windows平台下，使用 \ 作为分隔符，必须多用一个转义符，也就是 \\，linux下使用 / 作为分隔符
		File file1 = new File("D:\\test.txt"); // windows路径
		//File file1 = new File("/home/test.txt");      // linux下
		File file2 = new File("test.txt");    // 相对路径
		System.out.println("File1:");
		System.out.println(file1.exists());
		System.out.println(file1.getPath());
		System.out.println(file1.getAbsolutePath());
		System.out.println(file1.getCanonicalPath());
		System.out.println("File2:");
		System.out.println(file2.exists());
		System.out.println(file2.getPath());
		System.out.println(file2.getAbsolutePath());
		System.out.println(file2.getCanonicalPath());

		if(!file2.exists())
			file2.mkdir();
		 //多级目录使用下面这个
		//file.mkdirs()
		else
			file2.delete();

		//是否是一个目录  如果是目录返回true,如果不是目录or目录不存在返回的是false
		System.out.println(file2.isDirectory());
		//是否是一个文件
		System.out.println(file2.isFile());

		if(!file2.exists())
			try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			file2.delete();

		//常用的File对象的API
		System.out.println(file2.getName());
		System.out.println(file2.getParent());

		// FileFilter 的使用
		//File file3 = new File("e:\\example");
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
		//File[] files = file.listFiles(new FileFilter() {
		//	public boolean accept(File pathname) {
		//		System.out.println(pathname);
		//		return false;
		//	}
		//});
	}

}
