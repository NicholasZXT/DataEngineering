package SparkDemos

import scala.beans.BeanProperty
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.JSON

class MessageBean {
  @BeanProperty
  var mid: Int = null

  @BeanProperty
  var content: String = null

  @BeanProperty
  var datetime: String = null

  def dumpToJsonString: String = {
    // 设置输出空值
    val json_str = JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    json_str
  }
}
