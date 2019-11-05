package com.wxipad.wechat.tools.extend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Chinese {
    private static final String SIMPLE;
    private static final String TRADITIONAL;

    static {
        String simple = null;
        String traditional = null;
        try {
            InputStream in = Chinese.class.getResourceAsStream("Chinese.txt");
            if (in != null) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    simple = reader.readLine();
                    traditional = reader.readLine();
                } finally {
                    in.close();
                }
            }
        } catch (Exception localException) {
        } finally {
            SIMPLE = simple == null ? "" : simple;
            TRADITIONAL = traditional == null ? "" : traditional;
        }
    }

    public static String s2t(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(s2t(str.charAt(i)));
        }
        return sb.toString();
    }

    public static String t2s(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(t2s(str.charAt(i)));
        }
        return sb.toString();
    }

    public static char s2t(char ch) {
        int index = SIMPLE.indexOf(ch);
        if (index != -1) {
            return TRADITIONAL.charAt(index);
        }
        return ch;
    }

    public static char t2s(char ch) {
        int index = TRADITIONAL.indexOf(ch);
        if (index != -1) {
            return SIMPLE.charAt(index);
        }
        return ch;
    }
}
