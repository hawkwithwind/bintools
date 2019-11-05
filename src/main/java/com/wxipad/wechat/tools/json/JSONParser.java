//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.json;

class JSONParser {
    protected static final byte BOOLEAN_TURE_NUM = 1;
    protected static final byte BOOLEAN_FALSE_NUM = 0;
    protected static final boolean NULL_BOOLEAN = false;
    protected static final char NULL_CHAR = '\u0000';
    protected static final byte NULL_NUMBER = 0;
    private static final String ERR_OBJECT_NOT_BOOLEAN = "object can not be parse to boolean";
    private static final String ERR_OBJECT_NOT_CHAR = "object can not be parse to char";
    private static final String ERR_OBJECT_NOT_NUMBER = "object can not be parse to number";
    private static final String ERR_OBJECT_NOT_JSON_OBJECT = "object can not be parse to json object";
    private static final String ERR_OBJECT_NOT_JSON_ARRAY = "object can not be parse to json array";

    JSONParser() {
    }

    public static boolean parseBoolean(Object obj) throws JSONException {
        if (obj == null) {
            return false;
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            try {
                return Boolean.parseBoolean((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue() != 0L;
        } else {
            throw new JSONException("object can not be parse to boolean");
        }
    }

    public static char parseChar(Object obj) throws JSONException {
        if (obj == null) {
            return '\u0000';
        } else if (obj instanceof Number) {
            return (char) ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                String str = (String) obj;
                return str.isEmpty() ? '\u0000' : str.charAt(0);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (char) ((Boolean) obj ? 1 : 0);
        } else {
            throw new JSONException("object can not be parse to char");
        }
    }

    public static byte parseByte(Object obj) throws JSONException {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number) obj).byteValue();
        } else if (obj instanceof String) {
            try {
                return Byte.parseByte((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (byte) ((Boolean) obj ? 1 : 0);
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static short parseShort(Object obj) throws JSONException {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number) obj).shortValue();
        } else if (obj instanceof String) {
            try {
                return Short.parseShort((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (short) ((Boolean) obj ? 1 : 0);
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static int parseInteger(Object obj) throws JSONException {
        if (obj == null) {
            return 0;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? 1 : 0;
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static long parseLong(Object obj) throws JSONException {
        if (obj == null) {
            return 0L;
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? 1L : 0L;
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static float parseFloat(Object obj) throws JSONException {
        if (obj == null) {
            return 0.0F;
        } else if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        } else if (obj instanceof String) {
            try {
                return Float.parseFloat((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? 1.0F : 0.0F;
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static double parseDouble(Object obj) throws JSONException {
        if (obj == null) {
            return 0.0D;
        } else if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (Exception var2) {
                throw new JSONException(var2.getMessage());
            }
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? 1.0D : 0.0D;
        } else {
            throw new JSONException("object can not be parse to number");
        }
    }

    public static String parseString(Object obj) throws JSONException {
        if (obj == null) {
            return null;
        } else {
            return obj instanceof String ? (String) obj : obj.toString();
        }
    }

    public static JSONObject parseJsonObject(Object obj) throws JSONException {
        if (obj == null) {
            return null;
        } else if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else {
            throw new JSONException("object can not be parse to json object");
        }
    }

    public static JSONArray parseJsonArray(Object obj) throws JSONException {
        if (obj == null) {
            return null;
        } else if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        } else {
            throw new JSONException("object can not be parse to json array");
        }
    }
}
