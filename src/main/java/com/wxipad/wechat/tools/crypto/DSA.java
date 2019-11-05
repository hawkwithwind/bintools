package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSA {
    private static final String KEY_ALGORITHM = "DSA";
    private static final int KEY_SIZE = 1024;
    private Signature signature;
    private MODE mode;

    public DSA(String key, MODE mode) {
        init(key, mode);
    }

    public static Keys generateKeys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            Keys keys = new Keys();
            keys.publicKey = BASE64.encode(keyPair.getPublic().getEncoded());
            keys.privateKey = BASE64.encode(keyPair.getPrivate().getEncoded());
            return keys;
        } catch (NoSuchAlgorithmException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static String signByPrivateKey(byte[] data, String key) {
        DSA dsa = new DSA(key, MODE.PRIVATE_SIGN);
        dsa.update(data);
        return dsa.sign();
    }

    public static boolean verifyByPublicKey(byte[] data, String key, String sign) {
        DSA dsa = new DSA(key, MODE.PUBLIC_VERIFY);
        dsa.update(data);
        return dsa.verify(sign);
    }

    private boolean init(String key, MODE mode) {
        try {
            if (mode == null) {
                this.mode = (mode = MODE.DEFAULT);
            }
            byte[] keyBytes = BASE64.decode(key);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            switch (mode) {
                case PRIVATE_SIGN:
                    PrivateKey priKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
                    this.signature = Signature.getInstance("DSA");
                    this.signature.initSign(priKey);
                    break;
                case PUBLIC_VERIFY:
                    PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
                    this.signature = Signature.getInstance("DSA");
                    this.signature.initVerify(pubKey);
            }
            return true;
        } catch (InvalidKeyException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InvalidKeySpecException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.signature = null;
        return false;
    }

    public void update(byte[] input) {
        try {
            this.signature.update(input);
        } catch (SignatureException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void update(byte[] input, int offset, int len) {
        try {
            this.signature.update(input, offset, len);
        } catch (SignatureException ex) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String sign() {
        try {
            return BASE64.encode(this.signature.sign());
        } catch (SignatureException ex) {
            if (ConstFramework.getDebug())
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean verify(String sign) {
        try {
            return this.signature.verify(BASE64.decode(sign));
        } catch (SignatureException ex) {
            if (ConstFramework.getDebug())
                Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static enum MODE {
        PRIVATE_SIGN, PUBLIC_VERIFY;
        public static MODE DEFAULT = PRIVATE_SIGN;

        private MODE() {
        }
    }

    public static class Keys {
        public String publicKey;
        public String privateKey;
    }
}
