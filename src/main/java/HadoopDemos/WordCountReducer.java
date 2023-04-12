package HadoopDemos;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


// Reducer类的签名：Class Reducer<KEYIN,VALUEIN,KEYOUT,VALUEOUT>
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    //下面的这两个变量用于存储 reduce 的输出
    int sum ;
    IntWritable v = new IntWritable();

    //  reduce方法的签名：
    //reduce(KEYIN key, Iterable<VALUEIN> values, org.apache.hadoop.mapreduce.Reducer.Context context)
    //这个reduce方法 对于 每个 key 调用一次，注意，是每个key调用一次！！！！
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        //super.reduce(key, values, context);
        //由于reduce方法是对每个key调用一次，每个key对应的多个值被组成了一个 Iterable<IntWritable> 对象，
        sum = 0;
        for (IntWritable count: values){
            sum += count.get();
        }
        //要用IntWritable的set方法设置值
        v.set(sum);
        //使用context对象的write方法写入
        context.write(key,v);
    }
}
