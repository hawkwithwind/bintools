//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.constant;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConstFramework {
    public static final int FRAMEWORK_VERSION = 20202;
    public static final String FRAMEWORK_VERSION_STR = "2.2.2";
    public static final String CODE_VERSION = getCodeVersion();
    private static boolean debugMode = false;
    private static Charset defaultCharset = Charset.forName("UTF-8");

    public ConstFramework() {
    }

    public static boolean getDebug() {
        return debugMode;
    }

    public static void setDebug(boolean debug) {
        debugMode = debug;
    }

    public static Charset getCharset() {
        return defaultCharset == null ? Charset.defaultCharset() : defaultCharset;
    }

    public static void setCharset(Charset charset) {
        defaultCharset = charset;
    }

    public static void setCharset(String charsetName) {
        defaultCharset = Charset.forName(charsetName);
    }

    public static String getCharsetName() {
        return defaultCharset.name();
    }

    private static String getCodeVersion() {
        char rand = 65;
        rand = (char) (rand + (int) (Math.random() * 26.0D));
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date()) + rand;
    }
}
