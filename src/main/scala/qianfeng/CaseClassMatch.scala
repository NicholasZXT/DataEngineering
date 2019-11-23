package qianfeng

//样例类的模式匹配
abstract class Notification2
case class Email2(sender:String,title:String,body:String) extends Notification2
case class Message2(sender:String,message:String) extends Notification2

object CaseClassMatch {

  def showNotification2(notification:Notification2)={
    notification match {
      case Email2 (sender,title,_) => s"email:$sender title:$title"
      case Message2(sender,_) => s"email:$sender title:"
      case Message2(sender,_) => s"email:$sender title:"
//    带守卫的模式匹配
      case Email2 (sender,title,_) if sender.contains("tom") => s"email:$sender title:$title"
      case _=>"nothing"
    }
  }

  val obj = new Message2("tom","hello")
  println(showNotification2(obj))

}
