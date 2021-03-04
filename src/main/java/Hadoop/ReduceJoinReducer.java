package Hadoop;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.commons.beanutils.BeanUtils;

public class ReduceJoinReducer extends Reducer<Text, TableBean, TableBean, NullWritable>{

    @Override
    protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
        //super.reduce(key, values, context);
        ArrayList<TableBean> bean_list = new ArrayList<>();
        TableBean pdBean = new TableBean();

        for(TableBean bean: values){
            if(bean.getSource().equals("left")){
                TableBean leftBean = new TableBean();
                try {
                    BeanUtils.copyProperties(leftBean, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bean_list.add(leftBean);
            }else {
                try {
                    BeanUtils.copyProperties(pdBean, bean);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        for (TableBean bean: bean_list){
            bean.setNum(pdBean.getNum());
            context.write(bean, NullWritable.get());
        }
    }
}
