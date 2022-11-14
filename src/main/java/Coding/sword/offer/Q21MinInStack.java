package Coding.sword.offer;

/**
 * 实现一个特殊的栈，该栈有一个min方法，能够以O(1)的时间复杂度获取当前栈的最小元素
 */
public class Q21MinInStack<T extends Comparable<T>> {
    public static void main(String[] args){

    }

    public static class StackWithMin<T>{

    }

    public static class Stack<T>{
        private int INIT_SIZE;  // 栈的初始大小
        private T[] content;    // 采用泛型数组实现栈，注意，泛型数组只能声明，不能直接创建
        private int top;        // 栈顶指针
        private int size;       // 栈大小
        public Stack(){
            this.content = (T[]) new Object[INIT_SIZE];


        }
    }

}
