//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

public class ScriptKiller {
    private static final char[] CHARR_SPLIT = " =><!?)(][}{\t\n\r".toCharArray();

    public ScriptKiller() {
    }

    public static String kill(String html) {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        while (true) {
            while (index < html.length()) {
                char ch = html.charAt(index);
                if (ch == '<') {
                    int tagNameBeginIndex = findNextChar(html, index + 1);
                    if (tagNameBeginIndex > 0) {
                        boolean endTag = html.charAt(tagNameBeginIndex) == '/';
                        if (endTag) {
                            tagNameBeginIndex = findNextChar(html, tagNameBeginIndex + 1);
                        }
                    }

                    if (tagNameBeginIndex > 0) {
                        int tagNameEndIndex = findWordEnd(html, tagNameBeginIndex);
                        String tagName = html.substring(tagNameBeginIndex, tagNameEndIndex);
                        if (tagName.toLowerCase().equals("script")) {
                            int tagEndIndex = indexOfNoneQuote(html, tagNameEndIndex, '>');
                            index = tagEndIndex + 1;
                            continue;
                        }
                    }
                }

                sb.append(ch);
                ++index;
            }

            return sb.toString();
        }
    }

    private static int indexOfNoneQuote(String source, int startIndex, char searchCh) {
        int index = startIndex;

        while (index < source.length()) {
            char ch = source.charAt(index);
            if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '=' && ch != ' ' && ch != '\t' && ch != '\n' && ch != '_' && ch != '-' && ch != ':' && ch != '.') {
                if (searchCh == ch) {
                    return index;
                }

                if (ch != '"' && ch != '\'') {
                    ++index;
                } else {
                    int match = source.indexOf(ch, index + 1);
                    if (match < 0 || match >= source.length()) {
                        break;
                    }

                    index = match + 1;
                }
            } else {
                ++index;
            }
        }

        return -1;
    }

    private static boolean isWordChar(char ch) {
        if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_' && ch != '-' && ch != ':' && ch != '.') {
            for (int i = 0; i < CHARR_SPLIT.length; ++i) {
                if (ch == CHARR_SPLIT[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    private static int findNextChar(String source, int startIndex) {
        if (startIndex >= 0) {
            for (int i = startIndex; i < source.length(); ++i) {
                char ch = source.charAt(i);
                if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                    return i;
                }
            }
        }

        return -1;
    }

    private static int findWordEnd(String source, int startIndex) {
        if (startIndex >= 0) {
            for (int i = startIndex; i < source.length(); ++i) {
                if (!isWordChar(source.charAt(i))) {
                    return i;
                }
            }
        }

        return source.length();
    }
}
