package qianfeng

// 构造一棵二叉树
trait Node
case class TreeNode(value:String,left:Node,right:Node) extends Node
case class Tree(root:TreeNode)

object ConstructorMatch {
  def main(args: Array[String]): Unit = {
//    定义一棵二叉树
    val tree = Tree(TreeNode("root",TreeNode("left",null,null),TreeNode("right",null,null)))

    tree.root match {
//      要求，根节点的左子树值为left，根节点的右子树值为right，并且右子树的左右子树为null
      case TreeNode(_,TreeNode("left",_,_),TreeNode("right",null,null)) => println("matched")
      case _ => println("nothing")
    }
  }

}
