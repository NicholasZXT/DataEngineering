package Basics.collection;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemo {
    public static void main(String[] args) {
        // 集合的元素
        Item i1 = new Item(1, "a", 1);
        Item i2 = new Item(2, "a", 2);
        Item i3 = new Item(3, "a", 3);
        Item i4 = new Item(4, "b", 4);
        Item i5 = new Item(5, "b", 5);
        Item i6 = new Item(6, "c", 6);
        Item i7 = new Item(7, "c", 7);
        // 创建一个数组
        Item [] item_array = {i1, i2, i3, i4, i5, i6, i7};
        // 创建一个List
        List<Item> item_list = new ArrayList<>();
        Collections.addAll(item_list, item_array);

        // -------- Stream 创建 -----------
        // 从数组创建流
        Stream<Item> s1 = Stream.of(item_array);
        // 从数组指定位置创建流
        Stream<Item> s2 = Arrays.stream(item_array, 0, 6);
        // 从集合创建流
        Stream<Item> s3 = item_list.stream();
        // 创建可重用的流对象——使用了Lambda表达式
        Supplier<Stream<Item>> items_supplier = () -> Stream.of(item_array);

        // -------- Stream 中间操作 -----------
        // 包括 map, filter, flatMap, peek, sort 等操作方法
        s1.forEach(System.out::println);
        System.out.println("---------------------------");
        // 一个流对象使用一次之后就会被关闭，不能重用
        s2.map(Item::toString).peek(System.out::println).limit(10).toArray();
        System.out.println("---------------------------");
        s3.filter(s -> !Objects.equals(s.getCategory(), "c")).forEach(System.out::println);
        System.out.println("---------------------------");

        // 可重用的流，实际上是每次都创建了一个新的流
        items_supplier.get().forEach(System.out::println);
        System.out.println("---------------------------");
        items_supplier.get().filter(s -> !Objects.equals(s.getCategory(), "c")).forEach(System.out::println);

        // -------- Stream 结果收集 -----------
        // 常用的有 toArray, reduce, findAny, findFirst, allMatch, anyMatch, noneMatch, counting, min, max 等
        // 上面使用的 forEach 方法也是一个结果收集方法，常用于调试
        // 最通用的，还是 collect() 方法，可以完成各种复杂的结果收集，包括分组聚合。
        // 它的一个签名是 collect(Collector<? super T, A, R> collector)，接受一个 Collector 接口的实现类或者函数式接口，
        // java.util.stream.Collectors 里提供了不少可以直接生成对应 Collector 的工具方法，可以直接使用，比如 Collectors.toList() 等
        // 分组聚合操作，需要使用 collect() 方法 + Collectors.groupingBy() 工具方法
        // 仅分组
        Stream<Item> s4 = items_supplier.get();
        // 注意返回的类型，是一个 Map<String, List<Item>>
        Map<String, List<Item>> itemGroups = s4.collect(Collectors.groupingBy(Item::getCategory));
        List<Item> g1 = itemGroups.get("a");
        List<Item> g2 = itemGroups.get("b");
        List<Item> g3 = itemGroups.get("c");
        System.out.println("group 'a':");
        g1.forEach(System.out::println);
        System.out.println("group 'b':");
        g2.forEach(System.out::println);
        System.out.println("group 'c':");
        g3.forEach(System.out::println);
        System.out.println("---------------------------");
        // 分组count
        Stream<Item> s5 = items_supplier.get();
        Map<String, Long> itemGroupCount = s5.collect(Collectors.groupingBy(Item::getCategory, Collectors.counting()));
        itemGroupCount.forEach((k, v) -> System.out.println("group " + k + " with count: " + v));
        System.out.println("---------------------------");
        // 分组求和
        Stream<Item> s6 = items_supplier.get();
        Map<String, Long> itemGroupSum = s6.collect(Collectors.groupingBy(Item::getCategory, Collectors.summingLong(Item::getValue)));
        itemGroupSum.forEach((k, v) -> System.out.println("group " + k + " with sum: " + v));
        System.out.println("---------------------------");

        // 分组求count, sum, mean, min, max
        Stream<Item> s7 = items_supplier.get();
        // LongSummaryStatistics 是一个专门进行Long数值聚合并记录结果的类，由收集器 Collectors.summarizingLong() 返回
        Map<String, LongSummaryStatistics> itemGroupSummary = s7.collect(
                Collectors.groupingBy(Item::getCategory, Collectors.summarizingLong(Item::getValue))
        );
        itemGroupSummary.forEach(
            (k, v) -> System.out.println(
                        "group '" + k + "' summary: {" +
                        "count: " + v.getCount() +
                        ", sum: " + v.getSum() +
                        ", min: " + v.getMin() +
                        ", max: " + v.getMax() +
                        ", mean: " + v.getAverage() +
                        "}"
                    )
        );

    }

    static class Item {
        private int id;
        private String category;
        private int value;

        public Item(int id, String category, int value) {
            this.id = id;
            this.category = category;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Item{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", value=" + value +
                '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}