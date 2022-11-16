package Coding.sword.offer;
import org.apache.spark.sql.sources.In;

import java.lang.reflect.Array;

/**
 * 实现一个特殊的栈，该栈有一个min方法，能够以O(1)的时间复杂度获取当前栈的最小元素
 */
public class Q21MinInStack<T extends Comparable<T>> {
    public static void main(String[] args){
        // 测试栈
        Stack<Integer> stack = new Stack<>(Integer.class);
        for (int i = 1; i <= 5; i++){
            System.out.println("stack push item: " + i);
            stack.push(i);
        }
        System.out.println("current stack size: " + stack.getSize());
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("current stack size: " + stack.getSize());

    }

    public static class StackWithMin<T>{

    }

    public static class Stack<T>{
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
            System.out.println("resizeStack done");
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
    }

}
