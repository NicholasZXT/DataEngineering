package hadoop;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    int sum ;
    IntWritable v = new IntWritable();

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
//        super.reduce(key, values, context);
        sum = 0;
        for (IntWritable count: values){
            sum += count.get();
        }
        v.set(sum);
        context.write(key,v);
    }
}
