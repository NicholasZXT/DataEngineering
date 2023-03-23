package HadoopDemos;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

// ReduceJoin 和 MapJoin 使用的 Bean 对象
public class TableBean implements Writable{

    // bean对象里的字段
    private String id = "";
    private String city = "";
    private String statyear = "";
    private int num = 0;
    private String source = "";  // 用于区分来自不同表的记录

    // 空参构造函数，用于反序列化
    public TableBean(){
        super();
    }

    // 有参构造函数
    public TableBean(String id, String city, String statyear, int num, String source){
        super();
        this.id = id;
        this.city = city;
        this.statyear = statyear;
        this.num = num;
        this.source = source;
    }

    // 序列化方法
    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(id);
        output.writeUTF(city);
        output.writeUTF(statyear);
        output.writeInt(num);
        output.writeUTF(source);
    }

    // 反序列化方法
    @Override
    public void readFields(DataInput input) throws IOException {
        this.id = input.readUTF();
        this.city = input.readUTF();
        this.statyear = input.readUTF();
        this.num = input.readInt();
        this.source = input.readUTF();
    }

    // toString 方法，用于打印
    @Override
    public String toString() {
        return id + "\t" + city + "\t" + statyear + "\t" + num;
    }

    // get和set方法


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStatyear() {
        return statyear;
    }

    public void setStatyear(String statyear) {
        this.statyear = statyear;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
