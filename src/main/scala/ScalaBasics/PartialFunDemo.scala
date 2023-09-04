package ScalaBasics

// 普通函数，比如 (x:Int)=>x+1 会对Int类型所有的值生效
// 偏函数就是指只对某个类型的部分值生效的函数
// 偏函数的类型是PartialFunction[A,B]，A表示参数类型，B表示返回值类型
// 函数体是一组case语句

object PartialFunDemo {

  def partialFun:PartialFunction[String,Int]={
    case "one" => 1
    case "two" => 2
    case "three" => 3
//  一般不使用下面这句处理未匹配的情况
//    case _ => 0
  }

//  偏函数的orelse方法，组合多个偏函数
  val onePF:PartialFunction[String,Int]={
    case "one" => 1
  }
  val twoPF:PartialFunction[String,Int]={
    case "two" => 1
  }
  val threePF:PartialFunction[String,Int]={
    case "three" => 1
  }
//组合上面三个偏函数
  val newPF = onePF.orElse(twoPF).orElse(threePF)

//  andThen 方法，多个偏函数的连缀调用
  val thenPF:PartialFunction[Int,String] = {
    case 1 => "one"
  }

  val newPF2 = onePF.andThen(thenPF)


  def main(args: Array[String]): Unit = {
    println(partialFun("one"))
//    下面这句会报错
    println(partialFun("four"))
//    可以先判断偏函数是否在这个参数上有定义
    if (partialFun.isDefinedAt("four")) {
      println(partialFun("four"))
    }

//    使用组合后的偏函数
    println(newPF("three"))

    println(newPF2("one"))

//  applyOrElse
//  如果没有定义four，就返回none
//    val res = onePF.applyOrElse("four","none")
//    println(res)

  }

}
