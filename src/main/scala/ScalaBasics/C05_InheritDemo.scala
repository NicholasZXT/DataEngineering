package ScalaBasics

/**
 * Scala 中的继承
 */
// 父类
class PersonV1(var name: String) {
  var gender: String = _
  def this(name: String, gender: String) = {
    this(name);
    this.gender = gender
  }

}

// 子类，注意，子类调用的父类主构造器 也是在后面 加上一个圆括号，里面写上参数值
class StudentV4(name: String, gender: String) extends PersonV1(name, gender) {
  var grade: String = _
  def this(name: String, gender: String, grade: String) = {
    this(name, gender)
    this.grade = grade
  }

}

object TestInheritDemo extends App {

}
