package com.wxipad.wechat.tools.tool;

import java.util.ArrayList;
import java.util.Arrays;

public class ToolArr {
    public static boolean contain(Object[] arr, Object obj) {
        if ((arr == null) || (obj == null)) {
            return false;
        }
        for (Object o : arr) {
            if ((o != null) && (o.equals(obj))) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(Object[] arr, Object obj) {
        if ((arr == null) || (obj == null)) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            Object o = arr[i];
            if ((o != null) && (o.equals(obj))) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<String> getArrayList(String... str) {
        return getArrayList(String.class, str);
    }

    public static <T> ArrayList<T> getArrayList(Class<T> c, T... obj) {
        ArrayList<T> arr = new ArrayList();
        arr.addAll(Arrays.asList(obj));
        return arr;
    }

    public static String[] getArray(String... str) {
        return str;
    }

    public static String[] toArray(ArrayList<String> list) {
        if ((list == null) || (list.isEmpty())) {
            return new String[0];
        }
        String[] strs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strs[i] = ((String) list.get(i));
        }
        return strs;
    }
}
