package qianfeng

//首先要有一个抽象类，匿名类就是抽象类的对象
abstract class Monster{
  var name:String
  def skill
}

object NclassDemo {

  def main(args: Array[String]): Unit = {

//    直接创建匿名类的一个对象
    val obj = new Monster {
      override var name: String = "Kaijia"

      override def skill: Unit = {
        println("Eating")
      }
    }
  }

}
