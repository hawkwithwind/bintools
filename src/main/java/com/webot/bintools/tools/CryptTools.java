package com.webot.bintools.tools;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptTools {
    private static final Provider PROVIDER_BC = new BouncyCastleProvider();

    private static List<String> paddings = new ArrayList<String>(Arrays.asList("NoPadding", "PKCS1Padding", "PKCS5Padding", "PKCS7Padding", "PKCS12Padding"));
    
    /**
     * 使用AES-GCM算法进行加密
     * @param key   密钥
     * @param nonce 向量
     * @param aad   关联数据
     * @param data  需要加密的数据
     * @return 加密后的数据
     */
    public static byte[] aesGcmEncryptData(byte[] key, byte[] nonce, byte[] aad, byte[] data)
        throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", PROVIDER_BC);
        if (nonce != null) {
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        }
        if (aad != null) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(data);
    }

    /**
     * 使用AES-GCM算法进行解密
     * @param key   密钥
     * @param nonce 向量
     * @param aad   关联数据
     * @param data  需要解密的数据
     * @return 解密后的数据
     */
    public static byte[] aesGcmDecryptData(byte[] key, byte[] nonce, byte[] aad, byte[] data)
    throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", PROVIDER_BC);
        if (nonce != null) {
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        }
        if (aad != null) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(data);
    }

    /**
     * 使用AES-CBC算法进行加密
     * @param key  密钥
     * @param iv   向量
     * @param data 需要加密的数据
     * @return 加密后的数据
     */
    public static byte[] aesCbcEncryptData(String padding, byte[] key, byte[] iv, byte[] data)
    throws Exception {

        if(!paddings.contains(padding)) {
            throw new Exception("不支持 " + "AES/CBC/" + padding);
        }
        
        Cipher cipher = Cipher.getInstance("AES/CBC/" + padding, PROVIDER_BC);
        if (iv != null) {
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        }
        return cipher.doFinal(data);
    }

    /**
     * 使用AES-CBC算法进行解密
     * @param key  密钥
     * @param iv   向量
     * @param data 需要解密的数据
     * @return 解密后的数据
     */
    public static byte[] aesCbcDecryptData(String padding, byte[] key, byte[] iv, byte[] data)
    throws Exception {
        if(!paddings.contains(padding)) {
            throw new Exception("不支持 " + "AES/CBC/" + padding);
        }
        
        Cipher cipher = Cipher.getInstance("AES/CBC/" + padding, PROVIDER_BC);
        if (iv != null) {
            IvParameterSpec parameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), parameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
        }
        return cipher.doFinal(data);
    }
}
