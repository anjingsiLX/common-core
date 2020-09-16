package top.doudou.commons.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author chensen
 * @create 2018-09-17-16:23
 */
public class JsonUtils {
    private JsonUtils() {
    }

    /**
     * 字符串转换为jsonnode
     *
     * @param json
     * @return
     * @throws IOException
     */
    public static JsonNode parseToJsonNode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.isBlank(json)) {
            return objectMapper.createObjectNode();
        } else {
            try {
                return objectMapper.readTree(json);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 对象转换为json
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator;
        try {
            jsonGenerator = objectMapper.getFactory().createGenerator(stringWriter);
            objectMapper.writeValue(jsonGenerator, obj);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return stringWriter.toString();
    }

    /**
     * json字符串转换为对象
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * map转对象
     * @param map
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T fromMap(Map<String,Object> map, Class<T> tClass){
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(map), tClass);
    }


}
