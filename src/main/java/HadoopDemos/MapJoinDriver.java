package HadoopDemos;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapJoinDriver {


    // file:///D:/Projects/DataEngeering/hadoop_data/join_input/left_table.txt
    // D:\Projects\DataEngeering\hadoop_data\join_input\right_table.txt
    // D:\Projects\DataEngeering\hadoop_data\join_output
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        Configuration config = new Configuration();
        Job job = Job.getInstance(config);

        job.setJarByClass(ReduceJoinDriver.class);

        job.setMapperClass(ReduceJoinMapper.class);
        // 其实这里的 Map-Side join不需要 Reducer，可以不设置reducer类
        job.setReducerClass(ReduceJoinReducer.class);

        job.setMapOutputKeyClass(Text .class);
        job.setMapOutputValueClass(TableBean.class);

        job.setOutputKeyClass(TableBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]), new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        // 加载并缓存表
        job.addCacheFile(new URI(args[0]));

        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }


}
