package Hadoop;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



public class JoinDriver {

    public static void main(String[] args) throws IOException {

        Configuration config = new Configuration();
        Job job = Job.getInstance(config);

        job.setJarByClass(JoinDriver.class);

        job.setMapperClass(JoinMapper.class);
        job.setReducerClass(JoinReducer.class);

        job.setMapOutputKeyClass();
        job.setMapOutputValueClass();

        job.setOutputKeyClass();
        job.setOutputValueClass();

        job.setInputFormatClass();

    }

}
