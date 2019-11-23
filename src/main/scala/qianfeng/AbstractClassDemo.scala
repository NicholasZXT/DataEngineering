package qianfeng

//定义一个抽象类
abstract class Person(){
//  抽象字段，没有初始化的属性
  val name:String
//  抽象方法：没有方法体
  def printName
//  具体字段
  val nickname = "Alley"
//  具体方法，有方法体
  def printNickname={
    println(nickname)
  }
}

// 使用抽象类
class Student extends Person{
//  实现抽象父类中的抽象字段和抽象方法
//  override 关键字可以省略
  override val name: String = "Daniel"

  override def printName: Unit = {
    println(name)
  }

  //  重写抽象父类的具体方法,override关键字不能省略
  override def printNickname: Unit = {
//    调用父类的具体方法
//    super.printNicknamel
    println("nickName:" + nickname)
  }

  //  重写抽象父类的具体字段
  override val nickname: String = "Louisa"
}

object AbstractClassDemo {

}
