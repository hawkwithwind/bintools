//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.json;

import java.util.ArrayList;
import java.util.HashMap;

class JSONWriter {
    private final StringBuilder writerBuff;
    private boolean simple = false;
    private boolean nonc = true;

    public JSONWriter() {
        this.writerBuff = new StringBuilder();
    }

    public JSONWriter(boolean simple) {
        this.simple = simple;
        this.writerBuff = new StringBuilder();
    }

    public JSONWriter(boolean simple, boolean nonc) {
        this.simple = simple;
        this.nonc = nonc;
        this.writerBuff = new StringBuilder();
    }

    public String getBuffString() {
        return this.writerBuff.toString();
    }

    public String toString() {
        return this.getBuffString();
    }

    protected void writeKey(String key) {
        if (this.simple) {
            this.writerBuff.append(key);
        } else {
            this.writeObj(key);
        }

    }

    protected void writeObj(Boolean obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Character obj) {
        this.writeObj(obj.toString());
    }

    protected void writeObj(Byte obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Short obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Integer obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Long obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Float obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(Double obj) {
        this.writerBuff.append(obj.toString());
    }

    protected void writeObj(String obj) {
        char[] chArr = obj.toCharArray();
        this.writerBuff.append((char) (this.simple ? '\'' : '"'));
        char[] var3 = chArr;
        int var4 = chArr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            String uStr;
            switch (ch) {
                case '\b':
                    this.writerBuff.append("\\b");
                    continue;
                case '\t':
                    this.writerBuff.append("\\t");
                    continue;
                case '\n':
                    this.writerBuff.append("\\n");
                    continue;
                case '\f':
                    this.writerBuff.append("\\f");
                    continue;
                case '\r':
                    this.writerBuff.append("\\r");
                    continue;
                case '"':
                    this.writerBuff.append(this.simple ? "\"" : "\\\"");
                    continue;
                case '\'':
                    this.writerBuff.append(this.simple ? "\\'" : "'");
                    continue;
                case '/':
                    this.writerBuff.append("\\/");
                    continue;
                case '\\':
                    this.writerBuff.append("\\\\");
                    continue;
                default:
                    if (this.nonc) {
                        this.writerBuff.append(ch);
                        continue;
                    }

                    if (ch >= 0 && ch <= 127) {
                        this.writerBuff.append(ch);
                        continue;
                    }

                    uStr = Integer.toHexString(ch);
            }

            while (uStr.length() < 4) {
                uStr = '0' + uStr;
            }

            this.writerBuff.append("\\u").append(uStr);
        }

        this.writerBuff.append((char) (this.simple ? '\'' : '"'));
    }

    protected void writeObj(Object obj) throws JSONException {
        if (obj == null) {
            this.writeJSONNull();
        } else if (obj instanceof Boolean) {
            this.writeObj((Boolean) obj);
        } else if (obj instanceof Character) {
            this.writeObj((Character) obj);
        } else if (obj instanceof Byte) {
            this.writeObj((Byte) obj);
        } else if (obj instanceof Short) {
            this.writeObj((Short) obj);
        } else if (obj instanceof Integer) {
            this.writeObj((Integer) obj);
        } else if (obj instanceof Long) {
            this.writeObj((Long) obj);
        } else if (obj instanceof Float) {
            this.writeObj((Float) obj);
        } else if (obj instanceof Double) {
            this.writeObj((Double) obj);
        } else if (obj instanceof String) {
            this.writeObj((String) obj);
        } else if (obj instanceof JSONArray) {
            this.writeStr(((JSONArray) obj).toJsonString(this.simple, this.nonc));
        } else if (obj instanceof JSONObject) {
            this.writeStr(((JSONObject) obj).toJsonString(this.simple, this.nonc));
        } else if (obj instanceof ArrayList) {
            this.writeStr((new JSONArray((ArrayList) obj)).toJsonString(this.simple, this.nonc));
        } else if (obj instanceof HashMap) {
            this.writeStr((new JSONObject((HashMap) obj)).toJsonString(this.simple, this.nonc));
        } else {
            this.writeObj(obj.toString());
        }

    }

    protected void writeJSONObjectBegin() {
        this.writerBuff.append('{');
    }

    protected void writeJSONObjectEnd() {
        this.writerBuff.append('}');
    }

    protected void writeJSONArrayBegin() {
        this.writerBuff.append('[');
    }

    protected void writeJSONArrayEnd() {
        this.writerBuff.append(']');
    }

    protected void writeJSONComma() {
        this.writerBuff.append(',');
    }

    protected void writeJSONColon() {
        this.writerBuff.append(':');
    }

    protected void writeJSONNull() {
        this.writerBuff.append("null");
    }

    private void writeStr(String str) {
        this.writerBuff.append(str);
    }
}
