package ScalaBasics

//构造方法：主构造方法，辅助构造方法
//主构造方法会执行类定义中的所有语句
//var val 修饰的主构造方法的参数，会成为类的属性
// ——比如下面的定义这两个变量，在编译成Java代码后，name和age都会成为类的属性，并自动构造get和set方法
// ——第一版主构造方法，注意，参数列表前没有 private，参数使用了var或者val修饰
//class ConstructDemo (var name:String, val age:Int){
//  println("主构造方法")
//}

//没有var val 修饰的主构造方法的参数，不会成为类的属性
//主构造方法可以是私有的
// ——第二版主构造方法，注意，下面这个参数列表前有一个 private，参数没有用var或者val修饰
class ConstructDemo private(name:String,age:Int) {
  println("主构造方法")
  private var gender:Int = _
  private var facevalue:Double=_

  //辅助构造方法，首先辅助构造方法中，第一行代码一定要调用主构造方法或者其他辅助构造方法
  //辅助构造方法，方法名this
  def this(name:String,age:Int,gender:Int){
    this(name,age)
    this.gender = gender
  }

//  ——第二个辅助构造方法
  def this(name:String,age:Int,gender:Int,facevalue:Double){
//  ——这里第一行没有调用主构造方法，而是调用了第一个辅助构造方法
    //this(name,age)
    this(name,age,gender)
    this.facevalue = facevalue
  }

}

object testConstructDemo{
  def main(args: Array[String]): Unit = {
    val obj = new ConstructDemo("tom",20,0)
  }
}

