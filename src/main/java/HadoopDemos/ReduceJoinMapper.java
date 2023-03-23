package HadoopDemos;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;

public class ReduceJoinMapper extends Mapper<LongWritable, Text, Text, TableBean> {

    String filename;
    TableBean bean = new TableBean();
    Text key_out = new Text();

    // 重写 set 方法，获取文件信息
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
//        super.setup(context);
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        filename = fileSplit.getPath().getName();

    }

    // 重写 map 方法
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //super.map(key, value, context);
        String line = value.toString();

        if (filename.startsWith("left")){
            // 左表的记录
            String[] fields = line.split(" ");
            bean.setId(fields[0]);
            bean.setNum(Integer.parseInt(fields[1]));
            bean.setSource("left");

            key_out.set(fields[0]);
        } else {
            // 右表的记录
            String[] fields = line.split(" ");
            bean.setId(fields[0]);
            bean.setCity(fields[1]);
            bean.setStatyear(fields[2]);
            bean.setSource("right");

            key_out.set(fields[0]);
        }
        context.write(key_out, bean);

    }
}
