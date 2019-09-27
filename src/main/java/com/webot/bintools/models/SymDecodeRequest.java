package com.webot.bintools.models;

public class SymDecodeRequest {
    public static final String AESCBC = "aes/cbc";

    public String method;
    public String codec;
    public String padding;
    public String cryptText;
    public String key;
    public String iv;
}
