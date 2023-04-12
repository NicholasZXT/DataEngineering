package SparkDemos;

import java.util.Arrays;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;

public class HelloJavaSpark {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setAppName("Hello Java Spark")
                .set("spark.driver.memory", "1g")
                .set("spark.driver.cores", "1")
                .set("spark.executor.instances", "2")
                .set("spark.executor.cores", "2")
                .set("spark.executor.memory", "2g")
                .set("spark.default.parallelism", "2")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<Integer> array = Arrays.asList(1,2,3,4,5,6,7,8);
        // 返回类型
        JavaRDD<Integer> rdd1 = sc.parallelize(array, 3);
        System.out.println("partition num: " + rdd1.getNumPartitions());
        // 使用 lambda 表达式
        rdd1.foreach(x -> System.out.println(x));

        sc.stop();
    }
}
