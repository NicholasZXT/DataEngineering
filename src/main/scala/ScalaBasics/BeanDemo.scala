package ScalaBasics

import scala.beans.BeanProperty

class BeanDemo {
//  ——只要加上下面这个注释，就能自动生成Java的bean对象，可以编译后查看相应的Java源代码
  @BeanProperty
  var name:String = _

}
