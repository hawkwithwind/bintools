package com.webot.bintools.models;

public class SymDecodeRequest {
    public static final String AESCBC = "aes/cbc";

    public static final String BASE64 = "base64";
    public static final String HEX = "hex";
    
    public String method;
    public String codec;
    public String cryptText;
    public String key;
    public String iv;
}
