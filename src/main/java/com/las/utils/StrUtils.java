package com.las.utils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils extends StrKit {
    private static final Log log = Log.getLog(StrUtils.class);

    public static String urlDecode(String string) {
        try {
            return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode is error", e);
        }
        return string;
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncode is error", e);
        }
        return string;
    }

    public static String urlRedirect(String redirect) {
        try {
            redirect = new String(redirect.getBytes(JFinal.me().getConstants().getEncoding()), "ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            log.error("urlRedirect is error", e);
        }
        return redirect;
    }

    public static boolean areNotEmpty(String... strings) {
        if (strings == null || strings.length == 0)
            return false;

        for (String string : strings) {
            if (string == null || "".equals(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 不是空数据，注意：空格不是空数据
     *
     * @param string
     * @return
     */
    public static boolean isNotEmpty(String string) {
        return string != null && !string.equals("");
    }


    /**
     * 确保不是空白字符串
     *
     * @param o
     * @return
     */
    public static boolean isNotBlank(Object o) {
        return o == null ? false : notBlank(o.toString());
    }


    /**
     * 字符串是否匹配某个正则
     *
     * @param string
     * @param regex
     * @return
     */
    public static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * 这个字符串是否是全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null)
            return false;
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * 是否是邮件的字符串
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }


    /**
     * 是否是中国地区手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumber(String phoneNumber) {
        return Pattern.matches("^(1[3,4,5,7,8,9])\\d{9}$", phoneNumber);
    }


    /**
     * 生成一个新的UUID
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 去除特殊字符
     *
     * @param string
     * @return
     */
    public static String clearSpecialCharacter(String string) {
        if (isBlank(string)) {
            return string;
        }

        /**
         P：标点字符；
         L：字母；
         M：标记符号（一般不会单独出现）；
         Z：分隔符（比如空格、换行等）；
         S：符号（比如数学符号、货币符号等）；
         N：数字（比如阿拉伯数字、罗马数字等）；
         C：其他字符
         */
//        return string.replaceAll("[\\pP\\pZ\\pM\\pC]", "");
        return string.replaceAll("[\\\\\'\"\\/\f\n\r\t]", "");
    }


    /**
     * 把字符串拆分成一个set
     *
     * @param src
     * @param regex
     * @return
     */
    public static Set<String> splitToSet(String src, String regex) {
        if (src == null) {
            return null;
        }

        String[] strings = src.split(regex);
        Set<String> set = new HashSet<>();
        for (String table : strings) {
            if (StrUtils.isBlank(table)) {
                continue;
            }
            set.add(table.trim());
        }
        return set;
    }

}