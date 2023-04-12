package HadoopDemos;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class ReduceJoinDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // 输入为：
        // src/main/resources/hadoop_data/join_input/left_table.txt
        // src/main/resources/hadoop_data/join_input/right_table.txt
        System.out.println("args[0]: " + args[0]);
        System.out.println("args[1]: " + args[1]);
        System.out.println("args[2]: " + args[2]);
        Path input_left_path = new Path(args[0]);
        Path input_right_path = new Path(args[1]);
        Path output_path = new Path(args[2]);

        Configuration config = new Configuration();
        Job job = Job.getInstance(config);

        job.setJarByClass(ReduceJoinDriver.class);

        job.setMapperClass(ReduceJoinMapper.class);
        job.setReducerClass(ReduceJoinReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TableBean.class);

        job.setOutputKeyClass(TableBean.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, input_left_path, input_right_path);
        FileOutputFormat.setOutputPath(job, output_path);

        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);

    }

}
