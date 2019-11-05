//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolClass {
    private static final int BUFF_SIZE = 1024;

    public ToolClass() {
    }

    public static byte[] class2bytes(Class c) throws IOException {
        String name = c.getName().replace('.', '/') + ".class";
        InputStream is = c.getClassLoader().getResourceAsStream(name);

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];

            int read;
            while ((read = is.read(buff)) != -1) {
                os.write(buff, 0, read);
            }

            byte[] var6 = os.toByteArray();
            return var6;
        } finally {
            is.close();
        }
    }

    public static Class[] getTypes(Object... args) {
        Class[] types = new Class[args.length];

        for (int i = 0; i < args.length; ++i) {
            types[i] = args[i].getClass();
        }

        return types;
    }

    public static Object invoke(Class c, String name, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return c.getMethod(name, getTypes(args)).invoke((Object) null, args);
    }

    public static Object invoke(Object o, String name, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return o.getClass().getMethod(name, getTypes(args)).invoke(o, args);
    }

    public static boolean baseOfClass(Class c, Class baseclass) {
        for (Class superclass = c; superclass != null; superclass = superclass.getSuperclass()) {
            if (superclass.equals(baseclass)) {
                return true;
            }
        }

        return false;
    }

    public static int enumCount(Class enumClass) {
        return enumClass != null && enumClass.isEnum() ? enumClass.getEnumConstants().length : -1;
    }

    public static Object ordinal2Enum(Class enumClass, int ordinal) {
        if (enumClass != null && enumClass.isEnum()) {
            Object[] constants = enumClass.getEnumConstants();
            if (ordinal < constants.length) {
                return constants[ordinal];
            }
        }

        return null;
    }

    public static int enum2Ordinal(Class enumClass, Object enumObject) {
        if (enumClass != null && enumClass.isEnum()) {
            Object[] constants = enumClass.getEnumConstants();

            for (int i = 0; i < constants.length; ++i) {
                if (constants[i].equals(enumObject)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static Object name2Enum(Class enumClass, String name) {
        if (enumClass != null && enumClass.isEnum()) {
            Object[] var2 = enumClass.getEnumConstants();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Object e = var2[var4];
                if (e.toString().equals(name)) {
                    return e;
                }
            }
        }

        return null;
    }

    public static int name2Ordinal(Class enumClass, String name) {
        if (enumClass != null && enumClass.isEnum()) {
            Object[] constants = enumClass.getEnumConstants();

            for (int i = 0; i < constants.length; ++i) {
                if (constants[i].toString().equals(name)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static <T> T parseObj(Object obj, Class<T> parse) {
        return parseObj(obj, parse, false);
    }

    public static <T> T parseObj(Object obj, Class<T> parse, boolean accurate) {
        if (obj != null && parse != null) {
            if (accurate) {
                if (parse.equals(obj.getClass())) {
                    return (T) obj;
                }
            } else if (baseOfClass(obj.getClass(), parse)) {
                return (T) obj;
            }
        }

        return null;
    }

    public static <T> ArrayList<T> publicStatics(Class c, Class<T> parse) {
        return publicStatics(c, parse, (String) null, false);
    }

    public static <T> ArrayList<T> publicStatics(Class c, Class<T> parse, String prefix) {
        return publicStatics(c, parse, prefix, false);
    }

    public static <T> ArrayList<T> publicStatics(Class c, Class<T> parse, boolean accurate) {
        return publicStatics(c, parse, (String) null, accurate);
    }

    public static <T> ArrayList<T> publicStatics(Class c, Class<T> parse, String prefix, boolean accurate) {
        if (c == null) {
            return null;
        } else {
            ArrayList<T> list = new ArrayList();
            Field[] var5 = c.getFields();
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Field f = var5[var7];
                String name = f.getName();
                if ((prefix == null || name.startsWith(prefix)) && (!accurate || f.getDeclaringClass().equals(c))) {
                    int modifiers = f.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                        try {
                            T obj = parseObj(f.get((Object) null), parse);
                            if (obj != null) {
                                list.add(obj);
                            }
                        } catch (Exception var12) {
                            Logger.getLogger(ToolClass.class.getName()).log(Level.SEVERE, (String) null, var12);
                        }
                    }
                }
            }

            return list;
        }
    }

    public static HashMap<String, Object> publicObjects(Object o, boolean isStatic) {
        return publicObjects(o, isStatic, (String) null);
    }

    public static HashMap<String, Object> publicObjects(Object o, boolean isStatic, String prefix) {
        if (o == null) {
            return null;
        } else {
            HashMap<String, Object> map = new HashMap();
            Class c = o.getClass();
            Field[] var5 = c.getFields();
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                Field f = var5[var7];
                String name = f.getName();
                if (prefix == null || name.startsWith(prefix)) {
                    int modifiers = f.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) == isStatic) {
                        try {
                            map.put(name, f.get(o));
                        } catch (Exception var12) {
                            Logger.getLogger(ToolClass.class.getName()).log(Level.SEVERE, (String) null, var12);
                        }
                    }
                }
            }

            return map;
        }
    }

    public static String[] publicStrings(Class c) {
        return publicStrings(c, (String) null, false);
    }

    public static String[] publicStrings(Class c, String prefix) {
        return publicStrings(c, prefix, false);
    }

    public static String[] publicStrings(Class c, String prefix, boolean accurate) {
        ArrayList<String> strs = publicStatics(c, String.class, prefix, accurate);
        return ToolArr.toArray(strs);
    }

    public static Field getField(Class c, String name) {
        Field f = null;

        try {
            f = c.getField(name);
        } catch (Exception var4) {
        }

        return f;
    }

    public static void copyObject(Object src, Object dest) {
        if (src != null && dest != null) {
            Class srcCls = src.getClass();
            Class destCls = dest.getClass();
            Field[] var4 = srcCls.getFields();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Field srcFld = var4[var6];
                int srcModifiers = srcFld.getModifiers();
                if (!Modifier.isStatic(srcModifiers) && Modifier.isPublic(srcModifiers)) {
                    Field destFld = getField(destCls, srcFld.getName());
                    if (destFld != null) {
                        int destModifiers = srcFld.getModifiers();
                        if (!Modifier.isStatic(destModifiers) && Modifier.isPublic(destModifiers)) {
                            Class type = srcFld.getType();
                            if (type.equals(destFld.getType())) {
                                try {
                                    if (Boolean.TYPE.equals(type)) {
                                        srcFld.setBoolean(dest, srcFld.getBoolean(src));
                                    } else if (Byte.TYPE.equals(type)) {
                                        srcFld.setByte(dest, srcFld.getByte(src));
                                    } else if (Character.TYPE.equals(type)) {
                                        srcFld.setChar(dest, srcFld.getChar(src));
                                    } else if (Short.TYPE.equals(type)) {
                                        srcFld.setShort(dest, srcFld.getShort(src));
                                    } else if (Integer.TYPE.equals(type)) {
                                        srcFld.setInt(dest, srcFld.getInt(src));
                                    } else if (Long.TYPE.equals(type)) {
                                        srcFld.setLong(dest, srcFld.getLong(src));
                                    } else if (Float.TYPE.equals(type)) {
                                        srcFld.setFloat(dest, srcFld.getFloat(src));
                                    } else if (Double.TYPE.equals(type)) {
                                        srcFld.setDouble(dest, srcFld.getDouble(src));
                                    } else {
                                        srcFld.set(dest, srcFld.get(src));
                                    }
                                } catch (IllegalArgumentException var13) {
                                    Logger.getLogger(ToolClass.class.getName()).log(Level.SEVERE, (String) null, var13);
                                } catch (IllegalAccessException var14) {
                                    Logger.getLogger(ToolClass.class.getName()).log(Level.SEVERE, (String) null, var14);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public static <T> T parseObject(Object o, Class<T> c) {
        return o != null && baseOfClass(o.getClass(), c) ? (T) o : null;
    }

    public static boolean hasInterface(Class c, Class i) {
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

    public static <T> T newInstance(Class<T> c) {
        Object obj = null;

        try {
            obj = c.newInstance();
        } catch (Exception var3) {
        }

        return (T) obj;
    }

    public static class ByteArrayClassLoader extends ClassLoader {
        private static final int BUFF_SIZE = 1024;
        private static final int INT_SIZE = 4;
        private static final Charset CHARSET = Charset.forName("UTF-8");
        public final HashMap<String, byte[]> dataMap = new HashMap();

        public ByteArrayClassLoader() {
        }

        public ByteArrayClassLoader(ClassLoader parent) {
            super(parent);
        }

        private static String getClassKey(String name) {
            return name != null ? name.replace('.', '/') + ".class" : null;
        }

        private static int bytesInt(byte[] data, int offset) {
            if (offset + 4 > data.length) {
                return -1;
            } else {
                int val = 0;

                for (int i = 0; i < 4; ++i) {
                    val |= (data[offset + i] & 255) << i * 8;
                }

                return val;
            }
        }

        private static byte[] intBytes(int val) {
            byte[] data = new byte[4];

            for (int i = 0; i < data.length; ++i) {
                data[i] = (byte) (val >> i * 8 & 255);
            }

            return data;
        }

        private static byte[] cutBytes(byte[] data, int offset, int length) {
            if (offset == 0 && data.length == length) {
                return data;
            } else if (offset + length > data.length) {
                return null;
            } else {
                byte[] cutBytes = new byte[length];
                System.arraycopy(data, offset, cutBytes, 0, length);
                return cutBytes;
            }
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            String key = getClassKey(name);
            synchronized (this.dataMap) {
                if (this.dataMap.containsKey(key)) {
                    byte[] bytes = (byte[]) this.dataMap.get(key);
                    return this.defineClass(name, bytes, 0, bytes.length);
                } else {
                    return null;
                }
            }
        }

        public InputStream getResourceAsStream(String name) {
            String key = name;
            synchronized (this.dataMap) {
                if (this.dataMap.containsKey(key)) {
                    byte[] bytes = (byte[]) this.dataMap.get(key);
                    return new ByteArrayInputStream(bytes);
                } else {
                    return null;
                }
            }
        }

        public boolean loadJar(byte[] data) {
            if (data != null) {
                try {
                    JarInputStream in = new JarInputStream(new ByteArrayInputStream(data));

                    try {
                        JarEntry entry = in.getNextJarEntry();

                        for (byte[] buff = new byte[1024]; entry != null; entry = in.getNextJarEntry()) {
                            String name = entry.getName();
                            ByteArrayOutputStream out = new ByteArrayOutputStream();

                            int count;
                            while ((count = in.read(buff)) != -1) {
                                out.write(buff, 0, count);
                            }

                            this.dataMap.put(name, out.toByteArray());
                        }

                        boolean var13 = true;
                        return var13;
                    } finally {
                        in.close();
                    }
                } catch (IOException var12) {
                }
            }

            return false;
        }

        public byte[] packDat() {
            synchronized (this.dataMap) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    try {
                        ArrayList<String> keys = new ArrayList();
                        keys.addAll(this.dataMap.keySet());
                        Collections.sort(keys);
                        Iterator var4 = keys.iterator();

                        while (var4.hasNext()) {
                            String key = (String) var4.next();
                            byte[] data;
                            if (key != null) {
                                data = key.getBytes(CHARSET);
                                out.write(intBytes(data.length));
                                out.write(data);
                            } else {
                                out.write(intBytes(0));
                            }

                            data = (byte[]) this.dataMap.get(key);
                            if (data != null) {
                                out.write(intBytes(data.length));
                                out.write(data);
                            } else {
                                out.write(intBytes(0));
                            }
                        }

                        byte[] var15 = out.toByteArray();
                        return var15;
                    } finally {
                        out.close();
                    }
                } catch (IOException var13) {
                    return null;
                }
            }
        }

        public boolean loadDat(byte[] pack) {
            int pos = 0;
            byte[] temp = null;

            while (pos < pack.length) {
                int length = bytesInt(pack, pos);
                if (length < 0) {
                    return false;
                }

                pos += 4;
                byte[] buff = cutBytes(pack, pos, length);
                if (buff == null) {
                    return false;
                }

                pos += length;
                if (temp == null) {
                    temp = buff;
                } else {
                    String key = new String(temp, CHARSET);
                    this.dataMap.put(key, buff);
                    temp = null;
                }
            }

            return temp == null;
        }

        public byte[] removeClass(String name) {
            return this.removeData(getClassKey(name));
        }

        public byte[] addClass(String name, byte[] data) {
            return this.addData(getClassKey(name), data);
        }

        public byte[] removeData(String key) {
            return this.addData(key, (byte[]) null);
        }

        public byte[] addData(String key, byte[] data) {
            synchronized (this.dataMap) {
                if (key == null) {
                    return null;
                } else {
                    return data != null ? (byte[]) this.dataMap.put(key, data) : (byte[]) this.dataMap.remove(key);
                }
            }
        }

        public int clearData() {
            synchronized (this.dataMap) {
                int count = this.dataMap.size();
                this.dataMap.clear();
                return count;
            }
        }
    }
}
