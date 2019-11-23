package qianfeng

// apply方法是应用于伴生对象上的，需要有一个伴生对象
class ApplyDemo(val name:String,val age:Int,val country:String = "China"){

}

object ApplyDemo {
//  apply方法用作工厂方法，用来创建伴生类的对象
//  这个方法是一个注入方法，可以用于初始化工作
  def apply(name: String, age: Int, country: String): ApplyDemo = new ApplyDemo(name, age, country)

//  unapply方法，提取方法，用作模式匹配中，用来提取固定数量的对象或者数值
//  返回类型是option对象，如果有值，返回Some，否则就是None
//  通常是由系统调用的，不需要我们主动调用
//  传入的参数是一个 ApplyDemo 的对象
  def unapply(obj: ApplyDemo): Option[(String, Int, String)] = {
    if (obj == null)
      None
    else
    {
      println("unapply is called")
      Some(obj.name, obj.age, obj.country)
    }
  }
}

object TestApply{
  def main(args: Array[String]): Unit = {
//    传统的创建类的方法
    val obj = new ApplyDemo("tom",20)
//    使用apply方法，省略了new
    val obj2 = ApplyDemo("Alley",21,"China")

//    使用unapply方法
    val obj3 = ApplyDemo("Nicholas",21,"China")
//    模式匹配，用于提取信息
    obj3 match {
      case ApplyDemo(name, age, country) => println(name,age+"years old",country)
//    没有匹配则返回nothing
      case _ => "nothing"
    }

  }
}
