package qianfeng

// 演示作为借口使用的特质
trait Logger{
  def log(msg:String)
}

// 实现接口
// 第一个接口使用Extends，第二个使用with
class ConsoleLogger extends Logger{

//  继承接口后，必须要重写接口里的抽象方法
  override def log(msg: String): Unit = {
    println(msg)
  }
}

object InterfaceDemo {

  def main(args: Array[String]): Unit = {
    val obj = new ConsoleLogger
    obj.log("Interface trait")
  }

}
