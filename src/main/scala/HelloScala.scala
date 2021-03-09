import util.control.Breaks._

object HelloScala {

  def main(args: Array[String]): Unit = {

    println("hello scala")

//    breakable的break方法
    breakable{
      for (i <- 1 to 6){
        if (i == 3) break()
        println(i)
      }
    }

//    实现continue
    for (i <- 1 to 6){
      breakable{
        if (i==3) break()
        println(i)
      }
    }

    val fun = (x:Int,y:Int) =>x+y


  }

}
