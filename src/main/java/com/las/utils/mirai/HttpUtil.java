package com.las.utils.mirai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {

    private static final String BOUNDARY = "-------45962402127348";
    private static final String FILE_ENCTYPE = "multipart/form-data";

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);



    /**
     * 读取网络文件
     *
     * @param path 网络路径
     * @return
     */
    public static InputStream getFileInputStream(String path) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            return conn.getInputStream();
        } catch (Exception e) {
            logger.error("读取网络文件异常:" + path);
        }
        return null;
    }

    /**
     * @param urlStr http请求路径
     * @param params 请求参数
     * @param images 上传文件
     * @return
     */
    public static InputStream postFile(String urlStr, Map<String, String> params,
                                   Map<String, File> images) {
        InputStream is = null;

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setConnectTimeout(5000);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", FILE_ENCTYPE + "; boundary="
                    + BOUNDARY);

            StringBuilder sb = null;
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            ;
            if (params != null) {
                sb = new StringBuilder();
                for (String s : params.keySet()) {
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(s);
                    sb.append("\"\r\n\r\n");
                    sb.append(params.get(s));
                    sb.append("\r\n");
                }

                dos.write(sb.toString().getBytes());
            }

            if (images != null) {
                for (String s : images.keySet()) {
                    File f = images.get(s);
                    sb = new StringBuilder();
                    sb.append("--");
                    sb.append(BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data; name=\"");
                    sb.append(s);
                    sb.append("\"; filename=\"");
                    sb.append(f.getName());
                    sb.append("\"\r\n");
                    sb.append("Content-Type: application/zip");//这里注意！如果上传的不是图片，要在这里改文件格式，比如txt文件，这里应该是text/plain
                    sb.append("\r\n\r\n");
                    dos.write(sb.toString().getBytes());

                    FileInputStream fis = new FileInputStream(f);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, len);
                    }
                    dos.write("\r\n".getBytes());
                    fis.close();
                }

                sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("--\r\n");
                dos.write(sb.toString().getBytes());
            }
            dos.flush();

            if (con.getResponseCode() == 200) {
                is = con.getInputStream();
            }

            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }


}
