package com.las.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dullwolf
 */
public class JsonUtils {

    /**
     * 解析json字符串 转换指定的对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return
     */
    public static <T> T getObjectByJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 解析json字符串 转换指定的列表对象
     *
     * @param json  json字符串
     * @param clazz 列表对象
     * @param <T>   泛型
     * @return
     */
    public static <T> List<T> getArrayByJson(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 解析对象转换为json字符串
     *
     * @param obj 对象
     * @return json字符串
     */
    public static String getJsonString(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    /**
     * getObjectByJson的封装, 把JSON字符串转换成JSONObject
     *
     * @param json JSON字符串
     * @return 转换后的JSONObject
     */
    public static JSONObject getJsonObjectByJsonString(String json) {
        return getObjectByJson(json, JSONObject.class);
    }

    public static List<JSONObject> getJsonArrayByJsonString(String json) {
        return getArrayByJson(json, JSONObject.class);
    }

    /**
     * getJsonObjectByJsonString和getJsonString的封装, 把对象直接转换为JSONObject
     *
     * @param obj 目标对象
     * @return 转换后的JSONObject
     */
    public static JSONObject getJsonObject(Object obj) {
        return getJsonObjectByJsonString(getJsonString(obj));
    }

    public static List<JSONObject> getJSONArray(Object obj) {
        return getJsonArrayByJsonString(getJsonString(obj));
    }


    /**
     * json字符串中下划线转为驼峰
     *
     * @param json json字符串
     * @return 驼峰对象
     */
    public static Object convert(String json) {
        Object obj = JSON.parse(json);
        convert(obj);
        return obj;
    }

    private static void convert(Object json) {
        if (json instanceof JSONArray) {
            JSONArray arr = (JSONArray) json;
            for (Object obj : arr) {
                convert(obj);
            }
        } else if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            Set<String> keys = jo.keySet();
            String[] array = keys.toArray(new String[keys.size()]);
            for (String key : array) {
                Object value = jo.get(key);
                jo.remove(key);
                jo.put(StrKit.toCamelCase(key), value);
                convert(value);
            }
        }
    }

    /**
     * 根据Map转换指定的对象
     *
     * @param map   对象
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return
     */
    public static <T> T getObjectByMap(Map map, Class<T> clazz) {
        String jsonStr = getJsonString(map);
        String json = getJsonString(convert(jsonStr));
        return JSON.parseObject(json, clazz);
    }


    /**
     * getMapByObject的封装, 把对象直接转换为 Map
     *
     * @param obj 目标对象
     * @return 转换后的Map
     */
    public static Map<String, Object> getMapByObject(Object obj) {
        return getJsonObjectByJsonString(getJsonString(obj));
    }

    /**
     * 判断一个JSON字符串的格式是否正确
     *
     * @param content 字符串
     * @return true or false
     */
    public static boolean isJson(String content) {
        try {
            JSONObject.parseObject(content);
            return true;
        } catch (Exception ignored) {

        }
        return false;
    }
}
