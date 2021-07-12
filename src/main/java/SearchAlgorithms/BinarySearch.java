package SearchAlgorithms;

import java.util.Arrays;

/**
 * 二分查找法实现类
 * 使用的数据结构是 有序数组
 */
public class BinarySearch<Key extends Comparable<Key>, Value> {

    public static void main(String[] args) {


    }

    private Key[] keys;       // 存储键的数组
    private Value[] values;   // 存储值的数组
    private int N;         // 当前数组的大小

    public BinarySearch(int capacity){
        keys = (Key[]) new Comparable[capacity];
        values = (Value[]) new Comparable[capacity];
        //不能使用下面的方式初始化数组
        //keys = new Key[size];
        //values = new Value[size];
        N = 0;
    }

    /**
     * 用于根据 key 获取 value 的方法，它会调用 rank 方法
     * @param key 待查找的 key
     * @return 查找成功返回 key 对应的值，否则返回 null
     */
    public Value get(Key key){
        if (isEmpty()) return null;
        // rank 方法返回的是比 key 小的 键的个数
        int i = rank(key);
        if (i < N && keys[i].compareTo(key) == 0){
            return values[i];
        }else {
            return null;
        }
    }

    /**
     * 用于插入 key-value 的方法，它也会调用 rank 方法
     * @param key 要插入的 key
     * @param value 要插入的 value
     */
    public void put(Key key, Value value){
        int i = rank(key);
        // 如果查找到，则更新 value
        if (i < N && key.compareTo(keys[i]) == 0){
            values[i] = value;
            return;
        }

        // 没有该 key，就要将有序数组后的元素往后移动
        for (int j=N; j > i; j--){
            keys[j] = keys[j-1];
            values[j] = values[j-1];
        }
        // 移动完，在下标 i 处空出的位置，插入 key-value 对
        keys[i] = key;
        values[i] = value;
        N++;
    }

    /**
     * 二分查找的核心方法
     * 此处使用 迭代 的方式实现
     * 注意，这个方法虽然使用的是二分查找，但是它的返回值和仅用于查找的方法返回值不一样：它返回的是表中小于指定key的键数量
     * 这种返回值有如下两个特点：
     * 1. 如果 存在 对应的 key，返回表中小于该key的键数量，刚好是此键的位置——用于get
     * 2. 如果 不存在 对应的 key，返回表中小于该key的键数量，刚好是此键应该插入的位置——用于put
     * @param key，待查找的 key
     * @return int, 返回的是表中小于指定key的键数量
     */
    public int rank(Key key){
        int low = 0, high = N-1;
        while (low < high){
            int mid = low + (high - low) / 2;
            //下面这个用法也一样
            //int mid = (low + high) / 2;
            int cmp = key.compareTo(keys[mid]);
            if (cmp == 0) return mid;  // 查找成功，返回 key 在 keys 中的下标值
            else {
                if (cmp > 0) low = mid;
                else high = mid;
            }
        }
        // 查找失败的时候，low == high，此时的下标值也就是该 key 应当插入的位置——用于put方法，也可以返回
        return low;
    }

    /**
     * 二分查找的核心方法
     * 作用和上面的 rank 一致，但使用 递归 方式实现
     * @param key 待查找的Key
     * @param low 此次递归的左下标
     * @param high 此次递归的数组的右下标
     * @return int
     */
    public int rank(Key key, int low, int high){
        if (low >= high) return low;
        int mid = (low + high) / 2;
        int cmp = key.compareTo(keys[mid]);
        if (cmp == 0) return mid;
        else {
            // 这里采用 左右 递归的方式
            if (cmp > 0) return rank(key, mid, high);
            else return rank(key, low, mid);
        }
    }

    public int size(){
        return N;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    public void print(){
        System.out.print("{");
        for(int i = 0; i < N; i++){
            System.out.print(keys[i] + " : " + values[i] + "");
            if (i < N-1) System.out.print(", ");
        }
        System.out.println("}.");
    }

}
