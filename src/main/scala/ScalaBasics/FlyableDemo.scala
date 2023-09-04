package ScalaBasics

trait FlyableDemo {
//  特质，定义属性和方法
//  定义具体属性：含有初始化的属性
//  定义抽象属性：没有初始化值
  val distance:Int = 1000
  val hight:Int

//  定义具体方法：带有具体实现的方法
  def fly={
    println("I can fly")
  }
//  定义抽象方法：没有具体实现的方法
  def flight:String

}
