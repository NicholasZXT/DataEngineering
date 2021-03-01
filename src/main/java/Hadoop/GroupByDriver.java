package Hadoop;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GroupByDriver {

    public static void main(String[] args) throws IOException {

        Configuration config = new Configuration();
        Job job = Job.getInstance(config);

        job.setJarByClass(GroupByDriver.class);

        job.setMapperClass(GroupByMapper.class);
        job.setReducerClass(GroupByReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass();
        job.setOutputValueClass();

        FileInputFormat.setInputPaths();
        FileOutputFormat.setOutputPath();

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);



    }

}
