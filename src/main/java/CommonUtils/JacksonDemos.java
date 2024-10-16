package CommonUtils;

import lombok.Data;
import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 展示 Jackson 的使用
 */
public class JacksonDemos {
    public static void main(String[] args) throws IOException {
        // 入口类，相当于 FastJSON 里的 JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // 序列化
        Person person = new Person();
        person.setName("Nicholas");
        person.setAge(31);
        person.setSkillList(Arrays.asList("Python", "java", "c++"));
        String json = objectMapper.writeValueAsString(person);
        System.out.println(json);
        // 也可以直接写入文件或者数组
        //objectMapper.writeValue(new File("result.json"), person);
        //byte[] jsonBytes = objectMapper.writeValueAsBytes(person);

        // 反序列化
        String expectedJson = "{\"name\":\"Zhou\",\"age\":28,\"skillList\":[\"Python\",\"Scala\"]}";
        Person person_rev = objectMapper.readValue(expectedJson, Person.class);
        System.out.println(person_rev);

    }
}

@Data
class Person {
    private String name;
    private Integer age;
    private List<String> skillList;
}
