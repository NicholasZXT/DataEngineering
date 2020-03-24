package hadoop;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


// 继承的类的签名是：Class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

//    下面这两个变量是存储 Map 过程 输出 的 k-v 对，（不是输入的）
//    这个 k 用于存储输入的文本作为key，是Hadoop自带的序列化类型
//    注意，这个 k 作为 reduce 阶段的key，不仅要能序列化，还要实现 WritableComparable 接口，因为结果要排序
    Text k = new Text();
//    这个 v 的值就是1，只不过因为要序列化，不使用java内部的整型
    IntWritable v = new IntWritable(1);

//  重载的map方法签名是：
//  map(KEYIN key, VALUEIN value, org.apache.hadoop.mapreduce.Mapper.Context context)
//  这个map方法会作用于输入的每一对 key-value
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//        super.map(key, value, context);
//        传入的形参 key 和 value 都是经过序列化之后的对象
        String line = value.toString();
        String[] words = line.split(" ");

        for(String word:words){
//            k 就是类里设置的 Text 对象，用于存储map输出的key
//            这里word是 String类型，要调用 Text 类型的 set 方法才能实现赋值
            k.set(word);
//            调用context对象的write方法写出
            context.write(k,v);
        }

    }
}
