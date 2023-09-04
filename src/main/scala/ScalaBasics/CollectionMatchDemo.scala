package ScalaBasics

import scala.util.Random

object CollectionMatchDemo {

//  数组的模式匹配
  def arrMatch(arr:Array[Int])={
   val res =  arr match {
        //匹配包含0的数组
      case Array(0)=>"0"
        //匹配任何带有两个元素的数组
      case Array(x,y)=>s"$x $y"
        //匹配以0作为第一个元素的数组
      case Array(0,_*)=>"0>>>"
        //匹配所有
      case _=>"else"
    }
    res
  }

//  元组匹配
  def tupleMatch(x:(Int,Int))={
    val res = x match {
      case (0,_)=>"0..."
      case (y,0)=>s"$y 0"
      case _=>"others"
    }
    res
  }

  //列表匹配
  def listMatch(list:List[Int])={
    val res = list match {
        //匹配只有一个0元素的列表
      case 0::Nil =>"0"
        //匹配含有两个元素的列表
      case x::y::Nil =>s"$x,$y"
        //匹配以0开始的列表
      case 0::tail=>"0..."
        //匹配所有
      case _=>"others"
    }
    res
  }

  //类型匹配

  def typeMatch()={
    val arr=Array("scala",'c',123,4.5)
    val elem = arr(Random.nextInt(4))
    val res = elem match {
      case i:Int=>"Int"
      case s:String=>"String"
      case d:Double=>"Double"
      case _=>"others"
    }
    res
  }


  def main(args: Array[String]): Unit = {
    val arr = Array(1,2)
    val res = arrMatch(arr)
    println(res)

    val tup = (0,1)
    val res1 = tupleMatch(tup)
    println(res1)

    val list = List(5,6)
    val res2 = listMatch(list)
    println(res2)

    println(typeMatch())

  }

}
