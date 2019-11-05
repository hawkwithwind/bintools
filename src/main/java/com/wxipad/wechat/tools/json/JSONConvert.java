//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONConvert {
    private static final String EXPCETION_CONVERT_PREFIX = "convert exception:";

    public JSONConvert() {
    }

    public static JSONObject toJSONObject(Object obj) {
        return toJSONObject(obj, false);
    }

    public static JSONObject toJSONObject(Object obj, boolean hasStatic) {
        try {
            return (JSONObject) toJSON(obj, JSONObject.class, hasStatic);
        } catch (JSONException var3) {
            Logger.getLogger(JSONConvert.class.getName()).log(Level.SEVERE, (String) null, var3);
            return null;
        }
    }

    public static JSONArray toJSONArray(Object obj) {
        return toJSONArray(obj, false);
    }

    public static JSONArray toJSONArray(Object obj, boolean hasStatic) {
        try {
            return (JSONArray) toJSON(obj, JSONArray.class, hasStatic);
        } catch (JSONException var3) {
            Logger.getLogger(JSONConvert.class.getName()).log(Level.SEVERE, (String) null, var3);
            return null;
        }
    }

    public static <T> T toJSON(Object obj, Class<T> c) throws JSONException {
        return parseObject(toJSON(obj), c);
    }

    public static <T> T toJSON(Object obj, Class<T> c, boolean hasStatic) throws JSONException {
        return parseObject(toJSON(obj, hasStatic), c);
    }

    public static Object toJSON(Object obj) throws JSONException {
        return toJSON(obj, false);
    }

    public static Object toJSON(Object obj, boolean hasStatic) throws JSONException {
        if (obj == null) {
            return null;
        } else if (isBasic(obj)) {
            return obj;
        } else if (!(obj instanceof JSONArray) && !(obj instanceof JSONObject)) {
            Class objClass = obj.getClass();
            int i;
            Object key;
            if (objClass.isArray()) {
                JSONArray rtn = new JSONArray();
                int length = Array.getLength(obj);

                for (i = 0; i < length; ++i) {
                    key = Array.get(obj, i);
                    rtn.put(toJSON(key, hasStatic));
                }

                return rtn;
            } else if (hasInterface(objClass, List.class)) {
                List list = (List) obj;
                JSONArray jsonArr = new JSONArray();

                for (i = 0; i < list.size(); ++i) {
                    jsonArr.put(toJSON(list.get(i), hasStatic));
                }

                return jsonArr;
            } else if (hasInterface(objClass, Map.class)) {
                Map map = (Map) obj;
                JSONObject jsonObj = new JSONObject();
                Iterator var17 = map.keySet().iterator();

                while (var17.hasNext()) {
                    key = var17.next();
                    jsonObj.put(key.toString(), toJSON(map.get(key), hasStatic));
                }

                return jsonObj;
            } else {
                JSONObject rtn = new JSONObject();
                Field[] fields = objClass.getFields();

                for (i = 0; i < fields.length; ++i) {
                    try {
                        Field f = fields[i];
                        if (!f.isSynthetic() && (!Modifier.isStatic(f.getModifiers()) || hasStatic)) {
                            String name = f.getName();
                            Object value = f.get(obj);
                            rtn.put(name, toJSON(value, hasStatic));
                        }
                    } catch (IllegalArgumentException var9) {
                        throw new JSONException("convert exception:" + var9.getMessage());
                    } catch (IllegalAccessException var10) {
                        throw new JSONException("convert exception:" + var10.getMessage());
                    }
                }

                return rtn;
            }
        } else {
            return obj;
        }
    }

    public static void fromJSON(Object obj, JSONObject json) throws JSONException {
        if (obj != null && json != null) {
            Class cls = obj.getClass();
            Field[] fields = cls.getFields();
            Field[] var4 = fields;
            int var5 = fields.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Field f = var4[var6];
                String name = f.getName();
                Class type = f.getType();
                int modifiers = f.getModifiers();
                if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isInterface(modifiers)) {
                    try {
                        if (Boolean.TYPE.equals(type)) {
                            f.setBoolean(obj, json.getBoolean(name));
                        } else if (Byte.TYPE.equals(type)) {
                            f.setByte(obj, json.getByte(name));
                        } else if (Short.TYPE.equals(type)) {
                            f.setShort(obj, json.getShort(name));
                        } else if (Integer.TYPE.equals(type)) {
                            f.setInt(obj, json.getInteger(name));
                        } else if (Long.TYPE.equals(type)) {
                            f.setLong(obj, json.getLong(name));
                        } else if (Float.TYPE.equals(type)) {
                            f.setFloat(obj, json.getFloat(name));
                        } else if (Double.TYPE.equals(type)) {
                            f.setDouble(obj, json.getDouble(name));
                        } else if (String.class.equals(type)) {
                            f.set(obj, json.getString(name));
                        } else if (type.isArray()) {
                            Class elmCls = type.getComponentType();
                            Object arrObj = null;
                            JSONArray arrJson = json.getJsonArray(name);
                            if (arrJson != null) {
                                arrObj = Array.newInstance(elmCls, arrJson.length());
                                fromJSON(arrObj, arrJson);
                            }

                            f.set(obj, arrObj);
                        } else {
                            fromJSON(f.get(obj), json.getJsonObject(name));
                        }
                    } catch (Exception var14) {
                    }
                }
            }
        }

    }

    public static void fromJSON(Object obj, JSONArray json) throws JSONException {
        if (obj != null && json != null) {
            Class cls = obj.getClass();
            if (cls.isArray()) {
                Class elmCls = cls.getComponentType();

                for (int i = 0; i < json.length(); ++i) {
                    try {
                        if (Boolean.TYPE.equals(elmCls)) {
                            Array.setBoolean(obj, i, json.getBoolean(i));
                        } else if (Byte.TYPE.equals(elmCls)) {
                            Array.setByte(obj, i, json.getByte(i));
                        } else if (Short.TYPE.equals(elmCls)) {
                            Array.setShort(obj, i, json.getShort(i));
                        } else if (Integer.TYPE.equals(elmCls)) {
                            Array.setInt(obj, i, json.getInteger(i));
                        } else if (Long.TYPE.equals(elmCls)) {
                            Array.setLong(obj, i, json.getLong(i));
                        } else if (Float.TYPE.equals(elmCls)) {
                            Array.setFloat(obj, i, json.getFloat(i));
                        } else if (Double.TYPE.equals(elmCls)) {
                            Array.setDouble(obj, i, json.getDouble(i));
                        } else if (String.class.equals(elmCls)) {
                            Array.set(obj, i, json.getString(i));
                        } else {
                            Object elmObj = elmCls.newInstance();
                            fromJSON(elmObj, json.getJsonObject(i));
                            Array.set(obj, i, elmObj);
                        }
                    } catch (Exception var6) {
                    }
                }
            }
        }

    }

    private static boolean isBasic(Object obj) {
        return obj instanceof Boolean || obj instanceof Short || obj instanceof Integer || obj instanceof Long || obj instanceof Float || obj instanceof Double || obj instanceof String;
    }

    private static <T> T parseObject(Object o, Class<T> c) {
        return o != null && baseOfClass(o.getClass(), c) ? (T) o : null;
    }


    protected static boolean baseOfClass(Class c, Class baseclass) {
        for (Class superclass = c; superclass != null; superclass = superclass.getSuperclass()) {
            if (superclass.equals(baseclass)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean hasInterface(Class c, Class i) {
        Class[] var2 = c.getInterfaces();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Class ci = var2[var4];
            if (i.equals(ci)) {
                return true;
            }
        }

        return false;
    }
}
