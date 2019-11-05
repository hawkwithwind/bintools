//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import com.wxipad.wechat.tools.json.JSONArray;
import com.wxipad.wechat.tools.json.JSONConvert;
import com.wxipad.wechat.tools.json.JSONException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ActionRecorder<T> {
    private static final Class[] BASIC;
    private static final Class[] BASIC2;

    static {
        BASIC = new Class[]{Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
        BASIC2 = new Class[]{Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
    }

    private final ArrayList<ActionObj> actions = new ArrayList();
    private final Class clazz;

    public ActionRecorder(Class clazz) {
        this.clazz = clazz;
    }

    public void clear() {
        this.actions.clear();
    }

    public int size() {
        return this.actions.size();
    }

    public boolean record(String func, Object... params) {
        Method method = this.findMethod(func, params);
        if (method == null) {
            return false;
        } else {
            String[] paramStrs = new String[params.length];

            for (int i = 0; i < params.length; ++i) {
                paramStrs[i] = this.convert2str(params[i]);
            }

            Class[] types = method.getParameterTypes();
            String[] typeStrs = new String[types.length];

            for (int i = 0; i < types.length; ++i) {
                typeStrs[i] = types[i].toString();
            }

            this.actions.add(new ActionObj(func, paramStrs, typeStrs));
            return true;
        }
    }

    public int play(T obj) {
        int success = 0;
        Iterator var3 = this.actions.iterator();

        while (true) {
            ActionObj action;
            Method method;
            do {
                if (!var3.hasNext()) {
                    return success;
                }

                action = (ActionObj) var3.next();
                method = this.findMethod(action.func, action.types);
            } while (method == null);

            try {
                Class[] types = method.getParameterTypes();
                Object[] params = new Object[action.params.length];
                if (types.length == params.length) {
                    for (int i = 0; i < types.length; ++i) {
                        params[i] = this.convert2obj(action.params[i], types[i]);
                    }

                    method.invoke(obj, params);
                    ++success;
                }
            } catch (IllegalAccessException var9) {
                Logger.getLogger(ActionRecorder.class.getName()).log(Level.SEVERE, (String) null, var9);
            } catch (IllegalArgumentException var10) {
                Logger.getLogger(ActionRecorder.class.getName()).log(Level.SEVERE, (String) null, var10);
            } catch (InvocationTargetException var11) {
                Logger.getLogger(ActionRecorder.class.getName()).log(Level.SEVERE, (String) null, var11);
            }
        }
    }

    public JSONArray output() {
        JSONArray actionArr = new JSONArray();
        Iterator var2 = this.actions.iterator();

        while (var2.hasNext()) {
            ActionObj action = (ActionObj) var2.next();
            JSONArray actionObj = new JSONArray();
            actionObj.put(action.func);
            actionObj.put(JSONConvert.toJSONArray(action.params));
            actionObj.put(JSONConvert.toJSONArray(action.types));
            actionArr.put(actionObj);
        }

        return actionArr;
    }

    public boolean input(JSONArray data) {
        boolean success = true;
        this.actions.clear();

        for (int i = 0; i < data.length(); ++i) {
            try {
                JSONArray actionObj = data.getJsonArray(i);
                String func = actionObj.getString(0);
                JSONArray paramArr = actionObj.getJsonArray(1);
                String[] paramStrs = new String[paramArr.length()];

                for (int j = 0; j < paramArr.length(); ++j) {
                    paramStrs[j] = paramArr.getString(j);
                }

                JSONArray typeArr = actionObj.getJsonArray(2);
                String[] typeStrs = new String[typeArr.length()];

                for (int j = 0; j < typeArr.length(); ++j) {
                    typeStrs[j] = typeArr.getString(j);
                }

                this.actions.add(new ActionObj(func, paramStrs, typeStrs));
            } catch (JSONException var11) {
                Logger.getLogger(ActionRecorder.class.getName()).log(Level.SEVERE, (String) null, var11);
                success = false;
            }
        }

        return success;
    }

    protected abstract String parseObj(Object var1);

    protected abstract Object parseStr(String var1, Class var2);

    private Method findMethod(String func, Object... params) {
        Method[] methods = this.clazz.getMethods();
        Method[] var4 = methods;
        int var5 = methods.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Method m = var4[var6];
            if (m.getName().equals(func)) {
                Class[] types = m.getParameterTypes();
                int len1 = types.length;
                int len2 = params.length;
                if (len1 == len2) {
                    if (len1 == 0) {
                        return m;
                    }

                    boolean matched = true;

                    for (int i = 0; i < len1; ++i) {
                        if (!this.matchParam(types[i], params[i])) {
                            matched = false;
                            break;
                        }
                    }

                    if (matched) {
                        return m;
                    }
                }
            }
        }

        return null;
    }

    private Method findMethod(String func, String... paramTypes) {
        Method[] methods = this.clazz.getMethods();
        Method[] var4 = methods;
        int var5 = methods.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Method m = var4[var6];
            if (m.getName().equals(func)) {
                Class[] types = m.getParameterTypes();
                int len1 = types.length;
                int len2 = paramTypes.length;
                if (len1 == len2) {
                    if (len1 == 0) {
                        return m;
                    }

                    boolean matched = true;

                    for (int i = 0; i < len1; ++i) {
                        if (!types[i].toString().equals(paramTypes[i])) {
                            matched = false;
                            break;
                        }
                    }

                    if (matched) {
                        return m;
                    }
                }
            }
        }

        return null;
    }

    private boolean matchParam(Class type, Object obj) {
        if (obj == null) {
            Class[] var7 = BASIC;
            int var4 = var7.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Class c = var7[var5];
                if (c.equals(type)) {
                    return false;
                }
            }

            return true;
        } else if (type.equals(obj.getClass())) {
            return true;
        } else {
            for (int i = 0; i < BASIC.length; ++i) {
                if (BASIC[i].equals(type) && BASIC2[i].equals(obj.getClass())) {
                    return true;
                }
            }

            return false;
        }
    }

    private String convert2str(Object obj) {
        if (obj == null) {
            return null;
        } else {
            Class c = obj.getClass();
            Class[] var3 = BASIC2;
            int length = var3.length;

            int var5;
            for (var5 = 0; var5 < length; ++var5) {
                Class basic = var3[var5];
                if (basic.equals(c)) {
                    return obj.toString();
                }
            }

            if (String.class.equals(c)) {
                return (String) obj;
            } else {
                if (c.isArray()) {
                    Class type = c.getComponentType();
                    if (type.equals(Character.TYPE)) {
                        return new String((char[]) ((char[]) obj));
                    }

                    Class[] var12 = BASIC;
                    var5 = var12.length;

                    int i;
                    for (i = 0; i < var5; ++i) {
                        Class basic = var12[i];
                        if (basic.equals(type) && !basic.equals(Character.TYPE)) {
                            length = Array.getLength(obj);
                            StringBuilder sb = new StringBuilder();

                            for (i = 0; i < length; ++i) {
                                if (i > 0) {
                                    sb.append(",");
                                }

                                sb.append(Array.get(obj, i).toString());
                            }

                            return sb.toString();
                        }
                    }

                    if (type.equals(String.class)) {
                        length = Array.getLength(obj);
                        JSONArray arr = new JSONArray();

                        for (i = 0; i < length; ++i) {
                            arr.put(Array.get(obj, i));
                        }

                        return arr.toString();
                    }
                }

                return this.parseObj(obj);
            }
        }
    }

    private Object convert2obj(String str, Class type) {
        if (str == null) {
            return null;
        } else if (type.equals(Boolean.TYPE)) {
            return this.convert2boolean(str);
        } else if (type.equals(Character.TYPE)) {
            return this.convert2char(str);
        } else if (type.equals(Byte.TYPE)) {
            return this.convert2byte(str);
        } else if (type.equals(Short.TYPE)) {
            return this.convert2short(str);
        } else if (type.equals(Integer.TYPE)) {
            return this.convert2int(str);
        } else if (type.equals(Long.TYPE)) {
            return this.convert2long(str);
        } else if (type.equals(Float.TYPE)) {
            return this.convert2float(str);
        } else if (type.equals(Double.TYPE)) {
            return this.convert2double(str);
        } else if (type.equals(String.class)) {
            return str;
        } else {
            int i;
            String[] strs;
            if (type.equals(boolean[].class)) {
                strs = str.split(",");
                boolean[] arr = new boolean[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2boolean(strs[i]);
                }

                return arr;
            } else if (type.equals(char[].class)) {
                char[] arr = new char[str.length()];

                for (i = 0; i < str.length(); ++i) {
                    arr[i] = str.charAt(i);
                }

                return arr;
            } else if (type.equals(byte[].class)) {
                strs = str.split(",");
                byte[] arr = new byte[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2byte(strs[i]);
                }

                return arr;
            } else if (type.equals(short[].class)) {
                strs = str.split(",");
                short[] arr = new short[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2short(strs[i]);
                }

                return arr;
            } else if (type.equals(int[].class)) {
                strs = str.split(",");
                int[] arr = new int[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2int(strs[i]);
                }

                return arr;
            } else if (type.equals(long[].class)) {
                strs = str.split(",");
                long[] arr = new long[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2long(strs[i]);
                }

                return arr;
            } else if (type.equals(float[].class)) {
                strs = str.split(",");
                float[] arr = new float[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2float(strs[i]);
                }

                return arr;
            } else if (type.equals(double[].class)) {
                strs = str.split(",");
                double[] arr = new double[strs.length];

                for (i = 0; i < strs.length; ++i) {
                    arr[i] = this.convert2double(strs[i]);
                }

                return arr;
            } else if (type.equals(String[].class)) {
                try {
                    JSONArray arr = new JSONArray(str, true);
                    strs = new String[arr.length()];

                    for (i = 0; i < arr.length(); ++i) {
                        strs[i] = arr.getString(i);
                    }

                    return strs;
                } catch (JSONException var6) {
                    Logger.getLogger(ActionRecorder.class.getName()).log(Level.SEVERE, (String) null, var6);
                    return null;
                }
            } else {
                return this.parseStr(str, type);
            }
        }
    }

    private boolean convert2boolean(String str) {
        try {
            return str == null ? false : Boolean.parseBoolean(str);
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    private char convert2char(String str) {
        return str != null && !str.isEmpty() ? str.charAt(0) : '\u0000';
    }

    private byte convert2byte(String str) {
        try {
            return str == null ? 0 : Byte.parseByte(str);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    private short convert2short(String str) {
        try {
            return str == null ? 0 : Short.parseShort(str);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    private int convert2int(String str) {
        try {
            return str == null ? 0 : Integer.parseInt(str);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    private long convert2long(String str) {
        try {
            return str == null ? 0L : Long.parseLong(str);
        } catch (NumberFormatException var3) {
            return 0L;
        }
    }

    private float convert2float(String str) {
        try {
            return str == null ? 0.0F : Float.parseFloat(str);
        } catch (NumberFormatException var3) {
            return 0.0F;
        }
    }

    private double convert2double(String str) {
        try {
            return str == null ? 0.0D : Double.parseDouble(str);
        } catch (NumberFormatException var3) {
            return 0.0D;
        }
    }

    public static class ActionObj {
        public final String func;
        public final String[] params;
        public final String[] types;

        public ActionObj(String func, String[] params, String[] types) {
            this.func = func;
            this.params = params == null ? new String[0] : params;
            this.types = types == null ? new String[0] : types;
        }
    }
}
