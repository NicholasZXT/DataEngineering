package hadoop;

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

        Configuration config = new Configuration();
//      Job is the primary interface for a user to describe a MapReduce job to the Hadoop framework for execution
//      Job 对象是管理map-reduce任务的对象，所有的配置都从这个对象出发，类似于spark里的SparkContext
        Job job = Job.getInstance(config);

//      Job对象通常用于进行以下配置
//      Job is typically used to specify the:
//      Mapper, combiner (if any), Partitioner, Reducer, InputFormat, OutputFormat implementations

        job.setJarByClass(WordCountDriver.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setOutputKeyClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0:1);

    }
}
