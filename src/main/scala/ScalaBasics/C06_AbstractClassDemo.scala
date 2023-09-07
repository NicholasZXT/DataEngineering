package ScalaBasics

// 使用 abstract 关键字 定义一个抽象类
abstract class PersonV2(){
  // 抽象字段，没有初始化的属性
  var name: String
  // 抽象方法：没有方法体 —— 不需要，也不能使用 abstract 关键字
  def printName: Unit
  // 抽象类可以有具体字段
  val nickname: String = "nickname"
  // 也可以有具体方法，有方法体
  def printNickname={
    println(nickname)
  }
}

// 使用抽象类
class StudentV5 extends PersonV2{
  //实现抽象父类中的抽象字段和抽象方法
  //override 关键字可以省略
  override var name: String = "Daniel"
  override def printName: Unit = {
    println(name)
  }

  //  重写抽象父类的具体字段
  override val nickname: String = "Louisa"

  //重写抽象父类的具体方法，此时 override关键字 不能省略
  override def printNickname: Unit = {
    //调用父类的具体方法
    //super.printNicknamel
    println("nickName:" + nickname)
  }

}

object TestAbstractClassDemo {

}
