package ScalaBasics

/**
 *
 */
// 使用 trait 关键字定义 特质
trait LoggerV1 {
  // 定义具体属性：含有初始化的属性
  var log_level: String = "INFO"
  // 定义抽象属性：没有初始化值
  // 特质中的抽象属性在具体的子类中必须被重写
  var log_name: String

  // 定义一个抽象方法：没有方法体，不需要 abstract 关键字
  def log(msg: String)

  // 也可以定义一个具体方法——有方法体
  def showLog(msg: String): Unit = {
    println("Log: " + msg)
  }
}

// 基类
class Account{
  protected var balance: Double = 0.0
}

// 作为接口使用的特质：第一个接口使用 extends 关键字，后续使用 with
class ConsoleLogger extends LoggerV1{
  // 必须重写特质里的抽象属性
  override var log_name: String = _

  // 继承接口后，必须要重写接口里的抽象方法 —— override 关键字可以不用写
  override
  def log(msg: String): Unit = {
    println(msg)
  }
}

// 作为多重继承代替使用的特质
class SavingAccount extends Account with LoggerV1{
  def this(balance: Double) = {
    this()
    this.balance = balance
  }

  // 必须重写特质里的抽象属性 + 抽象方法
  override var log_name: String = _
  override def log(msg: String): Unit = {
    println(msg)
  }

  def withdraw(amount: Double): Unit = {
    if (amount> balance)
      log("余额不足")
    else
      this.balance = this.balance - amount
  }
}

object TestTraitDemo extends App {
  // 作为接口使用的特质
  val obj = new ConsoleLogger
  obj.log("Interface trait")
}

