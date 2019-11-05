//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DH {
    private static final String KEY_ALGORITHM = "DH";
    private static final int KEY_SIZE = 1024;
    private Cipher cipher;

    public DH(String publicKey, String privateKey, MODE mode) {
        this.init(publicKey, privateKey, mode, (String) null);
    }

    public DH(String publicKey, String privateKey, MODE mode, SECRET_ALGORITHM algorithm) {
        this.init(publicKey, privateKey, mode, algorithm.getAlgorithm());
    }

    public DH(String publicKey, String privateKey, MODE mode, String algorithm) {
        this.init(publicKey, privateKey, mode, algorithm);
    }

    public static Keys generateKeys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            Keys keys = new Keys();
            keys.publicKey = BASE64.encode(keyPair.getPublic().getEncoded());
            keys.privateKey = BASE64.encode(keyPair.getPrivate().getEncoded());
            return keys;
        } catch (NoSuchAlgorithmException var3) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var3);
            }

            return null;
        }
    }

    public static Keys generateKeys(String key) {
        try {
            byte[] keyBytes = BASE64.decode(key);
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Keys keys = new Keys();
            keys.publicKey = BASE64.encode(keyPair.getPublic().getEncoded());
            keys.privateKey = BASE64.encode(keyPair.getPrivate().getEncoded());
            return keys;
        } catch (InvalidAlgorithmParameterException var8) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var8);
            }
        } catch (InvalidKeySpecException var9) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var9);
            }
        } catch (NoSuchAlgorithmException var10) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var10);
            }
        }

        return null;
    }

    public static byte[] encrypt(byte[] data, String publicKey, String privateKey) {
        return encrypt(data, publicKey, privateKey, SECRET_ALGORITHM.DEFAULT);
    }

    public static byte[] encrypt(byte[] data, String publicKey, String privateKey, SECRET_ALGORITHM algorithm) {
        return (new DH(publicKey, privateKey, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String publicKey, String privateKey, String algorithm) {
        return (new DH(publicKey, privateKey, MODE.ENCRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String publicKey, String privateKey) {
        return decrypt(data, publicKey, privateKey, SECRET_ALGORITHM.DEFAULT);
    }

    public static byte[] decrypt(byte[] data, String publicKey, String privateKey, SECRET_ALGORITHM algorithm) {
        return (new DH(publicKey, privateKey, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String publicKey, String privateKey, String algorithm) {
        return (new DH(publicKey, privateKey, MODE.DECRYPT, algorithm)).doFinal(data);
    }

    private boolean init(String publicKey, String privateKey, MODE mode, String algorithm) {
        if (mode == null) {
            mode = MODE.DEFAULT;
        }

        if (algorithm == null) {
            algorithm = SECRET_ALGORITHM.DEFAULT.getAlgorithm();
        }

        try {
            byte[] pubKeyBytes = BASE64.decode(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            byte[] priKeyBytes = BASE64.decode(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
            keyAgree.init(priKey);
            keyAgree.doPhase(pubKey, true);
            SecretKey secretKey = keyAgree.generateSecret(algorithm);
            this.cipher = Cipher.getInstance(algorithm);
            this.cipher.init(mode.getMode(), secretKey);
            return true;
        } catch (NoSuchPaddingException var14) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var14);
            }
        } catch (InvalidKeyException var15) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var15);
            }
        } catch (InvalidKeySpecException var16) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var16);
            }
        } catch (NoSuchAlgorithmException var17) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var17);
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
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var3);
            }
        } catch (BadPaddingException var4) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var4);
            }
        }

        return null;
    }

    public byte[] doFinal(byte[] input, int offset, int len) {
        try {
            return this.cipher.doFinal(input, offset, len);
        } catch (IllegalBlockSizeException var5) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        } catch (BadPaddingException var6) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DH.class.getName()).log(Level.SEVERE, (String) null, var6);
            }
        }

        return null;
    }

    public static enum SECRET_ALGORITHM {
        DES,
        DESede,
        AES,
        Blowfish;

        public static SECRET_ALGORITHM DEFAULT = DES;

        private SECRET_ALGORITHM() {
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
                default:
                    return null;
            }
        }
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

    public static class Keys {
        public String publicKey;
        public String privateKey;

        public Keys() {
        }
    }
}
