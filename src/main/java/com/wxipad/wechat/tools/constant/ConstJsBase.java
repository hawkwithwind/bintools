//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.constant;

import com.wxipad.wechat.tools.json.JSONArray;
import com.wxipad.wechat.tools.json.JSONObject;

public abstract class ConstJsBase {
    public ConstJsBase() {
    }

    public String createParamJS(Object... params) {
        return this.createParam(params);
    }

    public String createParam(Object[] params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }

            if (params[i] == null) {
                sb.append("null");
            } else if (params[i] instanceof String) {
                sb.append(this.formatJsStr((String) params[i]));
            } else if (params[i] instanceof Character) {
                sb.append(this.formatJsStr(((Character) params[i]).toString()));
            } else if (params[i] instanceof Byte) {
                sb.append((Byte) params[i]);
            } else if (params[i] instanceof Short) {
                sb.append(((Short) params[i]).byteValue());
            } else if (params[i] instanceof Integer) {
                sb.append(((Integer) params[i]).byteValue());
            } else if (params[i] instanceof Long) {
                sb.append(((Long) params[i]).byteValue());
            } else if (params[i] instanceof Float) {
                sb.append((Float) params[i]);
            } else if (params[i] instanceof Double) {
                sb.append((Double) params[i]);
            } else if (params[i] instanceof Number) {
                sb.append(((Number) params[i]).doubleValue());
            } else if (params[i] instanceof JsObject) {
                sb.append(params[i].toString());
            } else if (params[i] instanceof JsCode) {
                sb.append(((JsCode) params[i]).code);
            } else if (params[i] instanceof JSONObject) {
                sb.append(((JSONObject) params[i]).toString(true));
            } else if (params[i] instanceof JSONArray) {
                sb.append(((JSONArray) params[i]).toString(true));
            } else {
                sb.append(params[i].toString());
            }
        }

        return sb.toString();
    }

    protected String formatJsStr(String str) {
        if (str == null) {
            return "null";
        } else {
            char[] chArr = str.toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append('\'');
            char[] var4 = chArr;
            int var5 = chArr.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                char ch = var4[var6];
                if (ch >= 0 && ch <= 127) {
                    switch (ch) {
                        case '\b':
                            sb.append("\\b");
                            break;
                        case '\t':
                            sb.append("\\t");
                            break;
                        case '\n':
                            sb.append("\\n");
                            break;
                        case '\f':
                            sb.append("\\f");
                            break;
                        case '\r':
                            sb.append("\\r");
                            break;
                        case '\'':
                            sb.append("\\'");
                            break;
                        case '/':
                            sb.append("\\/");
                            break;
                        case '\\':
                            sb.append("\\\\");
                            break;
                        default:
                            sb.append(ch);
                    }
                } else {
                    String uStr;
                    for (uStr = Integer.toHexString(ch); uStr.length() < 4; uStr = '0' + uStr) {
                    }

                    sb.append("\\u").append(uStr);
                }
            }

            sb.append('\'');
            return sb.toString();
        }
    }

    public static class JsObject {
        private final String prefix;
        private final String name;

        public JsObject(String prefix, String name) {
            this.prefix = prefix;
            this.name = name;
        }

        public String toString() {
            return this.prefix == null ? this.name : this.prefix + "." + this.name;
        }
    }

    public static class JsCode {
        private final String code;

        public JsCode(String code) {
            this.code = code;
        }
    }
}
