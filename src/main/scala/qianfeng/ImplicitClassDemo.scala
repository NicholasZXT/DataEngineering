package qianfeng

// 定义一个隐式类
object ImplicitClassDemo extends App {

  // 隐式类只能定义在类，trait和object的内部
  implicit class IntWithTimes(x:Int){
    def times[A](fun: => A):Unit = {
      //  递归方法需要指定返回的类型
      def loop(current:Int):Unit={
        if(current > 0){
          fun
          loop(current-1)
        }
      }
      loop(x)
    }
  }

  // 隐式类的构造函数只能带一个非隐式参数
  // 下面这个不可以
  //  implicit class Indexer[T](collection:Seq[T],index:Int)
  //  可以使用参数列表的形式
  implicit class Indexer[T] (collection:Seq[T])(implicit index:Int)



//  隐式转换
//  a和b的类型不一样
  var a:Int = 10
  var b:Double = 10.99

//  可以将Int类型的值赋值给Double，这就是Int类的隐式转换在起作用
  b = 100
  b = a

//  但是不可以将Double直接转成Int
//  需要定义一个隐式转换函数，把Double转成Int
  implicit def double2int (x:Double):Int= x.toInt
//  定义完隐式转换之后，就可以编译通过了
  a = b
  a = 10.99



//  隐式参数
//  定义一个特质，带有一个抽象方法
  trait  Adder[T]{
    def add(x:T,y:T):T
  }
//创建一个隐式对象
  implicit val aa = new Adder[Int] {
    override def add(x: Int, y: Int): Int = x + y
  }
//  定义一个带有隐式参数的方法
  def addTest(x:Int,y:Int)(implicit adder: Adder[Int])={
    adder.add(x,y)
  }

//  使用隐式参数
  println(addTest(1,2))
  println(addTest(1,2)(aa))


}
