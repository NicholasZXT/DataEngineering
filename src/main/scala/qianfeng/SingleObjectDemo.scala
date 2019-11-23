package qianfeng

// 单例对象就是使用object修饰的部分，
// scala中使用单例对象实现Java中的静态类的功能，
// 单例对象实现了单例模式
object Logger2{
  var msg:String = "log"
  def getLog(msg:String)={
    println("log" + msg)
  }
}


object SingleObjectDemo {

  def main(args: Array[String]): Unit = {
//    使用单例对象
    println(Logger2.msg)
    Logger2.getLog("my log info")
  }


}
