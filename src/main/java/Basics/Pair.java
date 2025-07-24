package Basics;

/**
 * 泛型类的使用
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
    // 这里指定了泛型的类别，

    public static void main(String[] args) {
        // 泛型类的使用
        Pair<String, Integer> pair1 = new Pair<String, Integer>("str-1", 1);
        // 可以省略 后面的<>里的类型
        Pair<String, Integer> pair2 = new Pair<>("str-2", 2);

        // 泛型方法的使用
        String[] str_arr = {"a", "b", "c"};
        String str_mid_1 = Pair.<String>midArray(str_arr);
        String str_mid_2 = Pair.midArray(str_arr);
    }

    private T first;
    private U second;

    //构造方法不需要使用泛型
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public void setFirst(T newValue) {
        first = newValue;
    }

    public void setSecond(U newValue) {
        second = newValue;
    }

    // 泛型方法
    public static < E > E midArray( E[] inputArray )
    {
        // 输出数组元素
        for ( E element : inputArray ){
            System.out.printf( "%s ", element );
        }
        System.out.println();
        return inputArray[inputArray.length / 2];
    }
}