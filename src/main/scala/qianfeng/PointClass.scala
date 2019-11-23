package qianfeng

class PointClass {
  //属性名要以“_”开始
  //属性要用private修饰——以避免系统自动生成get和set方法
  //get方法名 属性名一致
  //set方法名 属性名+"_"+=
  private var _x=0
  private var _y=0
  private  val bound = 100

  //定义属性的get set 方法
  def x=_x
  // ——set方法，注意这里方法是使用赋值的形式定义的
  def x_=(newvalue:Int):Unit = {
    if(newvalue < bound) _x = newvalue else println("out of bound,not allowed")
  }

}

object testPointClass{
  def main(args: Array[String]): Unit = {
    val obj = new PointClass
    obj.x
    obj.x_=(20)


  }
}