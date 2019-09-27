package com.webot.bintools.tools;

import org.apache.commons.codec.binary.Hex;
import java.util.Base64; 

public class DecodeTools {
    public static byte[] decodeHex(String text) throws Exception {
        return Hex.decodeHex(text.toCharArray());
    }

    public static String encodeHex(byte[] buffer) {
        return new String(Hex.encodeHex(buffer));
    }

    public static byte[] decodeBase64(String text) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(text);
    }

    public static String encodeBase64(byte[] buffer) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(buffer);
    }
}
