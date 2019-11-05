//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Digest {
    private static int DEFAULT_BUFF_SIZE = 1024;
    MessageDigest digest;

    public Digest() {
        this.init((String) null);
    }

    public Digest(ALGORITHM algorithm) {
        this.init(algorithm.getAlgorithm());
    }

    public Digest(String algorithm) {
        this.init(algorithm);
    }

    public static byte[] encode(byte[] data) {
        return encode(data, ALGORITHM.DEFAULT);
    }

    public static byte[] encode(byte[] data, ALGORITHM algorithm) {
        return (new Digest(algorithm)).update(data).digest();
    }

    public static byte[] encode(byte[] data, String algorithm) {
        return (new Digest(algorithm)).update(data).digest();
    }

    public static byte[] encode(InputStream in) throws IOException {
        return encode(in, ALGORITHM.DEFAULT);
    }

    public static byte[] encode(InputStream in, ALGORITHM algorithm) throws IOException {
        Digest d = new Digest(algorithm);
        byte[] data = new byte[DEFAULT_BUFF_SIZE];

        int read;
        while ((read = in.read(data)) != -1) {
            d.update(data, 0, read);
        }

        return d.digest();
    }

    public static byte[] encode(InputStream in, String algorithm) throws IOException {
        Digest d = new Digest(algorithm);
        byte[] data = new byte[DEFAULT_BUFF_SIZE];

        int read;
        while ((read = in.read(data)) != -1) {
            d.update(data, 0, read);
        }

        return d.digest();
    }

    private boolean init(String algorithm) {
        if (algorithm == null) {
            algorithm = ALGORITHM.DEFAULT.getAlgorithm();
        }

        try {
            this.digest = MessageDigest.getInstance(algorithm);
            return true;
        } catch (NoSuchAlgorithmException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(Digest.class.getName()).log(Level.SEVERE, (String) null, var3);
            }

            this.digest = null;
            return false;
        }
    }

    public Digest update(byte input) {
        this.digest.update(input);
        return this;
    }

    public Digest update(byte[] input) {
        this.digest.update(input);
        return this;
    }

    public Digest update(byte[] input, int offset, int len) {
        this.digest.update(input, offset, len);
        return this;
    }

    public byte[] digest() {
        return this.digest.digest();
    }

    public static enum ALGORITHM {
        MD5,
        SHA,
        SHA1,
        SHA256,
        SHA384,
        SHA512;

        public static ALGORITHM DEFAULT = MD5;

        private ALGORITHM() {
        }

        public String getAlgorithm() {
            switch (this) {
                case MD5:
                    return "MD5";
                case SHA:
                    return "SHA";
                case SHA1:
                    return "SHA-1";
                case SHA256:
                    return "SHA-256";
                case SHA384:
                    return "SHA-384";
                case SHA512:
                    return "SHA-512";
                default:
                    return null;
            }
        }
    }
}
