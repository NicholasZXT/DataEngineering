package Hadoop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.commons.beanutils.BeanUtils;
import scala.xml.Null;

public class ReduceJoinReducer extends Reducer<Text, TableBean, TableBean, NullWritable>{

    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
        //super.reduce(key, values, context);
        // 用于记录来自左表的记录
        ArrayList<TableBean> left_records = new ArrayList<>();
        // 记录来自右表的记录
        ArrayList<TableBean> right_records = new ArrayList<>();

        // 下面这个 for 循环用于从 key 下的 value list 里，分离出左右表的记录
        for(TableBean bean: values){
            // 检查 bean 对象的 source，判断是来自左表还是右表
            if(bean.getSource().equals("left")){
                TableBean leftBean = new TableBean();
                try {
                    BeanUtils.copyProperties(leftBean, bean);
                    left_records.add(leftBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
            // 来自右表的记录
                TableBean rightBean = new TableBean();
                try {
                    BeanUtils.copyProperties(rightBean, bean);
                    right_records.add(rightBean);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        // 将左右表的记录做 笛卡尔积，这里因为右表的含有的信息多，左表只有一个num，就把左表的num信息来填充右表，然后写出
        for (TableBean leftbean: left_records){
            for (TableBean rightbean: right_records){
                rightbean.setNum(leftbean.getNum());
                context.write(rightbean, NullWritable.get());
            }
        }
    }
}
