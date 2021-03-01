package Hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


// 使用MapReduce实现SQL里的GroupBy
public class GroupByDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        System.out.println("args[0]: " + args[0]);
        System.out.println("args[1]: " + args[1]);

        Configuration config = new Configuration();
        Job job = Job.getInstance(config);

        System.out.println("GroupByDriver.class: " + GroupByDriver.class);
        job.setJarByClass(GroupByDriver.class);
        System.out.println("job.getjar(): " + job.getJar());

        job.setMapperClass(GroupByMapper.class);
        job.setReducerClass(GroupByReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        TextInputFormat.setInputPaths(job, new Path(args[0]));
        TextOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);



    }

}
