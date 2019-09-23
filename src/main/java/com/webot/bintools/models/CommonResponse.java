package com.webot.bintools.models;

public class CommonResponse {
    public Object result;
    public String message;

    public CommonResponse() {
    }

    public CommonResponse(Exception e) {
        this.result = null;
        this.message = e.getMessage() + "(" + e.getClass().getCanonicalName() + ")";
        Throwable t = e.getCause();
        if(t != null) {
            this.message += "\n 原因是: " + t.getMessage();
            this.message += "(" + e.getClass().getCanonicalName() + ")";
        }
    }

    public CommonResponse(String m, Object o) {
        this.message = m;
        this.result = o;
    }
}
