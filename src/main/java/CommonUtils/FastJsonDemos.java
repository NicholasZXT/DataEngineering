package CommonUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * FastJSON的基本使用API如上所示：
 * (1) JSON 类是最常用的入口，常用方法如下：
 *   序列化方法（有各种重载形式）：
 *    + public static String toJSONString(Object object, SerializerFeature... features)
 *      将Java对象序列化为JSON字符串，支持各种各种Java基本类型和JavaBean
 *    + public static byte[] toJSONBytes(Object object, SerializerFeature... features)
 *      将Java对象序列化为JSON字符串，返回JSON字符串的utf-8 bytes
 *    + public static void writeJSONString(Writer writer, Object object, SerializerFeature... features);
 *      将Java对象序列化为JSON字符串，写入到Writer中
 *    + public static final int writeJSONString(OutputStream os, Object object, SerializerFeature... features);
 *      将Java对象序列化为JSON字符串，按UTF-8编码写入到OutputStream中
 *  反序列化方法（常用）：
 *    + public static <T> T parseObject(String jsonStr, Class<T> clazz, Feature... features);
 *      将JSON字符串反序列化为JavaBean
 *    + public static JSONObject parseObject(String text);
 *      将JSON字符串反序列为JSONObject
 *
 * (2) JSONObject: 继承了JSON类，相当于一个JSON对象，本质上是一个Map<String, Object>, 提供了更为丰富便捷的方法，方便对象属性的操作。
 * (3) JSONArray: 也继承了JSON类，相当于一个JSON数组，本质上是一个 List<Object>
 * (4) JSONPath: 1.2.0之后的版本新增，功能比较强大，可以对JSON对象进行一些查询操作
 * (5) annotation.JSONField, 注解，用于在 Bean 对象中配置各个字段的序列化/反序列化行为
 * (6) parser.Feature, 枚举类，用于在 JSON.parseObject() 方法中配置各种反序列化时的行为
 * (7) serializer.SerializerFeature，枚举类，用于在 JSON.toJSONString() 方法中配置序列化时的行为
 */

public class FastJsonDemos {
    public static void main(String[] args) {
        String json_str1 = "{\"k1\": \"v1\", \"k2\": 2, \"k3\": \"v3\"}";
        String json_str2 = "[{\"k1\": \"v1\", \"k2\": 2}, {\"k3\": \"v3\", \"k4\": 4}]";

        JSONObject json_obj = JSON.parseObject(json_str1);
        System.out.println(json_obj);
        System.out.println(json_obj.size());
        System.out.println(json_obj.getString("k1"));
        System.out.println(json_obj.getIntValue("k2"));
        System.out.println(json_obj.getString("k4"));  // 没有的key会直接返回null，而不是抛异常

        JSONArray json_array = JSON.parseArray(json_str2);
        System.out.println(json_array);
        System.out.println(json_array.size());
        System.out.println(json_array.getJSONObject(0));
        System.out.println(json_array.getJSONObject(0).getString("k1"));

    }
}
