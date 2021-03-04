package Hadoop;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapJoinMapper extends Mapper<LongWritable, Text, Text, TableBean>{

    // 用于存储缓存的表记录，这里缓存的是左表记录
    ArrayList<TableBean> left_records = new ArrayList<>();
    Text key_out = new Text();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //super.setup(context);
        URI[] cacheFiles = context.getCacheFiles();
        String path = cacheFiles[0].getPath().toString();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        String line;
        while (StringUtils.isNotEmpty(line=reader.readLine())){
            String[] fields = line.split("");
            TableBean bean = new TableBean();
            bean.setId(fields[0]);
            bean.setNum(Integer.parseInt(fields[1]));
            left_records.add(bean);
        }
        reader.close();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //super.map(key, value, context);
        String line = value.toString();
        String[] fields = line.split("");
        for(TableBean left_bean: left_records){
            if(fields[0].equals(left_bean.getId())){
                TableBean bean = new TableBean();
                bean.setId(fields[0]);
                bean.setCity(fields[1]);
                bean.setStatyear(fields[2]);
                bean.setNum(left_bean.getNum());

                key_out.set(fields[0]);
                context.write(key_out, bean);
            }
        }
    }
}
