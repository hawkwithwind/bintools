//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PBE {
    private static final int ITERATION_COUNT = 100;
    private Cipher cipher;

    public PBE(String password, byte[] salt, MODE mode) {
        this.init(password, salt, mode, (String) null);
    }

    public PBE(String password, byte[] salt, MODE mode, ALGORITHM algorithm) {
        this.init(password, salt, mode, algorithm.getAlgorithm());
    }

    public PBE(String password, byte[] salt, MODE mode, String algorithm) {
        this.init(password, salt, mode, algorithm);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] encrypt(byte[] data, String password, byte[] salt) {
        return encrypt(data, password, salt, ALGORITHM.DEFAULT);
    }

    public static byte[] encrypt(byte[] data, String password, byte[] salt, ALGORITHM algorithm) {
        return (new PBE(password, salt, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String password, byte[] salt, String algorithm) {
        return (new PBE(password, salt, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String password, byte[] salt) {
        return decrypt(data, password, salt, ALGORITHM.DEFAULT);
    }

    public static byte[] decrypt(byte[] data, String password, byte[] salt, ALGORITHM algorithm) {
        return (new PBE(password, salt, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String password, byte[] salt, String algorithm) {
        return (new PBE(password, salt, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    private boolean init(String password, byte[] salt, MODE mode, String algorithm) {
        try {
            if (algorithm == null) {
                algorithm = ALGORITHM.DEFAULT.getAlgorithm();
            }

            if (mode == null) {
                mode = MODE.DEFAULT;
            }

            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
            this.cipher = Cipher.getInstance(algorithm);
            this.cipher.init(mode.getMode(), secretKey, paramSpec);
            return true;
        } catch (InvalidKeyException var9) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var9);
            }
        } catch (InvalidAlgorithmParameterException var10) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var10);
            }
        } catch (NoSuchPaddingException var11) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var11);
            }
        } catch (InvalidKeySpecException var12) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var12);
            }
        } catch (NoSuchAlgorithmException var13) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var13);
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
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var3);
            }
        } catch (BadPaddingException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var4);
            }
        }

        return null;
    }

    public byte[] doFinal(byte[] input, int offset, int len) {
        try {
            return this.cipher.doFinal(input, offset, len);
        } catch (IllegalBlockSizeException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        } catch (BadPaddingException var6) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(PBE.class.getName()).log(Level.SEVERE, (String) null, var6);
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
        PBEWithMD5AndDES,
        PBEWithMD5AndTripleDES,
        PBEWithSHA1AndDESede,
        PBEWithSHA1AndRC2_40;

        public static ALGORITHM DEFAULT = PBEWithMD5AndDES;

        private ALGORITHM() {
        }

        public String getAlgorithm() {
            switch (this) {
                case PBEWithMD5AndDES:
                    return "PBEWithMD5AndDES";
                case PBEWithMD5AndTripleDES:
                    return "PBEWithMD5AndTripleDES";
                case PBEWithSHA1AndDESede:
                    return "PBEWithSHA1AndDESede";
                case PBEWithSHA1AndRC2_40:
                    return "PBEWithSHA1AndRC2_40";
                default:
                    return null;
            }
        }
    }
}
