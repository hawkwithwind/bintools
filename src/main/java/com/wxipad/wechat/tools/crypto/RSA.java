//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RSA {
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;
    private Cipher cipher;
    private Signature signature;
    private MODE mode;

    public RSA(String key, MODE mode) {
        this.init(key, mode, (String) null);
    }

    public RSA(String key, MODE mode, SIGNATURE_ALGORITHM algorithm) {
        this.init(key, mode, algorithm.getAlgorithm());
    }

    public RSA(String key, MODE mode, String algorithm) {
        this.init(key, mode, algorithm);
    }

    public static Keys generateKeys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            Keys keys = new Keys();
            keys.publicKey = BASE64.encode(keyPair.getPublic().getEncoded());
            keys.privateKey = BASE64.encode(keyPair.getPrivate().getEncoded());
            return keys;
        } catch (NoSuchAlgorithmException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var3);
            }

            return null;
        }
    }

    public static byte[] encryptByPrivateKey(byte[] data, String key) {
        return (new RSA(key, MODE.PRIVATE_ENCRYPT)).doFinal(data);
    }

    public static byte[] decryptByPrivateKey(byte[] data, String key) {
        return (new RSA(key, MODE.PRIVATE_DECRYPT)).doFinal(data);
    }

    public static byte[] encryptByPublicKey(byte[] data, String key) {
        return (new RSA(key, MODE.PUBLIC_ENCRYPT)).doFinal(data);
    }

    public static byte[] decryptByPublicKey(byte[] data, String key) {
        return (new RSA(key, MODE.PUBLIC_DECRYPT)).doFinal(data);
    }

    public static String signByPrivateKey(byte[] data, String key) {
        return signByPrivateKey(data, key, SIGNATURE_ALGORITHM.DEFAULT);
    }

    public static String signByPrivateKey(byte[] data, String key, SIGNATURE_ALGORITHM algorithm) {
        RSA rsa = new RSA(key, MODE.PRIVATE_SIGN, algorithm);
        rsa.update(data);
        return rsa.sign();
    }

    public static String signByPrivateKey(byte[] data, String key, String algorithm) {
        RSA rsa = new RSA(key, MODE.PRIVATE_SIGN, algorithm);
        rsa.update(data);
        return rsa.sign();
    }

    public static boolean verifyByPublicKey(byte[] data, String key, String sign) {
        return verifyByPublicKey(data, key, sign, SIGNATURE_ALGORITHM.DEFAULT);
    }

    public static boolean verifyByPublicKey(byte[] data, String key, String sign, SIGNATURE_ALGORITHM algorithm) {
        RSA rsa = new RSA(key, MODE.PUBLIC_VERIFY, algorithm);
        rsa.update(data);
        return rsa.verify(sign);
    }

    public static boolean verifyByPublicKey(byte[] data, String key, String sign, String algorithm) {
        RSA rsa = new RSA(key, MODE.PUBLIC_VERIFY, algorithm);
        rsa.update(data);
        return rsa.verify(sign);
    }

    private boolean init(String key, MODE mode, String algorithm) {
        try {
            if (mode == null) {
                this.mode = mode = MODE.DEFAULT;
            }

            if (algorithm == null) {
                algorithm = SIGNATURE_ALGORITHM.DEFAULT.getAlgorithm();
            }

            byte[] keyBytes = BASE64.decode(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            switch (mode) {
                case PRIVATE_SIGN:
                    PrivateKey priKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                    this.signature = Signature.getInstance(algorithm);
                    this.signature.initSign(priKey);
                    break;
                case PUBLIC_VERIFY:
                    PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
                    this.signature = Signature.getInstance(algorithm);
                    this.signature.initVerify(pubKey);
                    break;
                case PRIVATE_ENCRYPT:
                    PrivateKey priKey2 = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                    this.cipher = Cipher.getInstance(keyFactory.getAlgorithm());
                    this.cipher.init(1, priKey2);
                    break;
                case PRIVATE_DECRYPT:
                    PrivateKey priKey3 = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                    this.cipher = Cipher.getInstance(keyFactory.getAlgorithm());
                    this.cipher.init(2, priKey3);
                    break;
                case PUBLIC_ENCRYPT:
                    PublicKey pubKey2 = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
                    this.cipher = Cipher.getInstance(keyFactory.getAlgorithm());
                    this.cipher.init(1, pubKey2);
                    break;
                case PUBLIC_DECRYPT:
                    PublicKey pubKey3 = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
                    this.cipher = Cipher.getInstance(keyFactory.getAlgorithm());
                    this.cipher.init(2, pubKey3);
            }

            return true;
        } catch (NoSuchPaddingException var12) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var12);
            }
        } catch (InvalidKeyException var13) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var13);
            }
        } catch (InvalidKeySpecException var14) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var14);
            }
        } catch (NoSuchAlgorithmException var15) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var15);
            }
        }

        this.cipher = null;
        this.signature = null;
        return false;
    }

    public byte[] update(byte[] input) {
        if (MODE.isCrypto(this.mode)) {
            return this.cipher.update(input);
        } else {
            try {
                this.signature.update(input);
            } catch (SignatureException var3) {
                if (ConstFramework.getDebug()) {
                    Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var3);
                }
            }

            return null;
        }
    }

    public byte[] update(byte[] input, int offset, int len) {
        if (MODE.isCrypto(this.mode)) {
            return this.cipher.update(input, offset, len);
        } else {
            try {
                this.signature.update(input, offset, len);
            } catch (SignatureException var5) {
                if (ConstFramework.getDebug()) {
                    Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var5);
                }
            }

            return null;
        }
    }

    public byte[] doFinal(byte[] input) {
        try {
            return this.cipher.doFinal(input);
        } catch (IllegalBlockSizeException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var3);
            }
        } catch (BadPaddingException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var4);
            }
        }

        return null;
    }

    public byte[] doFinal(byte[] input, int offset, int len) {
        try {
            return this.cipher.doFinal(input, offset, len);
        } catch (IllegalBlockSizeException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        } catch (BadPaddingException var6) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var6);
            }
        }

        return null;
    }

    public String sign() {
        try {
            return BASE64.encode(this.signature.sign());
        } catch (SignatureException var2) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var2);
            }

            return null;
        }
    }

    public boolean verify(String sign) {
        try {
            return this.signature.verify(BASE64.decode(sign));
        } catch (SignatureException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, (String) null, var3);
            }

            return false;
        }
    }

    public static enum MODE {
        PRIVATE_SIGN,
        PUBLIC_VERIFY,
        PRIVATE_ENCRYPT,
        PRIVATE_DECRYPT,
        PUBLIC_ENCRYPT,
        PUBLIC_DECRYPT;

        public static MODE DEFAULT = PRIVATE_SIGN;

        private MODE() {
        }

        public static boolean isCrypto(MODE mode) {
            return mode == PRIVATE_ENCRYPT || mode == PRIVATE_DECRYPT || mode == PUBLIC_ENCRYPT || mode == PUBLIC_DECRYPT;
        }
    }

    public static enum SIGNATURE_ALGORITHM {
        MD5withRSA,
        SHA1withRSA,
        SHA256withRSA,
        SHA384withRSA,
        SHA512withRSA;

        public static SIGNATURE_ALGORITHM DEFAULT = MD5withRSA;

        private SIGNATURE_ALGORITHM() {
        }

        public String getAlgorithm() {
            switch (this) {
                case MD5withRSA:
                    return "MD5withRSA";
                case SHA1withRSA:
                    return "SHA1withRSA";
                case SHA256withRSA:
                    return "SHA256withRSA";
                case SHA384withRSA:
                    return "SHA384withRSA";
                case SHA512withRSA:
                    return "SHA512withRSA";
                default:
                    return null;
            }
        }
    }

    public static class Keys {
        public String publicKey;
        public String privateKey;

        public Keys() {
        }
    }
}
