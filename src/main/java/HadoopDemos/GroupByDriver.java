package HadoopDemos;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


// 使用MapReduce实现SQL里的GroupBy
public class GroupByDriver {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // 输入为 src/main/resources/hadoop_data/groupby_input/data.txt
        System.out.println("args[0]: " + args[0]);
        System.out.println("args[1]: " + args[1]);
        Path input_path = new Path(args[0]);
        Path output_path = new Path(args[1]);

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

        FileInputFormat.setInputPaths(job, input_path);
        FileOutputFormat.setOutputPath(job, output_path);
        //TextInputFormat.setInputPaths(job, input_path);
        //TextOutputFormat.setOutputPath(job, output_path);

        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);



    }

}
