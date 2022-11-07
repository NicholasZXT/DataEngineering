package Coding.sword.offer;

import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;

public class Q7QueueWithTwoStack {
    public static void main(String[] args){
        // 测试由两个栈组成的队列
        //int[] array = {1,2,3};
        //StackQueue<Integer> queue = new StackQueue<>();
        //System.out.println("add values: " + Arrays.toString(array));
        //for(int value: array) queue.add(value);
        //System.out.println("poll value: " + queue.poll());
        //System.out.println("poll value: " + queue.poll());
        //System.out.println("add values: " + 4);
        //queue.add(4);
        //System.out.println("poll value: " + queue.poll());
        //System.out.println("add values: " + 5);
        //queue.add(5);
        //System.out.println("poll value: " + queue.poll());
        // 测试由队列组成的栈
        int[] array = {1,2,3};
        QueueStack<Integer> queue = new QueueStack<>();
        System.out.println("add values: " + Arrays.toString(array));
        for(int value: array) queue.add(value);
        System.out.println("pop value: " + queue.pop());
        System.out.println("pop value: " + queue.pop());
        System.out.println("pop value: " + queue.pop());
        System.out.println("add values: " + 4);
        queue.add(4);
        System.out.println("add values: " + 5);
        queue.add(5);
        System.out.println("pop value: " + queue.pop());
        System.out.println("pop value: " + queue.pop());
    }
}

/*
用两个栈模拟一个队列
 */
class StackQueue<E>{
    private Stack<E> stack_in;
    private Stack<E> stack_out;
    private int size;
    public StackQueue(){
        this.stack_in = new Stack<E>();
        this.stack_out = new Stack<E>();
        this.size = 0;
    }

    public boolean add(E value){
        boolean success;
        success = this.stack_in.add(value);
        if (success) this.size += 1;
        return success;
    }

    public E poll(){
        E result;
        if (this.size == 0) return null;
        if (this.stack_out.empty()){
            while (!this.stack_in.empty()){
                this.stack_out.push(this.stack_in.pop());
            }
        }
        result = this.stack_out.pop();
        this.size -= 1;
        return result;
    }
}

/*
用队列模拟一个栈，这个比上面那个要难一些
 */
class QueueStack<E>{
    private Queue<E> queue1;
    private Queue<E> queue2;
    private int size;
    public QueueStack(){
        // Queue 是一个抽象接口，实际的一个实现类是LinkedList
        this.queue1 = new LinkedList<E>();
        this.queue2 = new LinkedList<E>();
        this.size = 0;
    }

    public boolean add(E value){
        boolean success;
        if (this.queue1.isEmpty()){
            success = this.queue2.add(value);
        }else{
            success = this.queue1.add(value);
        }
        if (success) this.size += 1;
        return success;
    }

    public E pop(){
        E result;
        Queue<E> queue_in;
        Queue<E> queue_out;
        if (this.queue1.isEmpty()){
            queue_in = this.queue1;
            queue_out = this.queue2;
        }else{
            queue_in = this.queue2;
            queue_out = this.queue1;
        }
        for (int i=1; i<this.size; i++){
            queue_in.add(queue_out.poll());
        }
        result = queue_out.poll();
        this.size -= 1;
        return result;
    }
}
