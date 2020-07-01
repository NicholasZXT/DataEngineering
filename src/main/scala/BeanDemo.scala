
import scala.beans.BeanProperty

class BeanDemo{

//  name是普通的变量
  var name:String = ""

//  name_bean是通过注解生成的JavaBean变量
  @BeanProperty
  var name_bean:String = ""
}

object BeanDemo {

  def main(args: Array[String]): Unit = {
    val bean = new BeanDemo()

//    name只能通过属性的方式设置和取值
    bean.name = "name"
    println(bean.name)

//    name_bean会自动生成下面的set和get方法
    bean.setName_bean("name_bean")
    println(bean.getName_bean)
  }

}
