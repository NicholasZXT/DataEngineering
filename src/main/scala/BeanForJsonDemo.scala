
import scala.collection.mutable.ListBuffer
import scala.beans.BeanProperty
import com.alibaba.fastjson.JSON
import java.util.List

class BeanForJsonDemo{

//  name是普通的变量
  var name:String = ""

//  name_bean是通过注解生成的JavaBean变量
  @BeanProperty
  var name_bean:String = ""
}


// 下面用于测试使用JavaBean对象和fastJSON解析JSON
case class People(@BeanProperty var name:String = "", @BeanProperty var gender:String = "")

case class Family_1(
  @BeanProperty var family_name:String = "",
  @BeanProperty var member:People = null,  // 这个用于json_str_1
  @BeanProperty var address:String = ""
  )

case class Family_2(
   @BeanProperty var family_name:String = "",
// 这里必须要使用Java的List,不能使用scala的List
   @BeanProperty var member:List[People] ,
//   @BeanProperty var member:List[String] ,
//   @BeanProperty var member:List[String] = Nil,
//   @BeanProperty var member:List[People] = Nil,
//   @BeanProperty var member:Array[People] = new Array[People](1),
   @BeanProperty var address:String = ""
   )

object BeanForJsonDemo {

  def main(args: Array[String]): Unit = {
    val bean = new BeanForJsonDemo()

//    name只能通过属性的方式设置和取值
//    bean.name = "name"
//    println(bean.name)

//    name_bean会自动生成下面的set和get方法
//    bean.setName_bean("name_bean")
//    println(bean.getName_bean)

//    fastJson通过JavaBean对象反序列化解析JSON
    val json_str_1:String =
      """
        |{
        |	"family_name":"zhang",
        |	"member":{"name":"one","gender":"male"},
        |	"address":"earth"
        |}
      """.stripMargin

//    这个带有嵌套数组
    val json_str_2:String =
      """
        |{
        |	"family_name":"zhang",
        |	"member":[
        |     {"name":"one","gender":"male"},
        |			{"name":"two","gender":"female"},
        |			{"name":"three","gender":"female"}
        |			],
        |	"address":"earth"
        |}
      """.stripMargin

    val family_1 = JSON.parseObject(json_str_1, classOf[Family_1])
    println(family_1.toString)
    println(family_1.getFamily_name)
    println(family_1.getMember)
    println(family_1.getMember.getName)
    println(family_1.getMember.getGender)
    println(family_1.getAddress)

    val family_2 = JSON.parseObject(json_str_2, classOf[Family_2])
    println(family_2.toString)
    println(family_2.getFamily_name)
    println(family_2.getMember)
    println(family_2.getMember.get(0).getName)
    println(family_2.getAddress)
  }



}
