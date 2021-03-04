package Hadoop;

import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;


public class MapJoinReducer extends Reducer<Text, TableBean, TableBean, NullWritable> {

    // Map-Side join 里的 Reducer 什么也不做，只是将key-value原样输出
    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
        super.reduce(key, values, context);
    }
}
