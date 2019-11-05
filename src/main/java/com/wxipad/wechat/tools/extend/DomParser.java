//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DomParser {
    private static final String[] SPECIAL_TAG = new String[]{"style", "script"};
    private static final String[] SINGLE_TAG = new String[]{"br", "hr", "img", "input", "param", "meta", "link", "area"};
    private static final String DEFAULT_SPACE = "  ";
    private static final String DEFAULT_LINE = "\n";
    public Node document;

    private DomParser(Node document) {
        this.document = document;
    }

    public static NodeElement createElement(String name) {
        return new NodeElement(name);
    }

    public static DomParser create() {
        return new DomParser(new Node());
    }

    public static DomParser create(Node document) {
        return new DomParser(document);
    }

    public static DomParser create(String document) {
        return new DomParser(build(document, false));
    }

    public static DomParser create(String document, boolean xmlmode) {
        return new DomParser(build(document, xmlmode));
    }

    public static DomParser create(File file) throws IOException {
        return create(file, (String) null);
    }

    public static DomParser create(File file, String encode) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        if (encode == null) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
        }

        try {
            char[] buff = new char[1024];

            int read;
            while ((read = reader.read(buff)) != -1) {
                sb.append(buff, 0, read);
            }
        } finally {
            reader.close();
        }

        return create(sb.toString());
    }

    private static Node build(String document, boolean xmlmode) {
        Node root = new Node();
        build(root, document, 0, document.length(), xmlmode);
        return root;
    }

    private static void build(Node context, String source, int start, int end, boolean xmlmode) {
        int index = start;

        while (index < end) {
            int tagStartIndex = source.indexOf(60, index);
            String text;
            if (tagStartIndex < 0 || tagStartIndex >= end) {
                text = source.substring(index, end);
                ((Node) context).addChild(new NodeText(text));
                break;
            }

            if (tagStartIndex > index) {
                text = source.substring(index, tagStartIndex);
                ((Node) context).addChild(new NodeText(text));
            }

            if (tagStartIndex + 1 >= end) {
                break;
            }

            char firstChar = source.charAt(tagStartIndex + 1);
            int nameEnd;
            String name;
            int tagEndIndex;
            String content;
            if (firstChar == '?') {
                nameEnd = searchWordEnd(source, tagStartIndex + 2, end);
                if (nameEnd < 0 || nameEnd >= end) {
                    break;
                }

                name = source.substring(tagStartIndex + 2, nameEnd);
                tagEndIndex = searchMatchStr(source, nameEnd, end, "?>", !xmlmode, true, true);
                if (tagEndIndex < 0 || tagEndIndex >= end) {
                    break;
                }

                content = source.substring(nameEnd, tagEndIndex);
                ((Node) context).addChild(new NodeDec(name, content));
                index = tagEndIndex + 2;
            } else if (firstChar == '!') {
                if (tagStartIndex + 3 < end && source.charAt(tagStartIndex + 2) == '-' && source.charAt(tagStartIndex + 3) == '-') {
                    nameEnd = source.indexOf("-->", tagStartIndex + 4);
                    if (nameEnd < 0 || nameEnd >= end) {
                        break;
                    }

                    name = source.substring(tagStartIndex + 4, nameEnd);
                    ((Node) context).addChild(new NodeComment(name));
                    index = nameEnd + 3;
                } else if (tagStartIndex + 8 < end && source.startsWith("[CDATA[", tagStartIndex + 2)) {
                    nameEnd = source.indexOf("]]>", tagStartIndex + 9);
                    if (nameEnd < 0 || nameEnd >= end) {
                        break;
                    }

                    name = source.substring(tagStartIndex + 9, nameEnd);
                    ((Node) context).addChild(new NodeCDATA(name));
                    index = nameEnd + 3;
                } else {
                    nameEnd = searchWordEnd(source, tagStartIndex + 2, end);
                    if (nameEnd < 0 || nameEnd >= end) {
                        break;
                    }

                    name = source.substring(tagStartIndex + 2, nameEnd);
                    tagEndIndex = searchMatchChar(source, nameEnd, end, '>', !xmlmode, true, true);
                    if (tagEndIndex < 0 || tagEndIndex >= end) {
                        break;
                    }

                    content = source.substring(nameEnd, tagEndIndex);
                    ((Node) context).addChild(new NodeDef(name, content));
                    index = tagEndIndex + 1;
                }
            } else if (firstChar == '/') {
                nameEnd = searchMatchChar(source, tagStartIndex + 2, end, '>', !xmlmode, false, true);
                if (nameEnd < 0 || nameEnd >= end) {
                    break;
                }

                name = source.substring(tagStartIndex + 2, nameEnd).trim();
                if (context instanceof NodeElement) {
                    NodeElement elmNode = (NodeElement) context;
                    if (!elmNode.closed && strEqual(name, elmNode.name, !xmlmode) && ((Node) context).parent != null) {
                        context = ((Node) context).parent;
                    }
                }

                index = nameEnd + 1;
            } else {
                nameEnd = searchWordEnd(source, tagStartIndex + 1, end);
                if (nameEnd < 0 || nameEnd >= end) {
                    break;
                }

                name = source.substring(tagStartIndex + 1, nameEnd);
                tagEndIndex = source.indexOf(62, nameEnd);
                if (tagEndIndex < 0 || tagEndIndex >= end) {
                    break;
                }

                int closeSymbolIndex = searchMatchChar(source, nameEnd, tagEndIndex, '/', !xmlmode, false, true);
                boolean selfClose = closeSymbolIndex >= 0 && closeSymbolIndex < tagEndIndex;
                closeSymbolIndex = closeSymbolIndex < 0 ? tagEndIndex : closeSymbolIndex;
                if (singleTag(name, xmlmode) >= 0) {
                    selfClose = true;
                }

                ArrayList<Attribute> attrs = buildAttrs(source, nameEnd, selfClose ? closeSymbolIndex : tagEndIndex);
                int specialIndex = specialTag(name, xmlmode);
                if (specialIndex < 0) {
                    NodeElement elmNode = new NodeElement(name, attrs, selfClose);
                    ((Node) context).addChild(elmNode);
                    if (!selfClose) {
                        context = elmNode;
                    }

                    index = tagEndIndex + 1;
                } else if (selfClose) {
                    NodeSpecial speNode = new NodeSpecial(name, attrs, (String) null);
                    ((Node) context).addChild(speNode);
                    index = tagEndIndex + 1;
                } else {
                    int specialSearchIndex = tagEndIndex + 1;
                    int contentStartIndex = specialSearchIndex;
                    int contentEndIndex = end;

                    boolean find;
                    int closeTagEndIndex;
                    for (find = false; specialSearchIndex < end && !find; specialSearchIndex = closeTagEndIndex + 1) {
                        contentEndIndex = searchMatchStr(source, specialSearchIndex, end, "</", !xmlmode, false, true);
                        if (contentEndIndex < 0 || contentEndIndex >= end) {
                            break;
                        }

                        closeTagEndIndex = searchMatchChar(source, contentEndIndex + 2, end, '>', !xmlmode, false, true);
                        if (closeTagEndIndex < 0 || closeTagEndIndex >= end) {
                            break;
                        }

                        String closeTagName = source.substring(contentEndIndex + 2, closeTagEndIndex);
                        if (strEqual(name, closeTagName.trim(), !xmlmode)) {
                            find = true;
                        }
                    }

                    if (!find) {
                        break;
                    }

                    content = source.substring(contentStartIndex, contentEndIndex);
                    NodeSpecial speNode = new NodeSpecial(name, attrs, content);
                    ((Node) context).addChild(speNode);
                    index = specialSearchIndex;
                }
            }
        }

    }

    private static int specialTag(String name, boolean xmlmode) {
        if (xmlmode) {
            return -1;
        } else {
            name = name.toLowerCase();

            for (int i = 0; i < SPECIAL_TAG.length; ++i) {
                if (SPECIAL_TAG[i].equals(name)) {
                    return i;
                }
            }

            return -1;
        }
    }

    private static int singleTag(String name, boolean xmlmode) {
        if (xmlmode) {
            return -1;
        } else {
            name = name.toLowerCase();

            for (int i = 0; i < SINGLE_TAG.length; ++i) {
                if (SINGLE_TAG[i].equals(name)) {
                    return i;
                }
            }

            return -1;
        }
    }

    private static ArrayList<Attribute> buildAttrs(String source, int start, int end) {
        ArrayList<Attribute> attrs = new ArrayList();
        int index = start;

        while (index < end) {
            int indexStart = searchNextChar(source, index, end);
            if (indexStart < 0) {
                break;
            }

            int indexEnd = searchWordEnd(source, indexStart, end);
            if (indexEnd >= end) {
                break;
            }

            String attrName = source.substring(indexStart, indexEnd);
            if (attrName.isEmpty()) {
                index = indexStart + 1;
            } else {
                indexStart = searchNextChar(source, indexEnd, end);
                if (indexStart < 0) {
                    attrs.add(new Attribute(attrName, (String) null));
                    break;
                }

                char ch = source.charAt(indexStart);
                if (ch != '=') {
                    attrs.add(new Attribute(attrName, (String) null));
                    index = indexStart;
                } else {
                    indexStart = searchNextChar(source, indexStart + 1, end);
                    if (indexStart < 0) {
                        attrs.add(new Attribute(attrName, (String) null));
                        break;
                    }

                    ch = source.charAt(indexStart);
                    String attrValue;
                    if (ch != '\'' && ch != '"') {
                        for (indexEnd = indexStart; indexEnd < end; ++indexEnd) {
                            ch = source.charAt(indexEnd);
                            if (ch == ' ' || ch == '>') {
                                break;
                            }
                        }

                        if (indexEnd >= end) {
                            attrValue = source.substring(indexStart, end);
                            attrs.add(new Attribute(attrName, attrValue, '\u0000'));
                            break;
                        }

                        attrValue = source.substring(indexStart, indexEnd);
                        attrs.add(new Attribute(attrName, attrValue, '\u0000'));
                        index = indexEnd;
                    } else {
                        ++indexStart;
                        indexEnd = source.indexOf(ch, indexStart);
                        if (indexEnd < 0 || indexEnd >= end) {
                            attrValue = source.substring(indexStart, end);
                            attrs.add(new Attribute(attrName, attrValue, ch));
                            break;
                        }

                        attrValue = source.substring(indexStart, indexEnd);
                        attrs.add(new Attribute(attrName, attrValue, ch));
                        index = indexEnd + 1;
                    }
                }
            }
        }

        return attrs;
    }

    private static int searchNextChar(String source, int start, int end) {
        if (start >= 0) {
            for (int i = start; i < source.length() && i < end; ++i) {
                char ch = source.charAt(i);
                if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                    return i;
                }
            }
        }

        return -1;
    }

    private static int searchWordEnd(String source, int start, int end) {
        if (start >= 0) {
            for (int i = start; i < source.length() && i < end; ++i) {
                if (!isWordChar(source.charAt(i))) {
                    return i;
                }
            }
        }

        return end;
    }

    private static int searchMatchStr(String source, int start, int end, String searchStr, boolean ignoreCase, boolean hasStack, boolean quoteEscape) {
        if (source != null && !"".equals(source)) {
            int newEndIndex = end - searchStr.length() + 1;
            char[] chs = searchStr.toCharArray();

            int index;
            for (index = searchMatchChar(source, start, newEndIndex, chs[0], ignoreCase, hasStack, quoteEscape); index >= 0; index = searchMatchChar(source, index + 1, newEndIndex, chs[0], ignoreCase, hasStack, quoteEscape)) {
                boolean match = true;

                for (int i = 1; i < chs.length; ++i) {
                    if (!chEqual(chs[i], source.charAt(index + i), ignoreCase)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    return index;
                }
            }

            return index;
        } else {
            return -1;
        }
    }

    private static int searchMatchChar(String source, int start, int end, char searchCh, boolean ignoreCase, boolean hasStack, boolean quoteEscape) {
        ArrayList<Character> stack = null;
        if (hasStack) {
            stack = new ArrayList();
        }

        int index = start;

        while (index < end) {
            char ch = source.charAt(index);
            if (chEqual(searchCh, ch, ignoreCase)) {
                if (!hasStack) {
                    return index;
                }

                if (stack.isEmpty()) {
                    return index;
                }
            }

            if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '=' && ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r' && ch != '_' && ch != '-' && ch != ':' && ch != '.') {
                int size;
                if (ch != '"' && ch != '\'') {
                    if (hasStack) {
                        if (ch == '<' || ch == '(' || ch == '[' || ch == '{') {
                            stack.add(ch);
                            ++index;
                            continue;
                        }

                        if (ch == '>' || ch == ')' || ch == ']' || ch == '}') {
                            size = stack.size();
                            if (stack.isEmpty()) {
                                break;
                            }

                            char stackCh = (Character) stack.get(size - 1);
                            boolean match = false;
                            if (!match && stackCh == '<' && ch == '>') {
                                match = true;
                            }

                            if (!match && stackCh == '(' && ch == ')') {
                                match = true;
                            }

                            if (!match && stackCh == '[' && ch == ']') {
                                match = true;
                            }

                            if (!match && stackCh == '{' && ch == '}') {
                                match = true;
                            }

                            if (!match) {
                                break;
                            }

                            stack.remove(size - 1);
                            ++index;
                            continue;
                        }
                    }

                    ++index;
                } else {
                    size = source.indexOf(ch, index + 1);
                    if (quoteEscape) {
                        while (size > 0 && source.charAt(size - 1) == '\\') {
                            size = source.indexOf(ch, size + 1);
                        }
                    }

                    if (size < 0 || size >= end) {
                        break;
                    }

                    index = size + 1;
                }
            } else {
                ++index;
            }
        }

        return -1;
    }

    private static boolean chEqual(char ch1, char ch2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(ch1) == Character.toLowerCase(ch2);
        } else {
            return ch1 == ch2;
        }
    }

    private static boolean strEqual(String str1, String str2, boolean ignoreCase) {
        if (str1 != null && str2 != null) {
            return ignoreCase ? str1.toLowerCase().equals(str2.toLowerCase()) : str1.equals(str2);
        } else {
            return false;
        }
    }

    private static boolean isWordChar(char ch) {
        if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_' && ch != '-' && ch != ':' && ch != '.') {
            return ch != ' ' && ch != '=' && ch != '\t' && ch != '\n' && ch != '\r' && ch != '<' && ch != '>' && ch != '!' && ch != '>' && ch != '(' && ch != ')' && ch != '[' && ch != ']' && ch != '{' && ch != '}' && ch != '\\' && ch != '/';
        } else {
            return true;
        }
    }

    public String toString() {
        return this.document.toString();
    }

    public static class Attribute {
        public static final char NONE_QUOTE = '\u0000';
        public static final char DEFAULT_QUOTE = '"';
        public String name;
        public String value;
        public char quote;

        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
            this.quote = '"';
        }

        public Attribute(String name, String value, char quote) {
            this.name = name;
            this.value = value;
            this.quote = quote;
        }

        public String toString() {
            if (this.quote == 0) {
                return this.value == null ? this.name : this.name + "=" + this.value;
            } else {
                return this.value == null ? this.name : this.name + "=" + this.quote + this.value + this.quote;
            }
        }
    }

    public static class NodeSpecial extends NodeElement {
        public String content;

        public NodeSpecial(String name, ArrayList<Attribute> attrs, String content) {
            super(name, attrs);
            this.content = content;
            this.closed = content == null;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<");
            sb.append(this.name);
            if (this.attrs != null) {
                Iterator var6 = this.attrs.iterator();

                while (var6.hasNext()) {
                    Attribute attr = (Attribute) var6.next();
                    sb.append(" ").append(attr.toString());
                }
            }

            if (this.closed) {
                return sb.append("/>").toString();
            } else {
                sb.append(">");
                if (this.content != null) {
                    sb.append(this.content);
                }

                return sb.append("</").append(this.name).append(">").toString();
            }
        }
    }

    public static class NodeElement extends Node {
        public String name;
        public ArrayList<Attribute> attrs;
        public boolean closed;

        public NodeElement(String name) {
            this.name = name;
            this.attrs = null;
            this.closed = false;
        }

        public NodeElement(String name, boolean closed) {
            this.name = name;
            this.attrs = null;
            this.closed = closed;
        }

        public NodeElement(String name, ArrayList<Attribute> attrs) {
            this.name = name;
            this.attrs = attrs;
            this.closed = false;
        }

        public NodeElement(String name, ArrayList<Attribute> attrs, boolean closed) {
            this.name = name;
            this.attrs = attrs;
            this.closed = closed;
        }

        public boolean setAttr(String name, String value) {
            return this.setAttr(name, value, true);
        }

        public boolean setAttr(String name, String value, boolean ignoreCase) {
            if (this.attrs == null) {
                this.attrs = new ArrayList();
            }

            Attribute attr = this.getAttr(name, ignoreCase);
            if (attr != null) {
                attr.value = value;
                return false;
            } else {
                this.attrs.add(new Attribute(name, value));
                return true;
            }
        }

        public Attribute getAttr(String name) {
            return this.getAttr(name, true);
        }

        public Attribute getAttr(String name, boolean ignoreCase) {
            if (this.attrs != null) {
                Iterator var3 = this.attrs.iterator();

                while (var3.hasNext()) {
                    Attribute attr = (Attribute) var3.next();
                    if (DomParser.strEqual(attr.name, name, ignoreCase)) {
                        return attr;
                    }
                }
            }

            return null;
        }

        public String getAttrValue(String name) {
            return this.getAttrValue(name, true);
        }

        public String getAttrValue(String name, boolean ignoreCase) {
            Attribute attr = this.getAttr(name, true);
            return attr == null ? null : attr.value;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<");
            sb.append(this.name);
            if (this.attrs != null) {
                Iterator var6 = this.attrs.iterator();

                while (var6.hasNext()) {
                    Attribute attr = (Attribute) var6.next();
                    sb.append(" ").append(attr.toString());
                }
            }

            if (this.closed) {
                return sb.append("/>").toString();
            } else {
                sb.append(">");
                boolean addPrefixSuffix = format && this.children != null && this.children.size() > 0;
                if (addPrefixSuffix) {
                    sb.append(line);
                }

                sb.append(super.toString(format, space, line, levelref));
                if (addPrefixSuffix) {
                    sb.append(line);
                    int level = this.getLevel() + levelref;

                    for (int i = 0; i < level; ++i) {
                        sb.append(space);
                    }
                }

                return sb.append("</").append(this.name).append(">").toString();
            }
        }
    }

    public static class NodeDef extends Node {
        public String name;
        public String content;

        public NodeDef(String name) {
            this.name = name;
            this.content = null;
        }

        public NodeDef(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<!");
            sb.append(this.name);
            if (this.content != null) {
                sb.append(this.content);
            }

            return sb.append(">").toString();
        }
    }

    public static class NodeDec extends Node {
        public String name;
        public String content;

        public NodeDec() {
            this.name = null;
            this.content = null;
        }

        public NodeDec(String name) {
            this.name = name;
            this.content = null;
        }

        public NodeDec(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<?");
            if (this.name != null) {
                sb.append(this.name);
            }

            if (this.content != null) {
                sb.append(this.content);
            }

            return sb.append("?>").toString();
        }
    }

    public static class NodeCDATA extends Node {
        public String content;

        public NodeCDATA(String content) {
            this.content = content;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            return "<![CDATA[" + this.content + "]]>";
        }
    }

    public static class NodeComment extends Node {
        public String comment;

        public NodeComment(String comment) {
            this.comment = comment;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            return "<!--" + this.comment + "-->";
        }
    }

    public static class NodeText extends Node {
        public String text;

        public NodeText(String text) {
            this.text = text;
        }

        public String toString(boolean format, String space, String line, int levelref) {
            return this.text;
        }
    }

    public static class Node {
        public Node parent;
        public ArrayList<Node> children;

        public Node() {
        }

        private static void findNodesByFilter(ArrayList<Node> matches, Filter filter, Node context) {
            Node child;
            if (context.children != null) {
                for (Iterator var3 = context.children.iterator(); var3.hasNext(); findNodesByFilter(matches, filter, child)) {
                    child = (Node) var3.next();
                    if (filter.match(child)) {
                        matches.add(child);
                    }
                }
            }

        }

        private static void findChildNodesByFilter(ArrayList<Node> matches, Filter filter, Node context) {
            if (context.children != null) {
                Iterator var3 = context.children.iterator();

                while (var3.hasNext()) {
                    Node child = (Node) var3.next();
                    if (filter.match(child)) {
                        matches.add(child);
                    }
                }
            }

        }

        private static ArrayList<String> getSelectorWordList(String cssSelector) {
            ArrayList<String> words = new ArrayList();
            char[] splitCh = " #.*:^$|>+~=[]\t\r\n".toCharArray();
            StringBuilder buff = new StringBuilder();
            int index = 0;

            while (index < cssSelector.length()) {
                char ch = cssSelector.charAt(index);
                boolean doSplit;
                if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
                    doSplit = false;
                } else {
                    int i;
                    if (ch == '"' || ch == '\'') {
                        i = cssSelector.indexOf(ch, index + 1);
                        if (i < 0) {
                            break;
                        }

                        String buffStr = buff.toString();
                        if (!buffStr.isEmpty()) {
                            words.add(buffStr);
                        }

                        buff = new StringBuilder();
                        String str = cssSelector.substring(index + 1, i);
                        words.add(str);
                        index = i + 1;
                        continue;
                    }

                    if (ch == '(') {
                        i = cssSelector.indexOf(41, index + 1);
                        if (i < 0) {
                            break;
                        }

                        buff.append(cssSelector.substring(index, i + 1));
                        index = i + 1;
                        continue;
                    }

                    doSplit = false;

                    for (i = 0; i < splitCh.length; ++i) {
                        if (splitCh[i] == ch) {
                            doSplit = true;
                            break;
                        }
                    }
                }

                if (doSplit) {
                    String buffStr = buff.toString();
                    if (!buffStr.isEmpty()) {
                        words.add(buffStr);
                    }

                    buff = new StringBuilder();
                    if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                        words.add(Character.toString(ch));
                    }
                } else {
                    buff.append(ch);
                }

                ++index;
            }

            String buffStr = buff.toString();
            if (!buffStr.isEmpty()) {
                words.add(buffStr);
            }

            return words;
        }

        private static ArrayList<NodeElement> mergeNodes(ArrayList<NodeElement> list1, ArrayList<NodeElement> list2) {
            if (list1 != null && list2 != null) {
                Iterator var2 = list2.iterator();

                while (var2.hasNext()) {
                    NodeElement node = (NodeElement) var2.next();
                    if (!list1.contains(node)) {
                        list1.add(node);
                    }
                }

                return list1;
            } else {
                return list1 != null ? list1 : list2;
            }
        }

        private static ArrayList<NodeElement> searchElement(ArrayList<Node> context, char oper1, String str1) {
            return searchElement(context, oper1, str1, ' ', (String) null);
        }

        private static ArrayList<NodeElement> searchElement(ArrayList<Node> context, char oper1, String str1, char oper2, String str2) {
            ArrayList<NodeElement> findNodes = null;
            Iterator var6;
            Node cNode;
            switch (oper1) {
                case '\u0000':
                case ' ':
                    for (var6 = context.iterator(); var6.hasNext(); findNodes = mergeNodes(findNodes, cNode.getElementsByTagName(str1))) {
                        cNode = (Node) var6.next();
                    }

                    return mergeNodes(new ArrayList(), findNodes);
                case '#':
                    for (var6 = context.iterator(); var6.hasNext(); findNodes = mergeNodes(findNodes, cNode.getElementById(str1))) {
                        cNode = (Node) var6.next();
                    }

                    return mergeNodes(new ArrayList(), findNodes);
                case '+':
                    findNodes = filterNextElementsByTagName(context, str1, true);
                    break;
                case '.':
                    for (var6 = context.iterator(); var6.hasNext(); findNodes = mergeNodes(findNodes, cNode.getElementsByClassName(str1))) {
                        cNode = (Node) var6.next();
                    }

                    return mergeNodes(new ArrayList(), findNodes);
                case ':':
                    findNodes = filterElementsByPseudoSelector(context, str1);
                    break;
                case '>':
                    findNodes = filterChildElementsByTagName(context, str1);
                    break;
                case '[':
                    findNodes = filterElementsByAttr(context, str1, str2, oper2);
                    break;
                case '~':
                    findNodes = filterNextElementsByTagName(context, str1, false);
            }

            return mergeNodes(new ArrayList(), findNodes);
        }

        private static ArrayList<NodeElement> filterElementsByAttr(ArrayList<Node> context, String attrName, String attrValue, char oper) {
            ArrayList<NodeElement> result = new ArrayList();
            Iterator var5 = context.iterator();

            while (true) {
                while (true) {
                    Node node;
                    do {
                        if (!var5.hasNext()) {
                            return result;
                        }

                        node = (Node) var5.next();
                    } while (!(node instanceof NodeElement));

                    NodeElement element = (NodeElement) node;
                    if (attrValue == null) {
                        if (element.getAttr(attrName) != null) {
                            result.add(element);
                        }
                    } else {
                        String getValue = element.getAttrValue(attrName);
                        if (getValue != null) {
                            String[] values1;
                            switch (oper) {
                                case '\u0000':
                                case ' ':
                                    if (attrValue.equals(getValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '$':
                                    if (getValue.endsWith(attrValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '*':
                                    if (getValue.indexOf(attrValue) >= 0) {
                                        result.add(element);
                                    }
                                    break;
                                case '^':
                                    if (getValue.startsWith(attrValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '|':
                                    values1 = getValue.split("-");
                                    String[] values2 = attrValue.split("-");
                                    boolean match = true;

                                    for (int i = 0; i < values2.length; ++i) {
                                        if (!values2[i].equals(values1[i])) {
                                            match = false;
                                            break;
                                        }
                                    }

                                    if (match) {
                                        result.add(element);
                                    }
                                    break;
                                case '~':
                                    String[] getValues = getValue.split("\\s");
                                    values1 = getValues;
                                    int var11 = getValues.length;

                                    for (int var12 = 0; var12 < var11; ++var12) {
                                        String val = values1[var12];
                                        if (attrValue.equals(val)) {
                                            result.add(element);
                                            break;
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }

        private static ArrayList<NodeElement> filterChildElementsByTagName(ArrayList<Node> context, String tagName) {
            ArrayList<NodeElement> result = new ArrayList();
            boolean addAll = "*".equals(tagName);
            Iterator var4 = context.iterator();

            label37:
            while (true) {
                Node node;
                do {
                    if (!var4.hasNext()) {
                        return result;
                    }

                    node = (Node) var4.next();
                } while (node.children == null);

                Iterator var6 = node.children.iterator();

                while (true) {
                    NodeElement element;
                    do {
                        Node child;
                        do {
                            if (!var6.hasNext()) {
                                continue label37;
                            }

                            child = (Node) var6.next();
                        } while (!(child instanceof NodeElement));

                        element = (NodeElement) child;
                    } while (!addAll && !tagName.equals(element.name));

                    result.add(element);
                }
            }
        }

        private static ArrayList<NodeElement> filterElementsByPseudoSelector(ArrayList<Node> context, String selector) {
            ArrayList<NodeElement> result = new ArrayList();
            selector = selector.toLowerCase();
            String inner = null;
            int startIndex = selector.indexOf(40);
            if (startIndex >= 0) {
                int endIndex = selector.indexOf(41);
                inner = selector.substring(startIndex + 1, endIndex);
                selector = selector.substring(0, startIndex);
            }

            Iterator var15 = context.iterator();

            while (true) {
                Node node;
                NodeElement element;
                label187:
                do {
                    while (var15.hasNext()) {
                        node = (Node) var15.next();
                        if ("root".equals(selector)) {
                            continue label187;
                        }

                        ArrayList children;
                        int index;
                        if ("nth-child".equals(selector)) {
                            try {
                                index = Integer.parseInt(inner);
                                if (node instanceof NodeElement && node.parent != null) {
                                    element = (NodeElement) node;
                                    children = node.parent.getChildElements();
                                    if (index >= 0 && index < children.size() && ((NodeElement) children.get(index)).equals(element)) {
                                        result.add(element);
                                    }
                                }
                            } catch (Exception var14) {
                            }
                        } else {
                            int size;
                            if ("nth-last-child".equals(selector)) {
                                try {
                                    index = Integer.parseInt(inner);
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements();
                                        size = children.size();
                                        if (index >= 0 && index < size && ((NodeElement) children.get(size - index - 1)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } catch (Exception var13) {
                                }
                            } else if ("nth-of-type".equals(selector)) {
                                try {
                                    index = Integer.parseInt(inner);
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements(element.name);
                                        if (index >= 0 && index < children.size() && ((NodeElement) children.get(index)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } catch (Exception var12) {
                                }
                            } else if ("nth-last-of-type".equals(selector)) {
                                try {
                                    index = Integer.parseInt(inner);
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements(element.name);
                                        size = children.size();
                                        if (index >= 0 && index < size && ((NodeElement) children.get(size - index - 1)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } catch (Exception var11) {
                                }
                            } else {
                                if ("first-child".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements();
                                        if (!children.isEmpty() && ((NodeElement) children.get(0)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("last-child".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements();
                                        if (!children.isEmpty() && ((NodeElement) children.get(children.size() - 1)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("first-of-type".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements(element.name);
                                        if (!children.isEmpty() && ((NodeElement) children.get(0)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("last-of-type".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements(element.name);
                                        if (!children.isEmpty() && ((NodeElement) children.get(children.size() - 1)).equals(element)) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("only-child".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements();
                                        if (children.size() == 1) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("only-of-type".equals(selector)) {
                                    if (node instanceof NodeElement && node.parent != null) {
                                        element = (NodeElement) node;
                                        children = node.parent.getChildElements(element.name);
                                        if (children.size() == 1) {
                                            result.add(element);
                                        }
                                    }
                                } else if ("empty".equals(selector) && node instanceof NodeElement) {
                                    element = (NodeElement) node;
                                    if (element.children == null || element.children.isEmpty()) {
                                        result.add(element);
                                    }
                                }
                            }
                        }
                    }

                    return result;
                } while (!(node instanceof NodeElement));

                for (element = (NodeElement) node; element.parent != null && element.parent instanceof NodeElement; element = (NodeElement) element.parent) {
                }

                result.add(element);
            }
        }

        private static ArrayList<NodeElement> filterNextElementsByTagName(ArrayList<Node> context, String tagName, boolean immediately) {
            boolean addAll = "*".equals(tagName);
            ArrayList<NodeElement> result = new ArrayList();
            Iterator var5 = context.iterator();

            while (true) {
                while (true) {
                    Node node;
                    do {
                        if (!var5.hasNext()) {
                            return result;
                        }

                        node = (Node) var5.next();
                    } while (node.parent == null);

                    boolean getNode = false;

                    for (int i = 0; i < node.parent.children.size(); ++i) {
                        if (getNode) {
                            Node child = (Node) node.parent.children.get(i);
                            if (child instanceof NodeElement) {
                                NodeElement element = (NodeElement) child;
                                if (addAll || tagName.equals(element.name)) {
                                    result.add(element);
                                    break;
                                }

                                if (immediately) {
                                    break;
                                }
                            }
                        }

                        if (!getNode && ((Node) node.parent.children.get(i)).equals(node)) {
                            getNode = true;
                        }
                    }
                }
            }
        }

        public Node getParent() {
            return this.parent;
        }

        public Node setParent(Node newParent) {
            Node oldParent = this.parent;
            if (oldParent != null) {
                oldParent.removeChild(this);
            }

            if (newParent != null) {
                newParent.addChild(this);
            } else {
                this.parent = null;
            }

            return oldParent;
        }

        public ArrayList<Node> getChildren() {
            return this.children;
        }

        public ArrayList<NodeElement> getChildElements() {
            if (this.children == null) {
                return null;
            } else {
                ArrayList<NodeElement> result = new ArrayList();

                for (int i = 0; i < this.children.size(); ++i) {
                    Node node = (Node) this.children.get(i);
                    if (node instanceof NodeElement) {
                        result.add((NodeElement) node);
                    }
                }

                return result;
            }
        }

        public ArrayList<NodeElement> getChildElements(String tagName) {
            if (this.children == null) {
                return null;
            } else {
                ArrayList<NodeElement> result = new ArrayList();

                for (int i = 0; i < this.children.size(); ++i) {
                    Node node = (Node) this.children.get(i);
                    if (node instanceof NodeElement) {
                        NodeElement element = (NodeElement) node;
                        if (element.name.equals(tagName)) {
                            result.add(element);
                        }
                    }
                }

                return result;
            }
        }

        public Node getChild(int index) {
            return this.children != null && this.children.size() >= index ? (Node) this.children.get(index) : null;
        }

        public NodeElement getChildElement(int index) {
            if (this.children != null) {
                int count = 0;

                for (int i = 0; i < this.children.size(); ++i) {
                    if (this.children.get(i) instanceof NodeElement) {
                        if (count == index) {
                            return (NodeElement) this.children.get(i);
                        }

                        ++count;
                    }
                }
            }

            return null;
        }

        public int indexOfChild(Node child) {
            if (this.children != null && child != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    if (((Node) this.children.get(i)).equals(child)) {
                        return i;
                    }
                }
            }

            return -1;
        }

        public int indexOfChildElement(NodeElement child) {
            if (this.children != null && child != null) {
                int index = 0;

                for (int i = 0; i < this.children.size(); ++i) {
                    Node node = (Node) this.children.get(i);
                    if (node instanceof NodeElement) {
                        if (node.equals(child)) {
                            return index;
                        }

                        ++index;
                    }
                }
            }

            return -1;
        }

        public Node addChild(Node child) {
            if (this.children == null) {
                this.children = new ArrayList();
            }

            child.parent = this;
            this.children.add(child);
            return this;
        }

        public Node addChild(Node child, int index) {
            if (this.children == null) {
                this.children = new ArrayList();
            }

            child.parent = this;
            if (index >= 0 && index <= this.children.size()) {
                this.children.add(index, child);
            } else {
                this.children.add(child);
            }

            return this;
        }

        public boolean removeChild(Node child) {
            if (this.children != null && child != null) {
                for (int i = 0; i < this.children.size(); ++i) {
                    if (child.equals(this.children.get(i))) {
                        this.children.remove(i);
                        return true;
                    }
                }
            }

            return false;
        }

        public String inner() {
            if (this.children == null) {
                return "";
            } else {
                StringBuilder sb = new StringBuilder();
                Iterator var2 = this.children.iterator();

                while (var2.hasNext()) {
                    Node child = (Node) var2.next();
                    sb.append(child.toString());
                }

                return sb.toString();
            }
        }

        public boolean inner(String document) {
            return this.inner(document, false);
        }

        public boolean inner(String document, boolean xmlmode) {
            Node root = DomParser.build(document, xmlmode);
            if (root.children == null) {
                return false;
            } else {
                this.children.clear();
                Iterator var4 = root.children.iterator();

                while (var4.hasNext()) {
                    Node child = (Node) var4.next();
                    this.addChild(child);
                }

                return true;
            }
        }

        public String text() {
            return this.text(this);
        }

        private String text(Node context) {
            String rtn;
            if (context instanceof NodeText) {
                rtn = ((NodeText) context).text;
            } else if (context.children != null) {
                StringBuilder sb = new StringBuilder();
                Iterator var4 = context.children.iterator();

                while (var4.hasNext()) {
                    Node node = (Node) var4.next();
                    sb.append(this.text(node));
                }

                rtn = sb.toString();
            } else {
                rtn = "";
            }

            return rtn == null ? "" : rtn.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"");
        }

        public String toString() {
            return this.toString(false, "  ", "\n", 0);
        }

        public String toString(boolean format) {
            return this.toString(format, "  ", "\n", 0);
        }

        public String toString(boolean format, String space) {
            return this.toString(format, space, "\n", 0);
        }

        public String toString(boolean format, String space, String line) {
            return this.toString(format, space, line, 0);
        }

        public String toString(boolean format, String space, String line, int levelref) {
            if (this.children == null) {
                return "";
            } else {
                StringBuilder sb = new StringBuilder();
                if (format) {
                    int childLevel = this.getLevel() + levelref + 1;
                    int count = 0;

                    for (Iterator var8 = this.children.iterator(); var8.hasNext(); ++count) {
                        Node child = (Node) var8.next();
                        if (count > 0) {
                            sb.append(line);
                        }

                        for (int i = 0; i < childLevel; ++i) {
                            sb.append(space);
                        }

                        sb.append(child.toString(format, space, line, levelref));
                    }
                } else {
                    Iterator var11 = this.children.iterator();

                    while (var11.hasNext()) {
                        Node child = (Node) var11.next();
                        sb.append(child.toString(format, space, line, levelref));
                    }
                }

                return sb.toString();
            }
        }

        public ArrayList<Node> getNodesByFilter(Filter filter) {
            ArrayList<Node> results = new ArrayList();
            findNodesByFilter(results, filter, this);
            return results;
        }

        public ArrayList<Node> getChildNodesByFilter(Filter filter) {
            ArrayList<Node> results = new ArrayList();
            findChildNodesByFilter(results, filter, this);
            return results;
        }

        public ArrayList<NodeElement> getElementsByFilter(final FilterElement filter) {
            ArrayList<Node> matches = this.getNodesByFilter(new Filter() {
                public boolean match(Node node) {
                    return node instanceof NodeElement ? filter.match((NodeElement) node) : false;
                }
            });
            ArrayList<NodeElement> result = new ArrayList();
            Iterator var5 = matches.iterator();

            while (var5.hasNext()) {
                Node node = (Node) var5.next();
                if (node instanceof NodeElement) {
                    result.add((NodeElement) node);
                }
            }

            return result;
        }

        public ArrayList<NodeElement> getChildElementsByFilter(final FilterElement filter) {
            ArrayList<Node> matches = this.getChildNodesByFilter(new Filter() {
                public boolean match(Node node) {
                    return node instanceof NodeElement ? filter.match((NodeElement) node) : false;
                }
            });
            ArrayList<NodeElement> result = new ArrayList();
            Iterator var5 = matches.iterator();

            while (var5.hasNext()) {
                Node node = (Node) var5.next();
                if (node instanceof NodeElement) {
                    result.add((NodeElement) node);
                }
            }

            return result;
        }

        public ArrayList<NodeElement> getElementById(final String id) {
            return this.getElementsByFilter(new FilterElement() {
                public boolean match(NodeElement node) {
                    return DomParser.strEqual(id, node.getAttrValue("id"), false);
                }
            });
        }

        public ArrayList<NodeElement> getElementsByClassName(final String className) {
            return this.getElementsByFilter(new FilterElement() {
                public boolean match(NodeElement node) {
                    String elementClassName = node.getAttrValue("class");
                    if (elementClassName != null) {
                        String[] classes = elementClassName.split("\\s");
                        String[] var4 = classes;
                        int var5 = classes.length;

                        for (int var6 = 0; var6 < var5; ++var6) {
                            String cn = var4[var6];
                            if (DomParser.strEqual(className, cn, false)) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            });
        }

        public ArrayList<NodeElement> getElementsByTagName(String tagName) {
            return this.getElementsByTagName(tagName, false);
        }

        public ArrayList<NodeElement> getElementsByTagName(final String tagName, final boolean xmlmode) {
            final boolean addAll = "*".equals(tagName);
            return this.getElementsByFilter(new FilterElement() {
                public boolean match(NodeElement node) {
                    return addAll || DomParser.strEqual(tagName, node.name, !xmlmode);
                }
            });
        }

        public ArrayList<NodeElement> getElementsByTagAttr(final String tagAttrName, final String tagAttrValue, final boolean xmlmode) {
            return this.getElementsByFilter(new FilterElement() {
                public boolean match(NodeElement node) {
                    return DomParser.strEqual(tagAttrValue, node.getAttrValue(tagAttrName, !xmlmode), false);
                }
            });
        }

        public ArrayList<NodeElement> search(String cssSelector) {
            ArrayList<Node> context = new ArrayList();
            ArrayList<NodeElement> result = null;
            context.add(this);
            if (cssSelector != null && !cssSelector.isEmpty()) {
                ArrayList<String> words = getSelectorWordList(cssSelector);

                for (int index = 0; index < words.size(); ++index) {
                    String word = (String) words.get(index);
                    if (!"#".equals(word) && !".".equals(word) && !">".equals(word) && !"+".equals(word) && !"~".equals(word)) {
                        if (":".equals(word)) {
                            if (index + 1 < words.size()) {
                                result = searchElement(context, word.charAt(0), (String) words.get(index + 1));
                                ++index;
                            } else {
                                result = null;
                            }
                        } else if (!"[".equals(word)) {
                            result = searchElement(context, ' ', word);
                        } else {
                            int endIndex;
                            for (endIndex = index + 1; endIndex < words.size() && !"]".equals(words.get(endIndex)); ++endIndex) {
                            }

                            if (endIndex < words.size()) {
                                int size = endIndex - index;
                                switch (size) {
                                    case 2:
                                        result = searchElement(context, '[', (String) words.get(index + 1));
                                        break;
                                    case 3:
                                    default:
                                        result = null;
                                        break;
                                    case 4:
                                        result = searchElement(context, '[', (String) words.get(index + 1), ' ', (String) words.get(index + 3));
                                        break;
                                    case 5:
                                        result = searchElement(context, '[', (String) words.get(index + 1), ((String) words.get(index + 2)).charAt(0), (String) words.get(index + 4));
                                }

                                index = endIndex + 1;
                            } else {
                                result = null;
                            }
                        }
                    } else if (index + 1 < words.size()) {
                        result = searchElement(context, word.charAt(0), (String) words.get(index + 1));
                        ++index;
                    } else {
                        result = null;
                    }

                    if (result != null) {
                        context = new ArrayList();
                        Iterator var9 = result.iterator();

                        while (var9.hasNext()) {
                            NodeElement elm = (NodeElement) var9.next();
                            context.add(elm);
                        }
                    }
                }

                return result;
            } else {
                return new ArrayList();
            }
        }

        protected int getLevel() {
            int level = 0;

            for (Node node = this.parent; node != null; node = node.parent) {
                ++level;
            }

            return level;
        }

        public interface FilterElement {
            boolean match(NodeElement var1);
        }

        public interface Filter {
            boolean match(Node var1);
        }
    }
}
