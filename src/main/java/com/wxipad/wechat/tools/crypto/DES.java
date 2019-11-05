//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DES {
    private Cipher cipher;

    public DES(String key, MODE mode) {
        this.init(key, mode, (String) null);
    }

    public DES(String key, MODE mode, ALGORITHM algorithm) {
        this.init(key, mode, algorithm.getAlgorithm());
    }

    public DES(String key, MODE mode, String algorithm) {
        this.init(key, mode, algorithm);
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
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var4);
            }

            return null;
        }
    }

    public static byte[] encrypt(byte[] data, String key) {
        return encrypt(data, key, ALGORITHM.DEFAULT);
    }

    public static byte[] encrypt(byte[] data, String key, ALGORITHM algorithm) {
        return (new DES(key, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String key, String algorithm) {
        return (new DES(key, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String key) {
        return decrypt(data, key, ALGORITHM.DEFAULT);
    }

    public static byte[] decrypt(byte[] data, String key, ALGORITHM algorithm) {
        return (new DES(key, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String key, String algorithm) {
        return (new DES(key, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    private boolean init(String key, MODE mode, String algorithm) {
        if (algorithm == null) {
            algorithm = ALGORITHM.DEFAULT.getAlgorithm();
        }

        if (mode == null) {
            mode = MODE.DEFAULT;
        }

        try {
            SecretKey secretKey = new SecretKeySpec(BASE64.decode(key), algorithm);
            this.cipher = Cipher.getInstance(algorithm);
            this.cipher.init(mode.getMode(), secretKey);
            return true;
        } catch (NoSuchPaddingException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        } catch (InvalidKeyException var6) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var6);
            }
        } catch (NoSuchAlgorithmException var7) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var7);
            }
        }

        this.cipher = null;
        return false;
    }

    public byte[] update(byte[] input) {
        return this.cipher.update(input);
    }

    public byte[] update(byte[] input, int offset, int len) {
        return this.cipher.update(input, offset, len);
    }

    public byte[] doFinal(byte[] input) {
        try {
            return this.cipher.doFinal(input);
        } catch (IllegalBlockSizeException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var3);
            }
        } catch (BadPaddingException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var4);
            }
        }

        return null;
    }

    public byte[] doFinal(byte[] input, int offset, int len) {
        try {
            return this.cipher.doFinal(input, offset, len);
        } catch (IllegalBlockSizeException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        } catch (BadPaddingException var6) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DES.class.getName()).log(Level.SEVERE, (String) null, var6);
            }
        }

        return null;
    }

    public static enum MODE {
        ENCRYPT,
        DECRYPT;

        public static MODE DEFAULT = ENCRYPT;

        private MODE() {
        }

        public int getMode() {
            switch (this) {
                case ENCRYPT:
                    return 1;
                case DECRYPT:
                    return 2;
                default:
                    return DEFAULT.getMode();
            }
        }
    }

    public static enum ALGORITHM {
        DES,
        DESede,
        AES,
        Blowfish,
        RC2,
        RC4;

        public static ALGORITHM DEFAULT = DES;

        private ALGORITHM() {
        }

        public String getAlgorithm() {
            switch (this) {
                case DES:
                    return "DES";
                case DESede:
                    return "DESede";
                case AES:
                    return "AES";
                case Blowfish:
                    return "Blowfish";
                case RC2:
                    return "RC2";
                case RC4:
                    return "RC4";
                default:
                    return null;
            }
        }
    }
}
