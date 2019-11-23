package qianfeng

//密封类使用sealed关键字修饰
//要求子类和父类都在同一个源文件中定义
//经常和模式匹配一起使用
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
