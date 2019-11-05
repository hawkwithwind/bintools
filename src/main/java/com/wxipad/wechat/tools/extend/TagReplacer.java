//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.util.HashMap;

public class TagReplacer {
    private static final char[] CHARR_SPLIT = " =><!?)(][}{\t\n\r".toCharArray();

    public TagReplacer() {
    }

    public static String replace(String html, Replacer replacer) {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        while (true) {
            char ch;
            boolean doSkip;
            do {
                if (index >= html.length()) {
                    return sb.toString();
                }

                ch = html.charAt(index);
                if (ch != '<') {
                    break;
                }

                int tagNameBeginIndex = findNextChar(html, index + 1);
                boolean endTag = false;
                if (tagNameBeginIndex > 0) {
                    endTag = html.charAt(tagNameBeginIndex) == '/';
                    if (endTag) {
                        tagNameBeginIndex = findNextChar(html, tagNameBeginIndex + 1);
                    }
                }

                if (tagNameBeginIndex <= 0) {
                    break;
                }

                int tagNameEndIndex = findWordEnd(html, tagNameBeginIndex);
                String tagName = html.substring(tagNameBeginIndex, tagNameEndIndex).toLowerCase();
                if (!replacer.doreplace(tagName)) {
                    break;
                }

                int tagEndIndex = indexOfNoneQuote(html, tagNameEndIndex, '>');
                index = tagEndIndex + 1;
                doSkip = true;
                if (!endTag) {
                    HashMap<String, String> attrs = getAttrs(html, tagNameEndIndex, tagEndIndex);
                    String replaceHtml = replacer.replace(tagName, attrs);
                    if (replaceHtml != null) {
                        sb.append(replaceHtml);
                    } else {
                        doSkip = false;
                    }
                }
            } while (doSkip);

            sb.append(ch);
            ++index;
        }
    }

    private static HashMap<String, String> getAttrs(String html, int start, int end) {
        HashMap<String, String> attrs = new HashMap();
        int index = start;

        while (index < end) {
            int indexStart = findNextChar(html, index);
            if (indexStart < 0) {
                break;
            }

            int indexEnd = findWordEnd(html, indexStart);
            if (indexEnd >= end) {
                break;
            }

            String attrName = html.substring(indexStart, indexEnd).toLowerCase();
            indexStart = findNextChar(html, indexEnd);
            if (indexStart < 0) {
                attrs.put(attrName, null);
                break;
            }

            char ch = html.charAt(indexStart);
            if (ch != '=') {
                attrs.put(attrName, null);
                index = indexStart;
            } else {
                indexStart = findNextChar(html, indexStart + 1);
                if (indexStart < 0) {
                    attrs.put(attrName, null);
                    break;
                }

                ch = html.charAt(indexStart);
                String attrValue;
                if (ch != '\'' && ch != '"') {
                    for (indexEnd = indexStart; indexEnd < end; ++indexEnd) {
                        ch = html.charAt(indexEnd);
                        if (ch == ' ' || ch == '>') {
                            break;
                        }
                    }

                    if (indexEnd >= end) {
                        attrValue = html.substring(indexStart, end);
                        attrs.put(attrName, attrValue);
                        break;
                    }

                    attrValue = html.substring(indexStart, indexEnd);
                    attrs.put(attrName, attrValue);
                    index = indexEnd;
                } else {
                    ++indexStart;
                    indexEnd = html.indexOf(ch, indexStart);
                    if (indexEnd < 0 || indexEnd >= end) {
                        attrValue = html.substring(indexStart, end);
                        attrs.put(attrName, attrValue);
                        break;
                    }

                    attrValue = html.substring(indexStart, indexEnd);
                    attrs.put(attrName, attrValue);
                    index = indexEnd + 1;
                }
            }
        }

        return attrs;
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

    public abstract static class Replacer {
        public Replacer() {
        }

        public abstract boolean doreplace(String var1);

        public abstract String replace(String var1, HashMap<String, String> var2);
    }
}
