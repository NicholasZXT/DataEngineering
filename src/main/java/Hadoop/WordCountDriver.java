package Hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{

//        1. 获取Job对象
        Configuration config = new Configuration();
//      Job is the primary interface for a user to describe a MapReduce job to the Hadoop framework for execution
//      Job 对象是管理map-reduce任务的对象，所有的配置都从这个对象出发，类似于spark里的SparkContext
        Job job = Job.getInstance(config);

//      Job对象通常用于进行以下配置
//      Job is typically used to specify the:
//      Mapper, combiner (if any), Partitioner, Reducer, InputFormat, OutputFormat implementations

//        2. 设置jar包存储对象
        job.setJarByClass(WordCountDriver.class);

//        3. 关联map和reduce的类
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

//        4. 设置Map阶段输出数据的key，value类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

//        5. 设置Reduce阶段输出数据的key, value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
//        这里也可设置reduceTask的数量，默认为1
        job.setNumReduceTasks(2);

//        6. 设置输入和输出数据路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

//        7. 提交job，并获取返回的任务状态
        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0:1);

    }
}
