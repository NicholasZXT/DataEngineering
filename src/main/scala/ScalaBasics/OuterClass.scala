package ScalaBasics

class OuterClass {

//  outer是外部类的别名
  outer => class InnerClass{
    var name = "inner"
    private def innerFun()={
//    通过别名outer来访问外部类的方法（包括外部类private方法）
      outer.outerFun1
      println("from inner fun")
//      访问外部类的属性
      outer.name
      }

    def innerFun2()={
      //      使用 this 访问本类的其他方法
      this.innerFun()
    }

  }

//  外部类的属性
  var name = "outerName"

//  外部类的方法
  private def outerFun1()={
    println("from outer class")
  }

//  外部类访问内部类的方法
  def outerFun2()={
//    通过创建对象的方式，只能访问公开的方法
    val obj = new InnerClass
    obj.innerFun2()
    obj.name

  }


}
