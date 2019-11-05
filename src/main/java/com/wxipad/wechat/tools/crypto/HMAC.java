//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMAC {
    private static int DEFAULT_BUFF_SIZE = 1024;
    private Mac mac;

    public HMAC(String key) {
        this.init(key, (String) null);
    }

    public HMAC(String key, ALGORITHM algorithm) {
        this.init(key, algorithm.getAlgorithm());
    }

    public HMAC(String key, String algorithm) {
        this.init(key, algorithm);
    }

    public static String generateKey() {
        return generateKey((ALGORITHM) null);
    }

    public static String generateKey(ALGORITHM algorithm) {
        if (algorithm == null) {
            algorithm = ALGORITHM.DEFAULT;
        }

        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.getAlgorithm());
            keyGenerator.init(secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            return BASE64.encode(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(HMAC.class.getName()).log(Level.SEVERE, (String) null, var4);
            }

            return null;
        }
    }

    public static byte[] encode(byte[] data, String key) {
        return encode(data, key, ALGORITHM.DEFAULT);
    }

    public static byte[] encode(byte[] data, String key, ALGORITHM algorithm) {
        return (new HMAC(key, algorithm)).update(data).doFinal();
    }

    public static byte[] encode(byte[] data, String key, String algorithm) {
        return (new HMAC(key, algorithm)).update(data).doFinal();
    }

    public static byte[] encode(InputStream in, String key) throws IOException {
        return encode(in, key, ALGORITHM.DEFAULT);
    }

    public static byte[] encode(InputStream in, String key, ALGORITHM algorithm) throws IOException {
        HMAC h = new HMAC(key, algorithm);
        byte[] data = new byte[DEFAULT_BUFF_SIZE];

        int read;
        while ((read = in.read(data)) != -1) {
            h.update(data, 0, read);
        }

        return h.doFinal();
    }

    public static byte[] encode(InputStream in, String key, String algorithm) throws IOException {
        HMAC h = new HMAC(key, algorithm);
        byte[] data = new byte[DEFAULT_BUFF_SIZE];

        int read;
        while ((read = in.read(data)) != -1) {
            h.update(data, 0, read);
        }

        return h.doFinal();
    }

    private boolean init(String key, String algorithm) {
        if (algorithm == null) {
            algorithm = ALGORITHM.DEFAULT.getAlgorithm();
        }

        try {
            SecretKey secretKey = new SecretKeySpec(BASE64.decode(key), algorithm);
            this.mac = Mac.getInstance(algorithm);
            this.mac.init(secretKey);
            return true;
        } catch (InvalidKeyException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(HMAC.class.getName()).log(Level.SEVERE, (String) null, var4);
            }
        } catch (NoSuchAlgorithmException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(HMAC.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        }

        this.mac = null;
        return false;
    }

    public HMAC update(byte input) {
        this.mac.update(input);
        return this;
    }

    public HMAC update(byte[] input) {
        this.mac.update(input);
        return this;
    }

    public HMAC update(byte[] input, int offset, int len) {
        this.mac.update(input, offset, len);
        return this;
    }

    public byte[] doFinal() {
        return this.mac.doFinal();
    }

    public static enum ALGORITHM {
        HmacMD5,
        HmacSHA1,
        HmacSHA256,
        HmacSHA384,
        HmacSHA512;

        public static ALGORITHM DEFAULT = HmacMD5;

        private ALGORITHM() {
        }

        public String getAlgorithm() {
            switch (this) {
                case HmacMD5:
                    return "HmacMD5";
                case HmacSHA1:
                    return "HmacSHA1";
                case HmacSHA256:
                    return "HmacSHA256";
                case HmacSHA384:
                    return "HmacSHA384";
                case HmacSHA512:
                    return "HmacSHA512";
                default:
                    return null;
            }
        }
    }
}
