package FlinkDemos.beans;

import java.util.Objects;


/**
 * 演示Flink操作时使用的Java对象，它需要满足如下几个条件：
 *  1. 公有类
 *  2. 有一个无参构造方法
 *  3. 所有属性都是公有
 *  4. 所有属性的类型都可以序列化
 * 这样的类会被 Flink 看做一个 POJO(Plain Ordinary Java Object)，自动推断序列化信息，方便数据的解析和序列化
 */
public class WaterSensor {
    public String id;
    public Double ts;
    public Integer vc;

    public WaterSensor() {
    }

    public WaterSensor(String id, Double ts, Integer vc) {
        this.id = id;
        this.ts = ts;
        this.vc = vc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTs() {
        return ts;
    }

    public void setTs(Double ts) {
        this.ts = ts;
    }

    public Integer getVc() {
        return vc;
    }

    public void setVc(Integer vc) {
        this.vc = vc;
    }

    @Override
    public String toString() {
        return "WaterSensor{" +
                "id='" + id + '\'' +
                ", ts=" + ts +
                ", vc=" + vc +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WaterSensor that = (WaterSensor) o;
        return Objects.equals(id, that.id) && Objects.equals(ts, that.ts) && Objects.equals(vc, that.vc);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, ts, vc);
    }
}
