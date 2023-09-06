package ScalaBasics

import scala.beans.BeanProperty

object TestConstructorDemo{
  def main(args: Array[String]): Unit = {
    val s1 = new StudentV2("tom", 24, "male", "cat", "stu01", "animal")
    val s2 = new StudentV2("jerry", 24, "female", "cat", "stu01")
    println("\nstudent 1: ")
    println(s1.name)
    println(s1.age)
    println(s1.gender)
    println(s1.role)
    println("\nstudent 2: ")
    println(s2.name)
    println(s2.age)
    println(s2.gender)
    println(s2.role)

    println("--------------------------------------------------------")
    val s3 = new StudentV3("zhou")
    val s4 = new StudentV3("daniel", 30, "male")
    println("\nstudent 3: ")
    println(s3.name)
    println(s3.age)
    println(s3.gender)
    println("\nstudent 4: ")
    println(s4.name)
    println(s4.age)
    println(s4.gender)

  }
}

/**
 * Scala 中主构造方法并不是和类名同名，而是和类的定义交织在一起：
 *  1. 主构造器的 参数 直接放在 类名之后，如果类名之后没有参数，则对应一个 无参主构造器
 *  2. 参数的形式可以使用 任何在 类定义体 合法的形式
 *  3. 主构造器中已经定义的 属性，不需要在 类定义体 中再次定义
 *  2. 主构造器会执行 类定义体 中的所有语句
 */
class StudentV2 (var name: String, private var privateAge: Int, @BeanProperty var gender: String,
                 hobby: String, sid: String, val role: String = "student") {
  // 上面定义参数的形式和 StudentV1 定义属性的形式一致，并且效果是一样的：var 生成 getter + setter, val 生成 getter, private 控制访问
  // 可以有默认值，比如 role 字段就设置了默认值 —— 但是默认值的参数必须要放在末尾

  // 由于主构造器中已经定义了属性，所以不需要在类定义体中重复定义了
  // var name: String = _
  // val role: String = "student"
  // private var privateAge: Int = _

  // 唯一特别的是 hobby 和 sid 字段，它们没有 val 或者 var 修饰，比较特殊，只有在被某个方法使用的时候，才会成为一个字段，
  // 并且是对象私有的不可变字段（相当于 private[this] val 修饰），如果没有被使用，那么这个字段就不会存在
  def description(): String = {
    // 注意开始的 s ，表示字符串插值
    // 这里使用了 hobby 变量，所以会有此字段，而 sid 字段没有被使用过，所以无法访问该字段
    s"$name's hobby is $hobby ."
  }

  // 下面的 println 语句也属于主构造方法的一部分，所以每次实例化一个对象时，都会被执行
  println("Primary constructor is executed!")

  // 为 privageAge 属性定义 getter 和 setter 方法
  def age = privateAge
  def age_=(newAge: Int): Unit = {
    if (newAge > this.privateAge){  // 限制不能变年轻
      this.privateAge = newAge;
    }
  }

}


/**
 *  1. Scala 中辅助构造器的名称统一是 this，所有的辅助构造器，第一行 必须调用 另一个辅助构造器 或者 主构造器
 *  2. 主构造器可以是 私有的，这样可以限制用户必须使用 辅助构造器 来构造对象
 */
class StudentV3 private () {
  // 上面定义了一个 私有的 空参数 的主构造器，对应于 this()，并且由于这个主构造器是私有的，所以用户只能通过 辅助构造器来实例化对象
  println("Primary constructor is executed!")

  // 由于主构造器是空参数，没有定义任何属性，所以需要在下面定义属性
  val role: String = "student"
  var name: String = _
  var age: Int = _
  @BeanProperty var gender: String = _

  // 定义各种辅助构造器
  def this(name: String) = {
    // 调用了主构造器，必须放在第一行
    this()
    this.name = name
  }

  def this(name: String, age: Int, gender: String) = {
    // 第一种方式：调用主构造器
    // this()
    // this.name = name
    // 第 2 种方式：调用另一个辅助构造器
    this(name)
    this.age = age
    this.gender = gender
  }

}


