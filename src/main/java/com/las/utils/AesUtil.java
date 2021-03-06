package com.las.utils;

import com.jfinal.kit.Base64Kit;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author dullwolf
 */
public final class AesUtil {

    private static Logger logger = Logger.getLogger(AesUtil.class);

    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY = "TA3YiYCfY2dDJQgg";
    private static final String IV = "0102030405060708";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密
     *
     */
    public static String encrypt(String content) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return Base64Kit.encode(result);
    }

    /**
     * 加密
     *
     */
    public static String encrypt(String content,String password) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return Base64Kit.encode(result);
    }

    /**
     * 解密
     *
     */
    public static String decrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(CHARSET_NAME), AES_NAME);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(Base64Kit.decode(content)), CHARSET_NAME);
        } catch (Exception e) {
            logger.error("出错ERROR：" + e.getMessage(),e);
        }
        return "";
    }
}
