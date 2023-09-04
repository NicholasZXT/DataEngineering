package ScalaBasics

// 创建一个样例类
// 查看编译后的Java源码可以发现，创建样例类后会自动生成apply，unapply等方法
case class Point(x:Int, y:Int)

// 样例类通常和模式匹配一起使用
// 首先定义一个抽象类
abstract class Notification
// 使用样例类继承抽象类
case class Email(sender:String,title:String,body:String) extends Notification
case class SMS(caller:String,message:String) extends Notification

// 样例对象
// 样例对象一般没有构造参数
case object Message

object CaseClassDemo {
  def main(args: Array[String]): Unit = {
//    创建样例类的对象
    val obj1 = new Point(1,2)
//    样例类可以不使用 new 关键字
    val obj2 = Point(3,4)
//    使用样例类
    println(obj1.x)
    println(obj1.y)

//    使用样例对象
    val obj3 = Message
//    不能使用new创建
//    val obj4 = new Message
//    样例对象也会自动生成一系列方法，但是没有apply和unapply方法
    println(obj3.toString)


//    模式匹配的应用
    val emailMsg = Email("a","b","body")
    showNotification(emailMsg)

  }

//    定义一个函数，用于模式匹配
  def showNotification(notification: Notification)={
    notification match {
      case Email(sender, title, _) => println(sender)
      case SMS(caller, message) => println(caller)
      case _ => println("nothing")
    }
  }

}
