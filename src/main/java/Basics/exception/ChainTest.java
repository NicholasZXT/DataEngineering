package Basics.exception;

public class ChainTest {

	/**
	 * test1():抛出“喝大了”异常
	 * test2():调用test1(),捕获“喝大了”异常，并且包装成运行时异常，继续抛出
	 * main方法中，调用test2(),尝试捕获test2()方法抛出的异常
	 */
	public static void main(String[] args) {
		ChainTest ct = new ChainTest();
		try{
			ct.test2();
		}catch(Exception e){
			// 打印异常堆栈信息
			e.printStackTrace();
			//	java.lang.IllegalStateException: Can't overwrite cause with BasicGrammars.exception.DrunkException: 喝车别开酒！
			//		at java.lang.Throwable.initCause(Throwable.java:457)
			//		at BasicGrammars.exception.ChainTest.test2(ChainTest.java:40)
			//		at BasicGrammars.exception.ChainTest.main(ChainTest.java:15)
			//	Caused by: java.lang.RuntimeException: BasicGrammars.exception.DrunkException: 喝车别开酒！
			//		at BasicGrammars.exception.ChainTest.test2(ChainTest.java:38)
			//		... 1 more
			//	Caused by: BasicGrammars.exception.DrunkException: 喝车别开酒！
			//		at BasicGrammars.exception.ChainTest.test1(ChainTest.java:29)
			//		at BasicGrammars.exception.ChainTest.test2(ChainTest.java:35)
			//		... 1 more
			// 下面的分割符并不能起到预期的作用。。。
			System.out.println("--------------------------------------------");
			// 通过这种方式，拿到更原始的异常信息
			Throwable e_origin = e.getCause();
			e_origin.printStackTrace();
			//	java.lang.RuntimeException: BasicGrammars.exception.DrunkException: 喝车别开酒！
			//		at BasicGrammars.exception.ChainTest.test2(ChainTest.java:49)
			//		at BasicGrammars.exception.ChainTest.main(ChainTest.java:15)
			//	Caused by: BasicGrammars.exception.DrunkException: 喝车别开酒！
			//		at BasicGrammars.exception.ChainTest.test1(ChainTest.java:40)
			//		at BasicGrammars.exception.ChainTest.test2(ChainTest.java:46)
			//		... 1 more
		}
	}

	// 由于方法中抛出了受检异常，并且没有try/catch捕获，所以必须在方法头部申明该受检异常
	public void test1() throws DrunkException{
		throw new DrunkException("喝车别开酒！");
	}
	
	public void test2(){
		// test1 中声明的 DrunkException 已经被 try/catch 捕获了，就不需要在方法头部声明了
		try {
			test1();
		} catch (DrunkException e) {
			// 将该异常重新包装成 RuntimeException，并再次抛出
			RuntimeException newExc = new RuntimeException(e);
			// 下面这句将原始的 DrunkException 异常设置成了新的异常产生的原因，方便外部跟着异常发生的具体原因
			newExc.initCause(e);
			throw newExc;
		}
	}
}
