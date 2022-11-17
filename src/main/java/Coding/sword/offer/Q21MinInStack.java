package Coding.sword.offer;

import java.util.Arrays;

/**
 * 实现一个特殊的栈，该栈有一个min方法，能够以O(1)的时间复杂度获取当前栈的最小元素
 * 这一题如果知道思路就不难，但是不知道思路就比较棘手；此外，感觉这一题在实际开发中可能比较实用
 */
public class Q21MinInStack<T extends Comparable<T>> {
    public static void main(String[] args){
        // 测试栈
        //Stack<Integer> stack = new Stack<>(Integer.class);
        //for (int i = 1; i <= 5; i++){
        //    System.out.println("stack push item: " + i);
        //    stack.push(i);
        //}
        //System.out.println("current stack size: " + stack.getSize());
        //System.out.println("stack pop item: " + stack.pop());
        //System.out.println("stack pop item: " + stack.pop());
        //System.out.println("stack pop item: " + stack.pop());
        //System.out.println("stack pop item: " + stack.pop());
        //System.out.println("current stack size: " + stack.getSize());

        // 测试 StackWithMin
        int[] array = {6, 4, 5, 3, 7, 1};
        StackWithMin<Integer> stack = new StackWithMin<>(Integer.class);
        System.out.println("array to be push: " + Arrays.toString(array));
        for (Integer value: array){
            stack.push(value);
        }
        System.out.println("current stack size: " + stack.getSize());
        System.out.println("current stack min: " + stack.min());
        System.out.println("-----------------------------------");
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("current stack min: " + stack.min());
        System.out.println("-----------------------------------");
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("current stack min: " + stack.min());
        System.out.println("-----------------------------------");
        System.out.println("stack pop item: " + stack.pop());
        System.out.println("current stack min: " + stack.min());
    }

    public static class StackWithMin<T extends Comparable<T>>{
        private Stack<T> main_stack;
        private Stack<T> sup_stack;
        private T last_min;
        public StackWithMin(Class<T> componentType){
            this.main_stack = new Stack<>(componentType);
            this.sup_stack = new Stack<>(componentType);
        }

        public void push(T item){
            if (this.main_stack.isEmpty()){
                this.last_min = item;
            }else {
                if (this.last_min.compareTo(item) > 0){
                    this.last_min = item;
                }
            }
            this.main_stack.push(item);
            this.sup_stack.push(this.last_min);
        }

        public T pop(){
            T result = null;
            if (this.main_stack.isEmpty()){
                System.out.println("stack is empty");
            }else {
                result = this.main_stack.pop();
                this.sup_stack.pop();
            }
            return result;
        }

        public T min(){
            T result = null;
            if (this.main_stack.isEmpty()){
                System.out.println("stack is empty");
            }else {
                result = this.sup_stack.getTop();
            }
            return result;
        }

        public int getSize(){
            return this.main_stack.getSize();
        }

    }
}

