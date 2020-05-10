package SearchAlgorithms;

import java.util.Scanner;

/**
 * 二叉查找树的实现
 */
public class BinarySearchTree<Key extends Comparable<Key>, Value> {
//    main函数测试
    public static void main(String[] args) {
        Integer [] key_array = new Integer[]{9,8,3,7,1,2,5,6};
        String [] val_array = new String[]{"9a","8b","3c","7d","1e","2f","5g","6h"};
        int size = key_array.length;
        System.out.println("待排序的key值数组为：");
        for(int ele:key_array) {
            System.out.print(ele);
            System.out.print(" ");
        }
        System.out.println();

//        BinarySearchTree<Integer,Integer> bst = new BinarySearchTree<Integer, Integer>();
        BinarySearchTree<Integer,String> bst = new BinarySearchTree<Integer, String>();
        for(int i = 0; i < size; i++){
            bst.put(key_array[i],val_array[i]);
        }

        System.out.println("二叉查找树的中序遍历为: ");
        bst.showInMidOrder();
        System.out.println();

        Scanner scan = new Scanner(System.in);
        int key;
        System.out.print("请输入待查找的key：");
        key = scan.nextInt();
        String value = bst.get(key);
        System.out.print("查找的元素为：");
        System.out.print(value);


    }

// -------------- 以下为BST的实现--------------------

    private Node root;  // 二叉树的根节点
    private class Node{
        Key key;  //键
        Value value;
        Node left, right;  // 左右子树
        int N;  // 以该节点为根的子树中的节点数

        public Node(Key key, Value value, int N){
            this.key = key; this.value = value;this.N=N;
        }
    }

    /**
     * get方法表示查找某个元素
     * 方法由重载的私有函数具体实现
     * @param key
     * @return
     */
    public Value get(Key key){
        return get(root,key);
    }

    private Value get(Node node, Key key){
        if(node == null) return null;
        int cmp = key.compareTo(node.key);
        if( cmp < 0){
            return get(node.left, key);
        }else if(cmp > 0){
            return get(node.right, key);
        }else {
            return node.value;
        }
    }

    /**
     * put方法表示插入某个元素
     * 具体方法有重载的私有函数实现
     * @param key
     * @param value
     */
    public void put(Key key, Value value){
        root = put(root, key, value);
    }

    private Node put(Node node, Key key, Value value){
        if( node == null ) return new Node(key,value,1);
        int cmp = key.compareTo(node.key);
        if( cmp < 0 ){
            node.left = put(node.left, key, value);
        }else if ( cmp > 0 ){
            node.right = put(node.right, key, value);
        }else {
            node.value = value;
        }
        node.N = size(node.left) + size(node.right) + 1;
        return node;
    }

    /**
     * 二叉查找树的中序遍历
     * @return
     */
    public void showInMidOrder() {
        showInMidOrder(root);
    }

    private void showInMidOrder(Node node){
        if(node != null) {
            showInMidOrder(node.left);
            System.out.print(node.value);
            System.out.print(" ");
            showInMidOrder(node.right);
        }
//        else {
//            System.out.println("null");
//        }
    }

//  -------------------下面是次要的函数----------------------
    public int size(){
        return size(root);
    }
    private int size(Node node){
        if( node == null)
            return 0;
        else
            return node.N;
    }


}
