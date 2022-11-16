package Coding.sword.offer;

import java.util.Arrays;

/**
 * 给定一个栈的入栈序列，判断另一个序列是否为出栈序列
 * 注意，出栈有很多序列情况：比如入栈序列为 [1,2,3,4,5]，那么 [4,5,3,2,1] 可以是一个出栈序列，但 [4,3,5,1,2] 就不可能是.
 * 这一题有难度，我一开始的理解有误，导致解答错误
 */
public class Q22StackPushPopOrder<T> {
    public static void main(String[] args){
        Q22StackPushPopOrder<Integer> solver = new Q22StackPushPopOrder<>();
        Integer[] array1 = {1,2,3,4,5};
        Integer[] array2 = {5,4,3,2,1};
        Integer[] array3 = {4,5,3,2,1};
        Integer[] array4 = {4,3,5,1,2};  // No
        //Integer[] array3 = {5,4,3};
        //Integer[] array4 = {3,2,1};
        Integer[] array5 = {3,1};
        Integer[] array6 = {3};
        Integer[] array7 = {4,5};
        Integer[] array8 = {3,4};
        System.out.println("stack pushed array: " + Arrays.toString(array1));
        System.out.println("pop order for array " + Arrays.toString(array2) + " : " + solver.isPopOrder(array1, array2, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array3) + " : " + solver.isPopOrder(array1, array3, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array4) + " : " + solver.isPopOrder(array1, array4, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array5) + " : " + solver.isPopOrder(array1, array5, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array6) + " : " + solver.isPopOrder(array1, array6, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array7) + " : " + solver.isPopOrder(array1, array7, Integer.class));
        System.out.println("pop order for array " + Arrays.toString(array8) + " : " + solver.isPopOrder(array1, array8, Integer.class));
    }


    public boolean isPopOrder(T[] array1, T[] array2, Class<T> componentType){
        boolean flag = false;
        if (array1 == null | array2 == null | array1.length == 0 | array2.length == 0)
            return flag;
        Stack<T> stack = new Stack<>(componentType);
        int push_index = 0;
        int cnt = 0;
        for(T value: array2){
            // 下面的 while 循环很重要
            // 只要 栈为空 或者 栈顶元素不是当前待检查的元素，就将入栈序列的元素逐个压栈
            while (stack.isEmpty() | stack.getTop() != value){
                if (push_index == array1.length)
                    break;
                stack.push(array1[push_index]);
                push_index++;
            }
            // 上面的 while 循环结束有两种情况：
            // 1. 入栈序列的元素全部压栈，通过 break 跳出的循环：说明当前待检查元素并不在弹出序列中，此时跳出 for 循环，不需要检查后续的弹出序列元素了
            if (stack.getTop() != value)
                break;
            // 2. 入栈序列的元素逐个压栈之后，找到了一个栈顶元素 = 当前待检查元素：此时只需要从栈中将待检查元素弹出，表示当前待检查元素在弹出序列中
            stack.pop();
            cnt++;
        }
        // 上面的 for 循环结束后，只有 栈为空，并且 array2 中的元素全部被检查过（cnt=array2.length），表示弹出序列都被检查过，没有问题
        // 否则都表示弹出序列不属于该入栈序列的
        if (stack.isEmpty() & cnt == array2.length)
            flag = true;
        return flag;
    }

    // --------------------------------------------------------------------------------------
    /**
     * 我的错误解答
     */
    public boolean isPopOrder_old(T[] array1, T[] array2, Class<T> componentType){
        boolean flag = false;
        if (array1 == null | array2 == null | array1.length == 0 | array2.length == 0)
            return flag;
        Stack<T> stack = new Stack<>(componentType);
        for (T value: array1){
            stack.push(value);
        }
        int cnt = 0;
        boolean find_flag;
        for (T value: array2){
            find_flag = this.findValueInStack(value, stack);
            if (!find_flag) break;
            //if (!this.findValueInStack(value, stack)) break;
            cnt += 1;   // 这个必须要放在上面的判断后面
        }
        if (cnt == array2.length){
            flag = true;
        }
        return flag;
    }

    private boolean findValueInStack(T item, Stack<T> stack){
        boolean flag = false;
        T pop_value;
        while (!stack.isEmpty()){
            pop_value = stack.pop();
            if (pop_value.equals(item)){
                flag = true;
                break;
            }
        }
        return flag;
    }

}
