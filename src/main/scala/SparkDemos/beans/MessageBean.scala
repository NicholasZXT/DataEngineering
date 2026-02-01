package SparkDemos.beans

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature

import scala.beans.BeanProperty

class MessageBean {
  @BeanProperty
  var mid: Int = 0

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
