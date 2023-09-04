package ScalaBasics
/*
模式匹配语法类似于Java中的switch case
变量 match{
case 1=> expr
case 2 => expr
case _ => expr
}
 */

object MatchDemo {
  //常量模式
  def desc(x:Any)=x match {
    case 5 =>"five"
    case true =>"truth"
    case Nil =>"empty list"
    case _=>"something else"
  }

  //变量模式
  //变量可以匹配任意对象，把变量绑定在匹配的对象上
  //仅仅是把传入的对象赋给了相应的变量，并没有匹配判断过程
  import math.Pi
  def varexample(x:Any)=x match {
  //  case Pi =>"Pi value"
    case pi =>println(pi);"pi value"
    case _ => "default"
  }

  //字符串模式
  def strDemo(str:String)= str match {
    case "str"=>"String"
    case "int"=>"Int"
    case "double"=>"Double"
    case _=>"other"
  }

  def main(args: Array[String]): Unit = {
//    println(desc(5))
//    println(desc(true))
//    println(desc((10.9)))
//    println(varexample(Pi))
//    println(varexample(3.14))
    println(strDemo("str"))
    println(strDemo("int"))
    println(strDemo("abc"))
  }

}
