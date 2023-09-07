package ScalaBasics

/**
 * 单例对象，以 object 关键字定义的代码体，主要有两个作用：
 *  1. scala的类中，不允许有静态字段或者静态方法，对于这些静态成员，scala提供了单例对象来存放.
 *     比如工具方法，应用程序的 main 入口 等.
 *  2. 实现某个类的单例模式
 *
 * 同一个源文件中如果有和单例对象同名的类，那么这个单例对象就变成了该类的伴生对象，同时这个类也叫伴生对象的伴生类；
 * 没有同名伴生类的单例对象也被称为 孤立对象.
 * 伴生对象和伴生类可以相互访问对方的属性和方法，包括私有属性和方法.
 *
 * 单例对象 基本上可以拥有类的所有特性：定义属性，扩展其他类或者特征，只有一个例外——它不能提供构造器参数
 */

/**
 * 伴生对象
 */
object CompanionDemo {
  // 伴生对象也是一个单例对象，这些属性只会存在一个对象里，所以伴生类里访问这些属性时，可以通过 伴生对象名称/伴生类名称 来直接访问
  private var obj_name: String = "companion object"
  private def privateObjName(): String = {
    this.obj_name
  }
  def showNamesByObj(cls_obj: CompanionDemo): Unit = {
    // 访问伴生对象自己的 私有属性 + 私有方法，没问题
    println(this.obj_name)
    println(this.privateObjName())
    // 访问 某个 伴生类的实例对象 的私有属性 + 私有方法 —— 这里必须要指定是 某个实例对象
    println(cls_obj.cls_name)
    println(cls_obj.privateClsName())
  }
}

/**
 * 伴生类
 */
class CompanionDemo {
  // 伴生类可以创建多个对象，所以伴生对象需要通过 伴生类的实例对象 来访问
  private var cls_name: String = "companion class"
  private def privateClsName(): String = {
    this.cls_name
  }
  // 定义了一个 辅助构造器
  def this(name: String) = {
    this()
    this.cls_name = name
  }
  def showNamesByCls(): Unit = {
    // 访问自己的私有属性 + 私有方法，没问题
    println(this.cls_name)
    println(this.privateClsName())
    // 直接访问伴生对象里 私有属性 + 私有方法，但是要使用 伴生对象名 限定
    println(CompanionDemo.obj_name)
    println(CompanionDemo.privateObjName())
  }
}

/**
 * 孤立对象
 */
object SingletonLogger{
  var msg: String = "log"
  def getLog(msg: String) = {
    println("log: " + msg)
  }
}


/**
 * 存放程序入口的 单例对象，继承了 App 特征，就不需要手动定义 main 方法了
 */
object TestCompanionDemo extends App {
  // 扩展 App 特征之后，定义体中的所有代码，都被看做 main 函数中的内容

  // 定义了两个 伴生类的实例对象
  val c1 = new CompanionDemo()
  val c2 = new CompanionDemo("companion class obj")

  // 在 伴生类 实例对象 的 showNamesByCls 中 访问了 伴生对象的 私有属性 + 私有方法
  c1.showNamesByCls()

  // 在 showNamesByObj() 中访问了 伴生类实例对象 c2 的私有属性 + 私有方法
  // 通过 伴生对象名 来 直接调用 伴生对象 CompanionDemo 中的方法——它相当于一个静态方法
  CompanionDemo.showNamesByObj(c2)

  println("--------------------------------------")

  // 使用孤立对象
  println(SingletonLogger.msg)
  SingletonLogger.getLog("hello")

}