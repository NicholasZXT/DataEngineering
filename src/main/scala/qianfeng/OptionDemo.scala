package qianfeng

object OptionDemo {

  def main(args: Array[String]): Unit = {
//    Option有两个子类，some和none
    val map = Map("a"->1,"b"->2)
    val value = map.get("a")
    val res = value match{
      case Some(value) => value
      case None => 0
    }
    println(res)

//    上述就相当与map的getOrElse方法
//    实际上，上面就是gerOrElse的底层实现
    val res2 = map.getOrElse("a",0)

  }

}
