package com.las.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dullwolf
 */
public class NetEaseMusicUtil {

    private static Logger logger = Logger.getLogger(NetEaseMusicUtil.class);

    private static String connection(String url, Map<String, Object> data)
            throws Exception {
        StringBuilder response = new StringBuilder();
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        StringBuilder parameterBuffer = new StringBuilder();
        URL curl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
        if (data != null && data.size() > 0) {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Referer", "http://music.163.com/");
            conn.setRequestProperty("Cookie", "os=pc; osver=Microsoft-Windows-10-Professional-build-10586-64bit; appver=2.0.3.131777; channel=netease; __remember_me=true");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.157 Safari/537.36");
            Iterator iterator = data.keySet().iterator();
            String key;
            String value;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (data.get(key) != null) {
                    value = (String) data.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(URLEncoder.encode(value, "utf-8"));
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
            try {
                out = new OutputStreamWriter(conn.getOutputStream());
                out.write(parameterBuffer.toString());
                out.flush();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), StandardCharsets.UTF_8);
                    response.append(lines);
                }
            } catch (Exception e) {
                logger.error("出错ERROR：" + e.getMessage(), e);
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
        return response.toString();
    }

    private static Map<String, Object> prepare(Map<String, Object> raw) {
        Map<String, Object> data = new HashMap<>();
        data.put("params", encrypt(jsonEncode(raw), "0CoJUm6Qyw8W8jud"));
        data.put("params", encrypt((String) data.get("params"), "TA3YiYCfY2dDJQgg"));
        String encSecKey = "84ca47bca10bad09a6b04c5c927ef077d9b9f1e37098aa3eac6ea70eb59df0aa28b691b7e75e4f1f9831754919ea784c8f74fbfadf2898b0be17849fd656060162857830e241aba44991601f137624094c114ea8d17bce815b0cd4e5b8e2fbaba978c6d1d14dc3d1faf852bdd28818031ccdaaa13a6018e1024e2aae98844210";
        data.put("encSecKey", encSecKey);
        return data;
    }

    private static String jsonEncode(Object object) {
        return JsonUtils.getJsonString(object);
    }

    private static String encrypt(String content, String password) {
        return AesUtil.encrypt(content, password);
    }

    public static String url(String id) {
        String url = "http://music.163.com/weapi/song/enhance/player/url?csrf_token=";
        String[] urls = {id};
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", urls);
        map.put("br", 999000);
        map.put("csrf_token", "");
        try {
            return connection(url, prepare(map));
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return null;
    }

    public static String detail(String id) {
        String url = "http://music.163.com/weapi/v3/song/detail?csrf_token=";
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> ids = new HashMap<String, String>();
        ids.put("id", id);
        map.put("c", "[" + jsonEncode(ids) + "]");
        map.put("csrf_token", "");
        try {
            return connection(url, prepare(map));
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return null;
    }

    public static String search(String s) {
        int limit = 10;
        int offset = 0;
        int type = 1;
        String url = "http://music.163.com/weapi/cloudsearch/get/web?csrf_token=";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("s", s);
        map.put("type", type);
        map.put("limit", limit);
        map.put("total", "true");
        map.put("offset", offset);
        map.put("csrf_token", "");
        try {
            return connection(url, prepare(map));
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(), e);
        }
        return null;
    }

}