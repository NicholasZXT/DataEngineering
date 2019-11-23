package qianfeng

trait Logger1{
  def log(msg:String)={
    println(msg)
  }
}

// 基类
class Account{
  protected var balance = 0.0
}

class SavingAccount extends Account with Logger1{
  def withdraw(amount:Double)={
    if (amount> balance) log("余额不足")
    else balance = balance - amount
  }

}

object TraitDemo {

}
