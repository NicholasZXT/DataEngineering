package qianfeng

import java.util.Date


object PartialAppFunDemo {

  def log(date: Date,message:String) = {
    println(date + message)
  }

  def main(args: Array[String]): Unit = {
    val date = new Date()
    log(date,"time1")
    log(date,"time2")

//    偏应用函数：通过固定某个函数的一部分参数得到的新函数
//    这里固定了date这个参数
    val newlog = log(date, _:String)
    newlog("time3")
    newlog("time4")

  }
}