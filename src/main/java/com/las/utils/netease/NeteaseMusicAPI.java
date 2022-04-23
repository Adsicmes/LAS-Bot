package com.las.utils.netease;

import com.jfinal.kit.Base64Kit;
import com.las.utils.JsonUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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


public class NeteaseMusicAPI {

    private static String connection(String url, Map<String, Object> data)
            throws Exception {
        StringBuilder response = new StringBuilder();
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        StringBuffer parameterBuffer = new StringBuffer();
        URL curl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) curl.openConnection();
        if (data != null && data.size() > 0) {
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            String REFERER = "http://music.163.com/";
            conn.setRequestProperty("Referer", REFERER);
            String COOKIE = "os=pc; osver=Microsoft-Windows-10-Professional-build-10586-64bit; appver=2.0.3.131777; channel=netease; __remember_me=true";
            conn.setRequestProperty("Cookie", COOKIE);
            String USERAGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.157 Safari/537.36";
            conn.setRequestProperty("User-Agent", USERAGENT);
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
                parameterBuffer.append(key).append("=")
                        .append(URLEncoder.encode(value, "utf-8"));
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
            try {
                out = new OutputStreamWriter(conn.getOutputStream());
                out.write(parameterBuffer.toString());
                out.flush();
                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), StandardCharsets.UTF_8);
                    response.append(lines);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        Map<String, Object> data = new HashMap<String, Object>();
        String NONCE = "0CoJUm6Qyw8W8jud";
        data.put("params", encrypt(jsonEncode(raw), NONCE));
        String secretKey = "TA3YiYCfY2dDJQgg";
        data.put("params", encrypt((String) data.get("params"), secretKey));
        String encSecKey = "84ca47bca10bad09a6b04c5c927ef077d9b9f1e37098aa3eac6ea70eb59df0aa28b691b7e75e4f1f9831754919ea784c8f74fbfadf2898b0be17849fd656060162857830e241aba44991601f137624094c114ea8d17bce815b0cd4e5b8e2fbaba978c6d1d14dc3d1faf852bdd28818031ccdaaa13a6018e1024e2aae98844210";
        data.put("encSecKey", encSecKey);
        return data;
    }

    private static  String jsonEncode(Object object) {
        return JsonUtils.getJsonString(object);
    }

    private static  String encrypt(String content, String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            String VI = "0102030405060708";
            IvParameterSpec iv = new IvParameterSpec(VI.getBytes());// 创建iv
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return Base64Kit.encode(result); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

    public static String search(String s) {
        int limit = 10;//限制结果数量
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
            e.printStackTrace();
        }
        return null;
    }

}