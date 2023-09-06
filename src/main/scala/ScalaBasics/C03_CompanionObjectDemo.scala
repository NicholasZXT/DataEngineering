package ScalaBasics

/**
 * 伴生对象首先是一个单例对象
 * 同一个源文件中如果有和单例对象同名的类，那么这个单例对象就变成了该类的伴生对象
 * 同时这个类也叫伴生对象的伴生类
 * 伴生对象和伴生类可以相互访问对方的属性和方法，包括私有属性和方法
 */
object C03_CompanionObjectDemo {
  private val name:String = "companion name"
  private def getName={
    println(name)
  }

  def main(args: Array[String]): Unit = {
//    伴生对象访问伴生类的私有属性和方法
//    但是是通过建立对象访问
    val obj = new C03_CompanionObjectDemo
    obj.name
    obj.getName
  }

}

class C03_CompanionObjectDemo {
  private val name = "companion class name"
  private def getName={
    println(name)
  }
// 访问伴生对象的私有属性和方法
  def getMsg={
    C03_CompanionObjectDemo.name
    C03_CompanionObjectDemo.getName
  }

}