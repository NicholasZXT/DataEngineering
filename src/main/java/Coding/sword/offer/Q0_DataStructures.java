package Coding.sword.offer;

import java.lang.reflect.Array;

/**
 * 实现 sword-offer中用到的基本数据结构，比如二叉树，栈，链表之类的
 */
public class Q0_DataStructures {
    public static void main(String[] args) {

    }
}

/**
 * 顺序栈的实现
 * 使用范围为 包可见
 */
class Stack<T>{
    private T[] content;    // 采用泛型数组实现栈，注意，泛型数组只能声明，不能直接创建
    private int capacity = 5;  // 栈容量
    private final int increament = 5;  // 每次扩充的大小
    private int top;        // 栈顶指针
    private int size;       // 栈当前大小
    private final Class<T> componentType;  // 栈所存储的数据类型
    @SuppressWarnings("unchecked")
    public Stack(Class<T> componentType){
        this.componentType = componentType;
        this.content = (T[]) Array.newInstance(componentType, this.capacity);
        this.top = -1;    // 特别要注意栈顶指针的初始值为 -1 ，不是0
        this.size = 0;
    }

    public boolean isEmpty(){
        return this.size == 0;
    }
    private boolean isFull(){
        return this.capacity - this.size <=2 ;
    }
    public int getSize(){
        return this.size;
    }

    @SuppressWarnings("unchecked")
    private void resizeStack(){
        T[] temp = (T[]) Array.newInstance(this.componentType, this.capacity + this.increament);
        for (int i = 0; i <= this.top; i++){
            temp[i] = this.content[i];
        }
        //System.out.println("resizeStack done");
        this.capacity += this.increament;
        this.content = temp;
    }

    public void push(T item){
        if (isFull()) {
            resizeStack();
        }
        this.content[++this.top] = item;
        this.size += 1;
    }

    public T pop(){
        T result;
        if (isEmpty()){
            System.out.println("stack is empty");
            result = null;
        }else {
            result = this.content[this.top--];
            this.size -= 1;
        }
        return result;
    }

    public T getTop(){
        T result = null;
        if (isEmpty()){
            System.out.println("stack is empty");
        }else {
            result = this.content[this.top];
        }
        return result;
    }
}


/*
 * 下面两个类的可见性是在 package 范围内，也就是说当前package下的其他文件也可以访问这两个类，并且不能再重复定义了
 */

// 链表的节点定义
class Node<T> {
    Node<T> next;
    T value;
    public Node(Node<T> next, T value){
        this.next = next;
        this.value = value;
    }
}

// 链表定义
class LinkList<T> {
    public Node<T> head;
    public LinkList(){
        // 链表头结点
        this.head = new Node<T>(null, null);
    }

    public void CreateLinkListFromArray(T[] array){
        Node<T> current = this.head;
        for (T value: array){
            System.out.println("add value: " + value);
            Node<T> node = new Node<T>(null, value);
            current.next = node;
            current = node;
        }
    }

    public void show(){
        Node<T> current = this.head;
        while (current.next != null){
            System.out.print(current.value + ", ");
            current = current.next;
        }
        System.out.println(current.value);
    }
}


/***
 * 二叉树的实现类
 * 最好不要作为内部类实现，作为内部类的话，main() 方法里无法直接创建这个类的对象
 */
class BinaryTreeNode{
    int data;
    BinaryTreeNode left;
    BinaryTreeNode right;
    public BinaryTreeNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
    public BinaryTreeNode(int data, BinaryTreeNode left, BinaryTreeNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }
    public void firstRootShow(){
        show(this);
        System.out.println();
    }
    private void show(BinaryTreeNode root){
        if (root != null){
            System.out.print(root.data);
            System.out.print(", ");
            show(root.left);
            show(root.right);
        }
    }
}