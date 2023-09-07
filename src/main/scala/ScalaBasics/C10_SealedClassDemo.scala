package ScalaBasics

/**
 * 密封类使用sealed关键字修饰，经常和模式匹配一起使用
 * 父类被定义为 密封类 后，子类 只能在 同一个源文件中定义，其他地方无法添加新的子类，这样保证模式匹配能覆盖所有的样例类
 */
sealed abstract class Amount
case class Dollar(value:String) extends Amount
case class Euro(value:String) extends Amount
case class RMB(value:String) extends Amount

object SealedClassDemo {

  def matchMoney(money: Amount)= money match {
    case Dollar(value) => println("Dollar"+value)
    case Euro(value) => println("Euro"+value)
    case RMB(value) => println("RMB"+value)
    case _ => println("nothing")
  }

  def main(args: Array[String]): Unit = {
    matchMoney(Dollar("100"))
  }

}
