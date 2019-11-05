//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import com.wxipad.wechat.tools.constant.ConstFramework;
import com.wxipad.wechat.tools.crypto.Digest;
import com.wxipad.wechat.tools.crypto.Digest.ALGORITHM;
import com.wxipad.wechat.tools.extend.IntConvert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class ToolStr {
    public static final String REGEX_LINE = "\n|\r\n|\r";
    public static final String REGEX_SPACE = "\\s+";
    public static final String SPACE_CHARS = " \b\t\r\n";
    public static final String STRING_LETTER = "abcdefghijklmnopqrstuvwxyz";
    public static final String STRING_NUMBER = "0123456789";
    public static final String STRING_HEX = "0123456789abcdef";
    private static final int CHINESE_START = 19968;
    private static final int CHINESE_END = 40891;
    private static final char[] EnLowerSBC = new char[]{'ａ', 'ｂ', 'ｃ', 'ｄ', 'ｅ', 'ｆ', 'ｇ', 'ｈ', 'ｉ', 'ｊ', 'ｋ', 'ｌ', 'ｍ', 'ｎ', 'ｏ', 'ｐ', 'ｑ', 'ｒ', 'ｓ', 'ｔ', 'ｕ', 'ｖ', 'ｗ', 'ｘ', 'ｙ', 'ｚ'};
    private static final char[] EnUpperSBC = new char[]{'Ａ', 'Ｂ', 'Ｃ', 'Ｄ', 'Ｅ', 'Ｆ', 'Ｇ', 'Ｈ', 'Ｉ', 'Ｊ', 'Ｋ', 'Ｌ', 'Ｍ', 'Ｎ', 'Ｏ', 'Ｐ', 'Ｑ', 'Ｒ', 'Ｓ', 'Ｔ', 'Ｕ', 'Ｖ', 'Ｗ', 'Ｘ', 'Ｙ', 'Ｚ'};
    private static final char[] NumSBC = new char[]{'０', '１', '２', '３', '４', '５', '６', '７', '８', '９'};
    private static final char[] SymSBC = new char[]{'＋', '－', '＊', '／', '（', '）', '．'};
    private static final char[] SymDBC = new char[]{'+', '-', '*', '/', '(', ')', '.'};

    public ToolStr() {
    }

    public static String bytes2Hex(byte[] data) {
        return bytes2Hex(data, (String) null);
    }

    public static String bytes2Hex(byte[] data, String prefix) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; ++i) {
            if (prefix != null) {
                sb.append(prefix);
            }

            sb.append("0123456789abcdef".charAt((data[i] & 240) >> 4));
            sb.append("0123456789abcdef".charAt(data[i] & 15));
        }

        return sb.toString();
    }

    public static byte[] hex2Bytes(String hex) {
        if (hex == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            char[] var2 = hex.toCharArray();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                char ch = var2[var4];
                if (ch >= 'A' && ch <= 'F') {
                    sb.append((char) (ch + 32));
                } else if (ch >= 'a' && ch <= 'f') {
                    sb.append(ch);
                } else if (ch >= '0' && ch <= '9') {
                    sb.append(ch);
                }
            }

            String src = sb.toString();
            byte[] bytes = new byte[src.length() / 2];

            for (int i = 0; i < bytes.length; ++i) {
                byte high = (byte) ("0123456789abcdef".indexOf(src.charAt(i * 2)) << 4);
                byte low = (byte) "0123456789abcdef".indexOf(src.charAt(i * 2 + 1));
                bytes[i] = (byte) (high | low);
            }

            return bytes;
        }
    }

    public static String MD5(String str) {
        return MD5(str, ConstFramework.getCharset());
    }

    public static String MD5(String str, Charset charset) {
        return bytes2Hex(Digest.encode(str.getBytes(charset), ALGORITHM.MD5));
    }

    public static String SHA(String str) {
        return SHA(str, ConstFramework.getCharset());
    }

    public static String SHA(String str, Charset charset) {
        return bytes2Hex(Digest.encode(str.getBytes(charset), ALGORITHM.SHA));
    }

    public static String randomMD5() {
        return MD5(UUID.randomUUID().toString());
    }

    public static String randomSHA() {
        return SHA(UUID.randomUUID().toString());
    }

    public static String timeUUID() {
        return (new UUID(System.currentTimeMillis(), System.nanoTime() + (long) ((int) (Math.random() * 1000.0D)))).toString();
    }

    public static String timeUUID(long timestamp) {
        return timeUUID(timestamp, false);
    }

    public static String timeUUID(long timestamp, boolean random) {
        long randomNum = System.nanoTime() + (long) ((int) (Math.random() * 1000.0D));
        return (new UUID(timestamp, random ? randomNum : 0L)).toString();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String toHtml(String str) {
        return toHtml(str, true, true);
    }

    public static String toHtml(String str, boolean replaceBr) {
        return toHtml(str, replaceBr, true);
    }

    public static String toHtml(String str, boolean replaceBr, boolean replaceSpace) {
        if (str == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < str.length(); ++i) {
                char c = str.charAt(i);
                sb.append(getHtmlCh(c, replaceBr, replaceSpace));
            }

            return sb.toString();
        }
    }

    private static String getHtmlCh(char ch, boolean replaceBr, boolean replaceSpace) {
        if (ch == '&') {
            return "&amp;";
        } else if (ch == '"') {
            return "&quot;";
        } else if (ch == '\'') {
            return "&apos;";
        } else if (ch == '>') {
            return "&gt;";
        } else if (ch == '<') {
            return "&lt;";
        } else if (ch == ' ') {
            return replaceSpace ? "&nbsp;" : Character.toString(ch);
        } else if (ch == '\n') {
            return replaceBr ? "<br/>" : Character.toString(ch);
        } else {
            return Character.toString(ch);
        }
    }

    public static String highlightHtml(String str, String hlStr, String className) {
        return highlightHtml(str, hlStr, className, "span", true);
    }

    public static String highlightHtml(String str, String hlStr, String className, String tagName) {
        return highlightHtml(str, hlStr, className, tagName, true);
    }

    public static String highlightHtml(String str, String hlStr, String className, String tagName, boolean ignoreCase) {
        if (str == null) {
            return "";
        } else if (hlStr != null && !hlStr.equals("")) {
            char[] src = str.toCharArray();
            char[] hl = hlStr.toCharArray();
            int index = 0;
            int inTag = 0;
            StringBuilder sb = new StringBuilder();

            while (true) {
                while (index < src.length) {
                    if (src[index] == '<') {
                        ++inTag;
                    }

                    if (chEqual(src[index], hl[0], ignoreCase)) {
                        int i = 1;

                        boolean match;
                        for (match = true; index + i < src.length && i < hl.length && (match = chEqual(src[index + i], hl[i], ignoreCase)); ++i) {
                        }

                        if (i == hl.length && match && inTag == 0) {
                            sb.append("<").append(tagName).append(" class=\"").append(className).append("\">");

                            for (i = 0; i < hl.length; ++i) {
                                sb.append(src[index + i]);
                            }

                            sb.append("</").append(tagName).append(">");
                            index += hl.length;
                            continue;
                        }
                    }

                    sb.append(src[index]);
                    if (src[index] == '>') {
                        --inTag;
                    }

                    ++index;
                }

                return sb.toString();
            }
        } else {
            return str;
        }
    }

    public static String highlightText(String str, String hlStr, String className) {
        return highlightText(str, hlStr, className, "span", true);
    }

    public static String highlightText(String str, String hlStr, String className, String tagName) {
        return highlightText(str, hlStr, className, tagName, true);
    }

    public static String highlightText(String str, String hlStr, String className, String tagName, boolean ignoreCase) {
        if (str == null) {
            return "";
        } else if (hlStr != null && !hlStr.equals("")) {
            char[] src = str.toCharArray();
            char[] hl = hlStr.toCharArray();
            int index = 0;
            boolean lastIsSpace = false;
            StringBuilder sb = new StringBuilder();

            while (true) {
                while (index < src.length) {
                    if (chEqual(src[index], hl[0], ignoreCase)) {
                        StringBuilder matchStrSb = new StringBuilder();
                        matchStrSb.append(src[index]);
                        int i = 1;

                        boolean match;
                        for (match = true; index + i < src.length && i < hl.length && (match = chEqual(src[index + i], hl[i], ignoreCase)); ++i) {
                            matchStrSb.append(src[index + i]);
                        }

                        if (i == hl.length && match) {
                            sb.append("<").append(tagName).append(" class=\"").append(className).append("\">").append(toHtml(matchStrSb.toString())).append("</").append(tagName).append(">");
                            index += hl.length;
                            continue;
                        }
                    }

                    char c = src[index];
                    sb.append(getHtmlCh(c, true, lastIsSpace));
                    lastIsSpace = c == ' ';
                    ++index;
                }

                return sb.toString();
            }
        } else {
            return toHtml(str);
        }
    }

    public static boolean chEqual(char ch1, char ch2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(ch1) == Character.toLowerCase(ch2);
        } else {
            return ch1 == ch2;
        }
    }

    public static boolean isInteger(String numStr) {
        for (int i = 0; i < numStr.length(); ++i) {
            char ch = numStr.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }

        return true;
    }

    public static boolean isFloat(String numStr) {
        boolean dot = false;

        for (int i = 0; i < numStr.length(); ++i) {
            char ch = numStr.charAt(i);
            if (ch < '0' || ch > '9') {
                if (ch != '.' || dot) {
                    return false;
                }

                dot = true;
            }
        }

        return true;
    }

    public static boolean isHex(String hexStr) {
        if (hexStr == null) {
            return false;
        } else {
            for (int i = 0; i < hexStr.length(); ++i) {
                char ch = hexStr.charAt(i);
                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f') && (ch < 'A' || ch > 'F')) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isNumAndLetter(String nlStr) {
        return isNumAndLetter(nlStr, (char[]) null);
    }

    public static boolean isNumAndLetter(String nlStr, char[] more) {
        if (nlStr == null) {
            return false;
        } else {
            for (int i = 0; i < nlStr.length(); ++i) {
                char ch = nlStr.charAt(i);
                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
                    if (more == null) {
                        return false;
                    }

                    boolean allowed = false;

                    for (int j = 0; j < more.length; ++j) {
                        if (ch == more[j]) {
                            allowed = true;
                            break;
                        }
                    }

                    if (!allowed) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static boolean isNum(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isLetter(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    public static boolean isChinese(char ch) {
        return ch >= 19968 && ch <= '龻';
    }

    public static int compareHex(String hex1, String hex2) {
        hex1 = fixHex(hex1);
        hex2 = fixHex(hex2);
        if (hex1.length() != hex2.length()) {
            return hex1.length() > hex2.length() ? 1 : -1;
        } else {
            for (int i = 0; i < hex1.length(); ++i) {
                if (hex1.charAt(i) != hex2.charAt(i)) {
                    return hex1.charAt(i) > hex2.charAt(i) ? 1 : -1;
                }
            }

            return 0;
        }
    }

    private static String fixHex(String hex) {
        char[] chArr = (hex == null ? "" : hex).toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chArr.length; ++i) {
            char ch = chArr[i];
            if (ch >= '0' && ch <= '9') {
                sb.append(ch);
            } else if (ch >= 'a' && ch <= 'z') {
                sb.append(ch);
            } else if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch - 65 + 97);
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    public static boolean equals(String strA, String strB) {
        return equals(strA, strB, true);
    }

    public static boolean equals(String strA, String strB, boolean sensitive) {
        if (strA == null && strB == null) {
            return true;
        } else if (strA != null && strB != null) {
            return sensitive ? strA.equals(strB) : strA.toLowerCase().equals(strB.toLowerCase());
        } else {
            return false;
        }
    }

    public static Boolean parseBoolean(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Boolean num;
            try {
                num = Boolean.parseBoolean(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static boolean parseBoolean(String numStr, boolean defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            boolean num = defaultVal;

            try {
                num = Boolean.parseBoolean(numStr);
            } catch (Exception var4) {
            }

            return num;
        }
    }

    public static Short parseShort(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Short num;
            try {
                num = Short.parseShort(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static short parseShort(String numStr, short defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            short num = defaultVal;

            try {
                num = Short.parseShort(numStr);
            } catch (Exception var4) {
            }

            return num;
        }
    }

    public static Integer parseInt(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Integer num;
            try {
                num = Integer.parseInt(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static int parseInt(String numStr, int defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            int num = defaultVal;

            try {
                num = Integer.parseInt(numStr);
            } catch (Exception var4) {
            }

            return num;
        }
    }

    public static Long parseLong(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Long num;
            try {
                num = Long.parseLong(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static long parseLong(String numStr, long defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            long num = defaultVal;

            try {
                num = Long.parseLong(numStr);
            } catch (Exception var6) {
            }

            return num;
        }
    }

    public static Float parseFloat(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Float num;
            try {
                num = Float.parseFloat(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static float parseFloat(String numStr, float defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            float num = defaultVal;

            try {
                num = Float.parseFloat(numStr);
            } catch (Exception var4) {
            }

            return num;
        }
    }

    public static Double parseDouble(String numStr) {
        if (numStr == null) {
            return null;
        } else {
            Double num;
            try {
                num = Double.parseDouble(numStr);
            } catch (Exception var3) {
                num = null;
            }

            return num;
        }
    }

    public static double parseDouble(String numStr, double defaultVal) {
        if (numStr == null) {
            return defaultVal;
        } else {
            double num = defaultVal;

            try {
                num = Double.parseDouble(numStr);
            } catch (Exception var6) {
            }

            return num;
        }
    }

    public static boolean[] parseBoolean(String arrStr, String splitReg, boolean defaultVal) {
        return parseBoolean(arrStr, splitReg, defaultVal, false);
    }

    public static boolean[] parseBoolean(String arrStr, String splitReg, boolean defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new boolean[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            boolean[] arr = new boolean[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseBoolean(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static short[] parseShort(String arrStr, String splitReg, short defaultVal) {
        return parseShort(arrStr, splitReg, defaultVal, false);
    }

    public static short[] parseShort(String arrStr, String splitReg, short defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new short[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            short[] arr = new short[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseShort(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static int[] parseInt(String arrStr, String splitReg, int defaultVal) {
        return parseInt(arrStr, splitReg, defaultVal, false);
    }

    public static int[] parseInt(String arrStr, String splitReg, int defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new int[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            int[] arr = new int[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseInt(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static long[] parseLong(String arrStr, String splitReg, long defaultVal) {
        return parseLong(arrStr, splitReg, defaultVal, false);
    }

    public static long[] parseLong(String arrStr, String splitReg, long defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new long[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            long[] arr = new long[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseLong(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static float[] parseFloat(String arrStr, String splitReg, float defaultVal) {
        return parseFloat(arrStr, splitReg, defaultVal, false);
    }

    public static float[] parseFloat(String arrStr, String splitReg, float defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new float[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            float[] arr = new float[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseFloat(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static double[] parseDouble(String arrStr, String splitReg, double defaultVal) {
        return parseDouble(arrStr, splitReg, defaultVal, false);
    }

    public static double[] parseDouble(String arrStr, String splitReg, double defaultVal, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr == null) {
            return rtnNull ? null : new double[0];
        } else {
            String[] strs = arrStr.split(splitReg);
            double[] arr = new double[strs.length];

            for (int i = 0; i < strs.length; ++i) {
                arr[i] = parseDouble(strs[i], defaultVal);
            }

            return arr;
        }
    }

    public static String[] parseString(String arrStr, String splitReg) {
        return parseString(arrStr, splitReg, false);
    }

    public static String[] parseString(String arrStr, String splitReg, boolean rtnNull) {
        arrStr = emptyToNull(arrStr, true);
        if (arrStr != null) {
            return arrStr.split(splitReg);
        } else {
            return rtnNull ? null : new String[0];
        }
    }

    public static boolean parse(String numStr, boolean defaultVal) {
        return parseBoolean(numStr, defaultVal);
    }

    public static short parse(String numStr, short defaultVal) {
        return parseShort(numStr, defaultVal);
    }

    public static int parse(String numStr, int defaultVal) {
        return parseInt(numStr, defaultVal);
    }

    public static long parse(String numStr, long defaultVal) {
        return parseLong(numStr, defaultVal);
    }

    public static float parse(String numStr, float defaultVal) {
        return parseFloat(numStr, defaultVal);
    }

    public static double parse(String numStr, double defaultVal) {
        return parseDouble(numStr, defaultVal);
    }

    public static boolean[] parse(String arrStr, String splitReg, boolean defaultVal) {
        return parseBoolean(arrStr, splitReg, defaultVal);
    }

    public static short[] parse(String arrStr, String splitReg, short defaultVal) {
        return parseShort(arrStr, splitReg, defaultVal);
    }

    public static int[] parse(String arrStr, String splitReg, int defaultVal) {
        return parseInt(arrStr, splitReg, defaultVal);
    }

    public static long[] parse(String arrStr, String splitReg, long defaultVal) {
        return parseLong(arrStr, splitReg, defaultVal);
    }

    public static float[] parse(String arrStr, String splitReg, float defaultVal) {
        return parseFloat(arrStr, splitReg, defaultVal);
    }

    public static double[] parse(String arrStr, String splitReg, double defaultVal) {
        return parseDouble(arrStr, splitReg, defaultVal);
    }

    public static String[] parse(String arrStr, String splitReg) {
        return parseString(arrStr, splitReg);
    }

    public static String cutString(String str, int beginIndex, int length) {
        if (str == null) {
            return null;
        } else {
            int maxIndex = str.length() - 1;
            int endIndex = beginIndex + length;
            if (beginIndex > maxIndex) {
                return "";
            } else {
                return endIndex <= maxIndex ? str.substring(beginIndex, endIndex) : str.substring(beginIndex);
            }
        }
    }

    public static String nullToDefault(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static String emptyToNull(String str) {
        return emptyToNull(str, false);
    }

    public static String emptyToNull(String str, boolean trim) {
        str = str != null && trim ? str.trim() : str;
        return "".equals(str) ? null : str;
    }

    public static String formatDouble(double value) {
        return formatDouble(value, 2, false);
    }

    public static String formatDouble(double value, boolean keep) {
        return formatDouble(value, 2, keep);
    }

    public static String formatDouble(double value, boolean keep, int cnt) {
        return formatDouble(value, cnt, keep);
    }

    public static String formatDouble(double value, int cnt) {
        return formatDouble(value, cnt, false);
    }

    public static String formatDouble(double value, int cnt, boolean keep) {
        String str = String.format("%." + Math.abs(cnt) + "f", value);
        if (keep) {
            return str;
        } else {
            char[] chars = str.toCharArray();

            int index;
            for (index = chars.length - 1; index >= 0 && chars[index] == '0'; --index) {
            }

            if (index >= 0 && chars[index] == '.') {
                --index;
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i <= index; ++i) {
                sb.append(chars[i]);
            }

            return sb.toString();
        }
    }

    public static String cutBetween(String source, String prefix, String suffix, String failed) {
        if (source == null) {
            return failed;
        } else {
            int start = source.indexOf(prefix);
            if (start < 0) {
                return failed;
            } else {
                String rtn = source.substring(start + prefix.length());
                int end = rtn.indexOf(suffix);
                if (end < 0) {
                    return failed;
                } else {
                    rtn = rtn.substring(0, end);
                    return rtn;
                }
            }
        }
    }

    public static String cutPrefix(String source, String prefix) {
        return cutPrefix(source, prefix, source);
    }

    public static String cutPrefix(String source, String prefix, String failed) {
        return source != null && source.startsWith(prefix) ? source.substring(prefix.length()) : failed;
    }

    public static boolean match(String str, String[] strs) {
        if (str != null && strs != null) {
            for (int i = 0; i < strs.length; ++i) {
                if (str.equals(strs[i])) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static String cutSuffix(String source, String suffix) {
        return cutSuffix(source, suffix, source);
    }

    public static String cutSuffix(String source, String suffix, String failed) {
        return source != null && source.endsWith(suffix) ? source.substring(0, source.length() - suffix.length()) : failed;
    }

    public static String URLDecode(String str) {
        return URLDecode(str, ConstFramework.getCharset());
    }

    public static String URLDecode(String str, String charsetName) {
        return URLDecode(str, Charset.forName(charsetName));
    }

    public static String URLDecode(String str, Charset charset) {
        if (str == null) {
            return null;
        } else {
            try {
                return URLDecoder.decode(str, charset.name());
            } catch (UnsupportedEncodingException var3) {
                return null;
            }
        }
    }

    public static String URLEncode(String str) {
        return URLEncode(str, ConstFramework.getCharset());
    }

    public static String URLEncode(String str, String charsetName) {
        return URLEncode(str, Charset.forName(charsetName));
    }

    public static String URLEncode(String str, Charset charset) {
        if (str == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(str, charset.name()).replace("+", "%20");
            } catch (UnsupportedEncodingException var3) {
                return null;
            }
        }
    }

    public static String toUnicode(String str) {
        char[] chArr = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        String ZERO = "0000";
        char[] var4 = chArr;
        int var5 = chArr.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            char c = var4[var6];
            if (c >= 0 && c <= 127) {
                sb.append(c);
            } else {
                String val = Integer.toHexString(c);
                sb.append("\\u").append("0000".substring(val.length())).append(val);
            }
        }

        return sb.toString();
    }

    public static String fromUnicode(String str) {
        char[] chArr = str.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chArr.length; ++i) {
            if (i <= chArr.length - 6 && chArr[i] == '\\' && (chArr[i + 1] == 'u' || chArr[i + 1] == 'U')) {
                int val = 0;

                for (int j = 2; j < 6; ++j) {
                    val *= 16;
                    char ch = chArr[i + j];
                    if (ch >= 'a' && ch <= 'f') {
                        val += ch - 97 + 10;
                    } else if (ch >= 'A' && ch <= 'F') {
                        val += ch - 65 + 10;
                    } else if (ch >= '0' && ch <= '9') {
                        val += ch - 48;
                    }
                }

                sb.append((char) val);
                i += 5;
            } else {
                sb.append(chArr[i]);
            }
        }

        return sb.toString();
    }

    public static String randMess(String str) {
        if (str != null && !str.isEmpty()) {
            Random rand = new Random(System.currentTimeMillis());
            ArrayList<Character> list = new ArrayList();

            for (int i = 0; i < str.length(); ++i) {
                int index = rand.nextInt(list.size() + 1);
                list.add(index, str.charAt(i));
            }

            StringBuilder sb = new StringBuilder();
            Iterator var7 = list.iterator();

            while (var7.hasNext()) {
                Character ch = (Character) var7.next();
                sb.append(ch);
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static String intMess1(String str, int param) {
        return str != null && !str.isEmpty() ? sortStr(str, genSortA(str.length(), param)) : "";
    }

    public static String intMess2(String str, int param) {
        return str != null && !str.isEmpty() ? sortStr(str, genSortB(str.length(), param)) : "";
    }

    private static String sortStr(String str, int[] sort) {
        if (str != null && !str.isEmpty()) {
            if (str.length() != sort.length) {
                return "";
            } else {
                StringBuilder sb = new StringBuilder();

                for (int i = sort.length - 1; i >= 0; --i) {
                    sb.append(str.charAt(sort[i]));
                }

                return sb.toString();
            }
        } else {
            return "";
        }
    }

    private static int[] genSortA(int length, int param) {
        int[] sortA = new int[length];

        int index;
        for (int i = 0; i < length; sortA[index] = i++) {
            index = Math.abs(IntConvert.convert1(param + i)) % (i + 1);
            System.arraycopy(sortA, index, sortA, index + 1, i - index);
        }

        return sortA;
    }

    private static int[] genSortB(int length, int param) {
        int[] sortA = genSortA(length, param);
        int[] sortB = new int[sortA.length];

        for (int i = sortA.length - 1; i >= 0; --i) {
            int index = -1;

            for (int j = 0; j < sortA.length; ++j) {
                if (sortA[j] == i) {
                    index = j;
                    break;
                }
            }

            sortB[sortA.length - 1 - i] = sortA.length - 1 - index;
        }

        return sortB;
    }

    public static String SBC2DBC(String str) {
        if (str == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (ch >= EnLowerSBC[0] && ch <= EnLowerSBC[EnLowerSBC.length - 1]) {
                    ch = (char) (ch - EnLowerSBC[0] + 97);
                } else if (ch >= EnUpperSBC[0] && ch <= EnUpperSBC[EnUpperSBC.length - 1]) {
                    ch = (char) (ch - EnUpperSBC[0] + 65);
                } else if (ch >= NumSBC[0] && ch <= NumSBC[NumSBC.length - 1]) {
                    ch = (char) (ch - NumSBC[0] + 48);
                } else {
                    for (int j = 0; j < SymSBC.length && j < SymDBC.length; ++j) {
                        if (ch == SymSBC[j]) {
                            ch = SymDBC[j];
                            break;
                        }
                    }
                }

                sb.append(ch);
            }

            return sb.toString();
        }
    }

    public static String join(String strA, String strB) {
        return join(strA, strB, (String) null);
    }

    public static String join(String strA, String strB, String joinStr) {
        return strB != null ? strA + (joinStr == null ? "" : joinStr) + strB : strA;
    }

    public static String joinArr(String[] arr) {
        return joinArr(arr, (String) null, (String) null, (String) null);
    }

    public static String joinArr(String[] arr, String joinStr) {
        return joinArr(arr, joinStr, (String) null, (String) null);
    }

    public static String joinArr(String[] arr, String joinStr, String prefixStr, String suffixStr) {
        if (arr == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean started = false;
            String[] var6 = arr;
            int var7 = arr.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                String str = var6[var8];
                if (str != null) {
                    if (started && joinStr != null) {
                        sb.append(joinStr);
                    }

                    if (prefixStr != null) {
                        sb.append(prefixStr);
                    }

                    sb.append(str);
                    if (suffixStr != null) {
                        sb.append(suffixStr);
                    }

                    started = true;
                }
            }

            return sb.toString();
        }
    }

    public static String joinList(ArrayList<String> list) {
        return joinList(list, (String) null, (String) null, (String) null);
    }

    public static String joinList(ArrayList<String> list, String joinStr) {
        return joinList(list, joinStr, (String) null, (String) null);
    }

    public static String joinList(ArrayList<String> list, String joinStr, String prefixStr, String suffixStr) {
        if (list == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean started = false;
            Iterator var6 = list.iterator();

            while (var6.hasNext()) {
                String str = (String) var6.next();
                if (str != null) {
                    if (started && joinStr != null) {
                        sb.append(joinStr);
                    }

                    if (prefixStr != null) {
                        sb.append(prefixStr);
                    }

                    sb.append(str);
                    if (suffixStr != null) {
                        sb.append(suffixStr);
                    }

                    started = true;
                }
            }

            return sb.toString();
        }
    }

    public static ArrayList<String> csvValList(String str) {
        return csvValList(str, ',');
    }

    public static ArrayList<String> csvValList(String str, char split) {
        ArrayList<String> list = new ArrayList();
        int pos = 0;

        while (true) {
            while (pos < str.length()) {
                char ch = str.charAt(pos);
                if (ch == ' ') {
                    ++pos;
                } else {
                    int end;
                    if (ch != '"') {
                        end = str.indexOf(split, pos);
                        if (end >= 0) {
                            list.add(str.substring(pos, end));
                            pos = end + 1;
                        } else {
                            list.add(str.substring(pos));
                            pos = str.length();
                        }
                    } else {
                        for (end = str.indexOf(34, pos + 1); end >= 0 && end < str.length() - 1; end = str.indexOf(34, end + 2)) {
                            char next = str.charAt(end + 1);
                            if (next != '"') {
                                break;
                            }
                        }

                        if (end >= 0) {
                            list.add(str.substring(pos + 1, end));
                            pos = end + 1;
                            end = str.indexOf(split, pos);
                            if (end >= 0) {
                                pos = end + 1;
                            } else {
                                pos = str.length();
                            }
                        } else {
                            list.add(str.substring(pos + 1));
                            pos = str.length();
                        }
                    }
                }
            }

            return list;
        }
    }

    public static String[] csvValArr(String str) {
        ArrayList<String> list = csvValList(str);
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] csvValArr(String str, char split) {
        ArrayList<String> list = csvValList(str, split);
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String csvValStr(ArrayList<String> vals) {
        return vals != null && !vals.isEmpty() ? csvValStr((String[]) vals.toArray(new String[vals.size()])) : null;
    }

    public static String csvValStr(String... vals) {
        if (vals != null && vals.length != 0) {
            StringBuilder sb = new StringBuilder();
            String[] var2 = vals;
            int var3 = vals.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String val = var2[var4];
                if (sb.length() > 0) {
                    sb.append(",");
                }

                sb.append("\"").append(val.replace("\"", "\"\"")).append("\"");
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String getHost(String url) {
        if (url != null) {
            String prefix = "://";
            int index1 = url.indexOf(prefix);
            if (index1 >= 0) {
                String part = url.substring(index1 + prefix.length());
                int index2 = part.indexOf(47);
                if (index2 >= 0) {
                    part = part.substring(0, index2);
                }

                return part;
            }
        }

        return null;
    }

    public static String getUrl(String url, boolean keep, String... params) {
        if (url == null) {
            return null;
        } else {
            if (url.startsWith("//")) {
                url = "http:" + url;
            }

            int index = url.lastIndexOf(63);
            if (index < 0) {
                return url;
            } else {
                String part1 = url.substring(0, index);
                String part2 = url.substring(index + 1);
                StringBuilder paramSb = new StringBuilder();
                String[] var7 = part2.split("&");
                int var8 = var7.length;

                for (int var9 = 0; var9 < var8; ++var9) {
                    String str = var7[var9];
                    int index2 = str.indexOf("=");
                    String paramName = str;
                    String paramValue = null;
                    if (index2 >= 0) {
                        paramName = str.substring(0, index2);
                        paramValue = str.substring(index2 + 1);
                    }

                    boolean contain = ToolArr.contain(params, paramName);
                    if (keep) {
                        if (!contain) {
                            continue;
                        }
                    } else if (contain) {
                        continue;
                    }

                    paramSb.append(paramSb.length() > 0 ? "&" : "?");
                    paramSb.append(paramName);
                    if (paramValue != null) {
                        paramSb.append("=").append(paramValue);
                    }
                }

                return part1 + paramSb.toString();
            }
        }
    }

    public static String getExtFromUrl(String url) {
        if (url != null) {
            int index = url.indexOf(63);
            if (index >= 0) {
                url = url.substring(0, index);
            }

            index = url.lastIndexOf(47);
            if (index >= 0) {
                url = url.substring(index + 1);
            }

            index = url.lastIndexOf(46);
            if (index >= 0) {
                return url.substring(index + 1);
            }
        }

        return null;
    }

    public static boolean matchKeyword(String keyword, String pattern) {
        if (pattern == null) {
            return true;
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            String[] strs = pattern.replace('，', ',').split(",");
            String[] var3 = strs;
            int var4 = strs.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String str = var3[var5];
                String[] parts = str.split("\\s+");
                boolean matched = true;
                String[] var9 = parts;
                int var10 = parts.length;

                for (int var11 = 0; var11 < var10; ++var11) {
                    String part = var9[var11];
                    if (keyword.toLowerCase().indexOf(part.toLowerCase()) < 0) {
                        matched = false;
                        break;
                    }
                }

                if (matched) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static int matchDomain(String host, String[] domains) {
        if (host != null && !host.trim().isEmpty() && domains != null) {
            String[] parts1 = host.toLowerCase().split("\\.");

            for (int i = 0; i < domains.length; ++i) {
                if (domains[i] != null) {
                    String[] parts2 = domains[i].trim().toLowerCase().split("\\.");
                    boolean matched = true;
                    if (parts1.length == parts2.length) {
                        for (int j = 0; j < parts1.length; ++j) {
                            if (!parts2[j].equals("*") && !parts2[j].equals(parts1[j])) {
                                matched = false;
                                break;
                            }
                        }
                    } else {
                        matched = false;
                    }

                    if (matched) {
                        return i;
                    }
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public static String camelize(String name) {
        if (name == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean upper = true;
            char[] var3 = name.toCharArray();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                char ch = var3[var5];
                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
                    upper = true;
                } else {
                    sb.append(upper ? Character.toUpperCase(ch) : ch);
                    upper = false;
                }
            }

            return sb.toString();
        }
    }

    public static String uncamelize(String name) {
        return uncamelize(name, "_");
    }

    public static String uncamelize(String name, String middle) {
        if (name == null) {
            return null;
        } else {
            middle = middle != null ? middle : "_";
            StringBuilder sb = new StringBuilder();
            boolean upper = false;
            char[] var4 = name.toCharArray();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                char ch = var4[var6];
                if (Character.isUpperCase(ch)) {
                    if (upper) {
                        sb.append(Character.toLowerCase(ch));
                    } else {
                        sb.append(middle).append(Character.toLowerCase(ch));
                    }

                    upper = true;
                } else {
                    sb.append(ch);
                    upper = false;
                }
            }

            return sb.toString();
        }
    }

    public static int getIntFromStr(String str) {
        return parseInt(getNumFromStr(str, false, false), 0);
    }

    public static int getIntFromStr(String str, boolean allowBreak) {
        return parseInt(getNumFromStr(str, allowBreak, false), 0);
    }

    public static long getLongFromStr(String str) {
        return parseLong(getNumFromStr(str, false, false), 0L);
    }

    public static long getLongFromStr(String str, boolean allowBreak) {
        return parseLong(getNumFromStr(str, allowBreak, false), 0L);
    }

    public static float getFloatFromStr(String str) {
        return parseFloat(getNumFromStr(str, false, true), 0.0F);
    }

    public static float getFloatFromStr(String str, boolean allowBreak) {
        return parseFloat(getNumFromStr(str, allowBreak, true), 0.0F);
    }

    public static double getDoubleFromStr(String str) {
        return parseDouble(getNumFromStr(str, false, true), 0.0D);
    }

    public static double getDoubleFromStr(String str, boolean allowBreak) {
        return parseDouble(getNumFromStr(str, allowBreak, true), 0.0D);
    }

    public static String getNumFromStr(String str, boolean allowBreak, boolean hasDot) {
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            boolean find = false;

            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    sb.append(ch);
                    find = true;
                } else if (ch == '.' && hasDot) {
                    sb.append(ch);
                } else if (find && !allowBreak) {
                    break;
                }
            }
        }

        return sb.toString();
    }
}
