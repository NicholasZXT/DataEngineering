package ScalaBasics

import math.Pi

/**
  模式匹配语法类似于Java中的 switch case
  变量 match {
    case 1 => expr
    case 2 => expr
    case _ => expr
  }
 */
object C09_PatternMatchDemo {
  def main(args: Array[String]): Unit = {
    println(cons_match(5))
    println(cons_match(true))
    println(cons_match((10.9)))
    println("--------------------------")

    println(var_match(Pi))
    println(var_match(3.14))
    println("--------------------------")

    println(str_match("str"))
    println(str_match("int"))
    println(str_match("abc"))
    println("--------------------------")

    println(constructor_match(FlyDuck("fly_duck", "fly")))
    println(constructor_match(GroundDuck("ground_duck", "run")))
    println(constructor_match("Non-Duck"))

    val obj = Message2("tom", "hello")
    println(showNotification2(obj))
  }

  //常量模式
  def cons_match(x:Any)= x match {
    case 5 => "five"
    case true => "truth"
    case Nil => "empty list"
    case _ => "something else"
  }

  //字符串模式，也相当于常量模式
  def str_match(str:String)= str match {
    case "str" => "String"
    case "int" => "Int"
    case "double" => "Double"
    case _ => "other"
  }

  //变量模式
  //变量可以匹配任意对象，把变量绑定在匹配的对象上，实际上仅仅是把传入的对象赋给了相应的变量，并没有匹配判断过程
  def var_match(x:Any)= x match {
    case Pi =>"Pi value"
    case pi => println(pi); "pi value"
    case _ => "default"
  }

  // 构造方法模式 —— 这个才是模式匹配真正发挥威力的地方
  // 通常和样例类配合使用，名字指定的是一个样例类：1. 首先检查被匹配的对象是否对应的样例类；2. 然后检查被匹配对象的构造方法参数是否匹配
  def constructor_match(x: Any) = x match {
    case FlyDuck(_, _) => "A flying duck"
    case GroundDuck(_, _) => "A running duck"
    case _ => "nothing"
  }
  def showNotification2(notification:Notification2)= {
    notification match {
      case Email2 (sender,title,_) => s"email:$sender title:$title"
      case Message2(sender,_) => s"email:$sender title: none"
      case Message2(sender,_) => s"email:$sender title: none"
      // 带守卫的模式匹配
      case Email2 (sender,title,_) if sender.contains("tom") => s"email:$sender title:$title"
      case _=>"nothing"
    }
  }

  // 序列和元组的匹配，类型匹配，模式守卫
  def seq_mathc(x: Any): String = x match {
    case List(0, _*) => "sequence start with 0"
    case (a, b, c) => "tuple with length 3"
    // 带有模式守卫——也就是if语句
    case s: String if s.startsWith("string") => "String class with 'string' start"
    case _ => "nothing"
  }
}

abstract class Duck
case class FlyDuck(name: String, flyable: String) extends Duck
case class GroundDuck(name: String, runnable: String) extends Duck

//样例类的模式匹配
abstract class Notification2
case class Email2(sender:String, title:String, body:String) extends Notification2
case class Message2(sender:String, message:String) extends Notification2