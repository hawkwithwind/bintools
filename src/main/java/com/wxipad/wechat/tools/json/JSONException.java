package com.wxipad.wechat.tools.json;

public class JSONException
        extends Exception {
    private String errMsg;

    public JSONException(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public String getMessage() {
        return getErrMsg();
    }

    public String toString() {
        return getErrMsg();
    }
}
