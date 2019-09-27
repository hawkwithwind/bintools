package com.webot.bintools.bo;

import com.webot.bintools.tools.DecodeTools;

public class Decoder {
    public static final String BASE64 = "base64";
    public static final String HEX = "hex";

    public static byte[] decode(String codec, String text) throws Exception {
        switch(codec) {
        case HEX:
            return DecodeTools.decodeHex(text);
        case BASE64:
            return DecodeTools.decodeBase64(text);
        default:
            throw new Exception("不支持编码 " + codec);
        }
    }

    public static String encode(String codec, byte[] buffer) throws Exception {
        switch(codec) {
        case HEX:
            return DecodeTools.encodeHex(buffer);
        case BASE64:
            return DecodeTools.encodeBase64(buffer);
        default:
            throw new Exception("不支持编码 " + codec);
        }
    }
}
