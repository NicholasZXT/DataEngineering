package ScalaBasics

import scala.beans.BeanProperty

object Test {
  def main(args: Array[String]): Unit = {
    //创建对象，这里暂时不讨论构造方法
    val s1 = new StudentV1()
    val s2 = new StudentV1  // 由于没有构造方法，也可以这么写

    // 访问 公有 val 属性的 getter 方法
    println(s1.role)
    // 但是没有 setter 方法，所以无法赋值
    // s1.role = "teacher"

    // 访问 公有 var 属性的 getter 和 setter 方法
    println(s1.name)
    s1.name = "jerry"
    println(s1.name)
    // 手动调用自动生成的 setter 方法
    s1.name_=("new_jerry")
    println(s1.name)

    // 访问 私有 var 属性
    // println(s1.hobby)  // 访问不了
    print("showHobby(): ")
    println(s1.showHobby())  // 实际上是有该属性的

    // 访问自己重写的 privateAge 的 getter 和 setter 方法
    println(s1.age)
    s1.age = 4
    println(s1.age)
    s1.age_=(5)
    println(s1.age)

    print("showSid(): ")
    println(s1.showSid())

    print("isYounger(): ")
    println(s1.isYounger(s2))

    println("gender: ")
    println(s1.gender)
    s1.gender = "female"
    println(s1.gender)

  }
}

/**
 * 定义一个 Scala 类：
 *  1. Scala中 不 要求类名称和文件名必须一致
 *  2. 类不需要声明为 public
 *  3. 一个源文件可以包含多个类，所有的这些类，都具有公有可见性
 * 这里暂时不讨论构造方法
 */
class StudentV1 {
  // 可以用 val 或者 var 定义属性，默认下属性为 public
  // 对于 val 定义的公有属性，会自动生成一个 同名的 公有 getter 方法，但不会生成 setter 方法
  val role: String = "student"
  // 对于 var 定义的公有属性，会自动生成一个 同名的 公有 getter 方法，还有一个名为 "name_=" 的 公有 setter方法（包括后面的=）

  var name: String = "tom"
  // 定义属性时只是指定属性的类型，用占位符表示未赋值
  var nickName: String = _
  // 但是 val 属性不能使用 占位符
  // val nickName2: String = _

  // 对于私有属性，生成的 setter 和 getter 方法 也是私有的
  private var hobby: String = "no hobby"
  // 提供一个显示该字段的方法
  def showHobby(): String = {
    this.hobby
  }

  // 可以自己重新定义 setter 和 getter 方法，这里为 私有属性 privageAge 定义了一个 "age" 属性的 setter 和 getter
  private var privateAge: Int = 3
  // setter 方法
  def age = privateAge  // 这里定义时未使用括号，那么使用此方法时也不能使用括号
  // getter 方法
  def age_=(newAge: Int): Unit = {
    // 和 Java 一样，this 都表示访问当前实例对象的属性
    if (newAge > this.privateAge){  // 限制不能变年轻
      this.privateAge = newAge;
    }
  }

  // 如果不想自动生成 getter 和 setter 方法，可以使用 private[this]
  private[this] var sid: String = "stu01"
  // 提供一个显示此字段的方法
  def showSid()= {
    this.sid
  }

  // 实际上，private[this] 还限制此字段为 对象私有的，不能被当前类的其他实例对象访问
  // 更深入一点， private[类名] 可以限制访问此字段的类范围
  def isYounger(stu2: StudentV1): Boolean = {
    // 本来 stu2.privageAge 在外面是访问不到的，但是这里可以访问到
    // 如果换成上面的 sid 属性就不行了
    this.privateAge <= stu2.privateAge
  }

  // 当然，Scala 还提供了下面的注解，自动生成 name, name_=, getName, setName 这样4个方法，后两个是 Java Bean 的访问方式
  @BeanProperty
  var gender: String = _

  // 上面这些属性可以编译完之后，看看编译成的Java代码是什么
}

