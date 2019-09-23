package com.webot.bintools.tools;

import org.apache.commons.codec.binary.Hex;

public class DecodeTools {

    public static byte[] decodeHex(String text) throws Exception {
        return Hex.decodeHex(text.toCharArray());
    }

    public static String encodeHex(byte[] buffer) {
        return new String(Hex.encodeHex(buffer));
    }
}
