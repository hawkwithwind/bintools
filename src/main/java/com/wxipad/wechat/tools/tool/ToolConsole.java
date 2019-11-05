package com.wxipad.wechat.tools.tool;

public class ToolConsole {
    public static final String NONE = "\033[0m";
    public static final String HIGH_LIGHT = "\033[1m";
    public static final String UNDER_LINE = "\033[4m";
    public static final String FLASH = "\033[5m";
    public static final String INVERSE = "\033[7m";
    public static final String OFF = "\033[8m";
    private static final String COLOR_PREFIX = "\033[";
    private static final String COLOR_SUFFIX = "m";
    private static final int COLOR_BASE = 30;
    private static final int BG_COLOR_BASE = 40;

    public static void print(String str) {
        System.out.print(str);
    }

    public static void print(String str, String[] ctrls) {
        if (ctrls != null) {
            for (String ctrl : ctrls) {
                print(ctrl);
            }
        }
        print(str);
        print("\033[0m");
    }

    public static void print(String str, COLOR color) {
        print(str, color, null);
    }

    public static void print(String str, COLOR color, COLOR bgColor) {
        if (color != null) {
            print(color.getStr());
        }
        if (bgColor != null) {
            print(bgColor.getBGStr());
        }
        print(str);
        print("\033[0m");
    }

    public static enum COLOR {
        BLACK,
        RED,
        GREEN,
        YELLOW,
        BLUE,
        PURPLE,
        DARKGREEN,
        WHITE;

        private COLOR() {
        }

        public String getStr() {
            int colorStr = 30 + ordinal();
            return "\033[" + colorStr + "m";
        }

        public String getBGStr() {
            int colorStr = 40 + ordinal();
            return "\033[" + colorStr + "m";
        }
    }
}
