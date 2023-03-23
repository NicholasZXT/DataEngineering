package HadoopDemos;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;

import java.io.IOException;


// reducer输入的 key 是 groupby 的 分组字段，values 是每一组的值
// reducer的输出 key 仍然是 groupby 的 分组字段，但是 value 是每一组聚合之后的值
public class GroupByReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
//        super.reduce(key, values, context);
        // 只要对每一组的 values 计算均值即可 —— 奇怪的是，java.Math里居然没有计算均值的方法
        double average = 0.0;
        int len = 0;
        for(DoubleWritable value: values){
            average += value.get();
            len += 1;
        }
        average = average/len;
        context.write(key, new DoubleWritable(average));
    }

}
