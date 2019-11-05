package com.wxipad.wechat.tools.tool;

import java.util.ArrayList;

public class ToolLink {
    public static String convertLink(String href, String[] oldPrefix, String[] newPrefix) {
        return convertLink(href, oldPrefix, newPrefix, null);
    }

    public static String convertLink(String href, String[] oldPrefix, String[] newPrefix, String defaultHref) {
        if ((href == null) || (oldPrefix == null) || (newPrefix == null) || (oldPrefix.length != newPrefix.length)) {
            return defaultHref;
        }
        for (int i = 0; i < oldPrefix.length; i++) {
            if (href.startsWith(oldPrefix[i])) {
                return newPrefix[i] + href.substring(oldPrefix[i].length());
            }
        }
        return href;
    }

    public static String convertLink(String href, ArrayList<String> oldPrefix, ArrayList<String> newPrefix) {
        return convertLink(href, oldPrefix, newPrefix, null);
    }

    public static String convertLink(String href, ArrayList<String> oldPrefix, ArrayList<String> newPrefix, String defaultHref) {
        if ((href == null) || (oldPrefix == null) || (newPrefix == null) || (oldPrefix.size() != newPrefix.size())) {
            return defaultHref;
        }
        for (int i = 0; i < oldPrefix.size(); i++) {
            if (href.startsWith((String) oldPrefix.get(i))) {
                return (String) newPrefix.get(i) + href.substring(((String) oldPrefix.get(i)).length());
            }
        }
        return href;
    }
}
