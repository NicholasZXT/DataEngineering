package ScalaBasics

class StudentClass {
  //定义属性，同时初始化
  var name = "tom"
  //定义属性的同时使用占位符，指定属性的类型
  var nickName:String = _

  val age = 20
  //val修饰的属性是不可以使用占位符
  //val age1:Int = _

  //类的私有属性
  private var hobby:String = _

  //对象私有属性
  private [this] var card:String = _

  // 上面这些属性可以编译完之后，看看编译成的Java代码是什么
}

object test{
  def main(args: Array[String]): Unit = {
    //创建一个对象
    val obj = new  StudentClass
    //访问属性name的get方法
    printf(obj.name)
    //访问属性name的set方法
    obj.name="jerry"
    printf(obj.name)
    //obj.age = 20
    // ——注意age是Int类型，要转成String
    printf(obj.age+" ")

    // ——下面这两个是私有属性，无法从外部访问
    //obj.card
    //obj.hobby
  }
}
