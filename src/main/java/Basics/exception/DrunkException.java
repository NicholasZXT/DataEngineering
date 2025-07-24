package Basics.exception;

/**
 * 自定义异常类型，只需要派生于 Exception 或者 Exception 的子类.
 * 习惯上，异常类型应该有两个构造器，一个默认构造器，另一个是带有详细信息的构造器
 */
public class DrunkException extends Exception {

	// 默认构造器
	public DrunkException(){
	}

	// 带有异常描述信息的构造器
	public DrunkException(String message){
		super(message);
	}
}
