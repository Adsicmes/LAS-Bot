package com.las.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {

    private static final int MAX_TIME = 15000;

    public static String doPost(String url, String jsonText) {
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String result = "";
        try {
            StringEntity entity = new StringEntity(jsonText, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
            }
        } catch (IOException ignored) {
        } finally {
            try {
                httpPost.releaseConnection();
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static CloseableHttpClient getClient() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(MAX_TIME)
                .setConnectTimeout(MAX_TIME)
                .setConnectionRequestTimeout(MAX_TIME)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
    }


    public static String doGet(String url) {
        String result = "";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(httpGet, httpClient, response);
        }
        return result;
    }

    public static String doGetByToken(String url, String header, String token) {
        String result = "";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = getClient();
        CloseableHttpResponse response = null;
        try {
            httpGet.setHeader(header, token);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(httpGet, httpClient, response);
        }
        return result;
    }


    private static void close(HttpGet httpGet, CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (httpClient != null) {
                httpGet.releaseConnection();
            }
            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
