package ScalaBasics

/**
 * apply 方法是 scala 中的一个特殊方法，它常常用于简化 new 的使用.
 * apply 方法一般会定义在伴生对象里，当使用形如 Object(arg1, arg2, ...) 这种函数方式调用对象时，scala会查找对应参数签名的 apply 方法，
 * 并进行隐式调用 —— 类似于 python 类中的 __call__ 方法
 */

// 定义一个伴生类，有一个私有无参主构造方法 和 一个公有辅助构造方法
class ApplyDemo private (){
  var name: String = _
  var age: Int = _
  var country: String = "China"
  def this(name: String, age: Int, country: String){
    this()
    this.name = name
    this.age = age
    this.country = country
  }
}
// 在伴生对象里定义 apply 方法
object ApplyDemo {
  //  apply方法用作工厂方法，用来创建伴生类的对象。这个方法是一个注入方法，可以用于初始化工作
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

object TestApplyDemo{
  def main(args: Array[String]): Unit = {
    // 传统的创建类的方法，使用 new
    val obj = new ApplyDemo("tom",20, "China")
    // 使用apply方法，省略了new
    val obj2 = ApplyDemo("Alley", 21, "China")

    // 使用unapply方法
    val obj3 = ApplyDemo("Nicholas", 21, "China")
    // 模式匹配，用于提取信息
    obj3 match {
      case ApplyDemo(name, age, country) => println(name,age+"years old",country)
      // 没有匹配则返回nothing
      case _ => "nothing"
    }
  }
}
