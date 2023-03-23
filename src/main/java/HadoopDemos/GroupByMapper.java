package HadoopDemos;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;

import java.io.IOException;

// 使用的是TextInputFormat类输入，所以默认的key是偏移量，value是这一行的内容
// Mapper的输出key是GroupBy的分组字段，为 Text 类型，value每组的值，这里选择的是计算均值
public class GroupByMapper extends Mapper<LongWritable, Text, Text, DoubleWritable>{

    // 重写map方法
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//        父类的map方法会直接将 key-value 写出
//        super.map(key, value, context);
        // 将 Text 类型转成 String
        String line = value.toString();
        String[] words = line.split(" ");
        // Mapper 里其实不用做什么，只需要从每一行中提取 groupby 的分组字段和需要聚合的字段值就行，实际的聚合过程放在 reducer 里完成
        String key_out = words[0];
        double value_out = Double.parseDouble(words[1]);
        context.write(new Text(key_out), new DoubleWritable(value_out));
    }
}
