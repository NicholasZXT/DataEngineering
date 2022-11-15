
import java.lang.reflect.Array;
/**
 * 泛型数组的使用
 */
public class GenericArray<T> {
//public class GenericArray<T extends Comparable<T>> {  // 一旦泛型类型加入了限定，第一种泛型数组就容易出问题
    public static void main(String[] args) {
        int i = 1;
        //Integer i = new Integer(1);
        Integer j;

        // 使用第一种方式创建的泛型
        GenericArray<Integer> ga1 = new GenericArray<Integer>(5);
        // 调用 addItem 和 getItem 方法读取不会有问题
        ga1.addItem(i, 0);
        System.out.println(ga1.getItem(0));
        // 但是直接访问就会报错  —— TODO 这里不知道啥原因
        //System.out.println(ga1.array[0]);
        // 赋值也会报错
        //ga1.array[0] = i;
        //j = ga1.array[0];
        //System.out.println(j);

        // 使用 第二种方式 创建的泛型数组没有问题
        GenericArray<Integer> ga2 = new GenericArray<Integer>(Integer.class,5);
        ga2.array[0] = i;
        j = ga2.array[0];
        System.out.println(j);
    }

    // 只能对泛型数组进行申明
    public T[] array;

    // 第一种创建泛型数组的方式
    // 这种方式的泛型数组，如果类型参数带有限定，就很容易出问题，
    @SuppressWarnings("unchecked")  // 使用这个注解来避免警告
    public GenericArray(int size){
        // 这种构造方式得到的 array，运行时的类型仍然是 Object[]，而不是 T[]
        this.array = (T[]) new Object[size];
    }

    // 第二种创建泛型数组的方式，推荐这个，它可以处理带有限定条件的泛型
    @SuppressWarnings("unchecked")
    public GenericArray(Class<T> ComponentType, int size){
        // 这种构造方式虽然依然有类型擦除，但在构造器中传递了类型标记Class，以便从擦除中恢复，使得我们可以创建实际类型的数组
        this.array = (T[]) Array.newInstance(ComponentType, size);
    }

    public void addItem(T item, int index){
        this.array[index] = item;
    }
    public T getItem(int index){
        return (T) this.array[index];
    }
}
