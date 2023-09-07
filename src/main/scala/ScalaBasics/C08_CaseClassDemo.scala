package ScalaBasics

/**
 * Scala 中的样例类是以 case class 关键字定义的类，后面的参数列表可选，它和普通的类有如下的几点差别：
 *  1. 样例类会将 参数列表 里的参数定义为 公开的 val，也可以显示申明为 var，不过不推荐这样做
 *  2. 自动生成 toString, equals, hashCode 和 copy 方法
 *  3. 样例类会自动生成一个默认的 apply 方法来创建对象，省去了 new 的使用
 *  4. 会自动生成 unapply 方法
 */

// 创建一个样例类
// 查看编译后的Java源码可以发现，创建样例类后会自动生成apply，unapply等方法
case class Point(x: Int, y: Int)

// 样例对象：没有构造参数
case object Message

// 样例类通常和模式匹配一起使用
// 首先定义一个抽象类，用于多态引用
abstract class Notification
// 使用样例类继承抽象类
case class Email(sender:String, title:String, body:String) extends Notification
case class SMS(caller:String, message:String) extends Notification

object TestCaseClassDemo extends App {
  //通常方式创建样例类的对象
  val obj1 = new Point(1, 2)
  //样例类可以不使用 new 关键字——这也是样例类实例对象的常见创建方式
  val obj2 = Point(3, 4)
  //使用样例类
  println(obj1.x)
  println(obj1.y)

  //使用样例对象
  val obj3 = Message
  //不能使用new创建
  //val obj4 = new Message
  //样例对象也会自动生成一系列方法，但是没有apply和unapply方法
  println(obj3.toString)

  //模式匹配的应用
  val emailMsg = Email("a", "b", "body")
  showNotification(emailMsg)

  //定义一个函数，用于模式匹配，这里使用 抽象类 实现了 多态引用
  def showNotification(notification: Notification): Unit = {
    notification match {
      case Email(sender, title, _) => println(sender)
      case SMS(caller, message) => println(caller)
      case _ => println("nothing")
    }
  }

}
