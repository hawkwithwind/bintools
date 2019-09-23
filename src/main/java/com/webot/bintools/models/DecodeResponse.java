package com.webot.bintools.models;

public class DecodeResponse {
    public String codec;
    public String data;

    public DecodeResponse() {}

    public DecodeResponse(String c, String d) {
        this.codec = c;
        this.data = d;
    }
}
