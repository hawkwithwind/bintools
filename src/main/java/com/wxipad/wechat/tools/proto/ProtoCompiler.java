//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.proto;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProtoCompiler {
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final short WORD_OTHER = 0;
    private static final short WORD_NUMBER = 1;
    private static final short WORD_PLAIN = 2;
    private static final short WORD_STRING = 3;
    private static final String KEYWORD_SYNTAX = "syntax";
    private static final String KEYWORD_PACKAGE = "package";
    private static final String KEYWORD_IMPORT = "import";
    private static final String KEYWORD_RESERVED = "reserved";
    private static final String KEYWORD_MESSAGE = "message";
    private static final String KEYWORD_ENUM = "enum";
    private static final String KEYWORD_REQUIRED = "required";
    private static final String KEYWORD_OPTIONAL = "optional";
    private static final String KEYWORD_REPEATED = "repeated";
    private static final String KEYWORD_PACKED = "packed";
    private static final String KEYWORD_DEFAULT = "default";
    private static final String TYPE_INT32 = "int32";
    private static final String TYPE_INT64 = "int64";
    private static final String TYPE_UINT32 = "uint32";
    private static final String TYPE_UINT64 = "uint64";
    private static final String TYPE_SINT32 = "sint32";
    private static final String TYPE_SINT64 = "sint64";
    private static final String TYPE_FIXED64 = "fixed64";
    private static final String TYPE_SFIXED64 = "sfixed64";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_FIXED32 = "fixed32";
    private static final String TYPE_SFIXED32 = "sfixed32";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_BOOL = "bool";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_BYTES = "bytes";
    private static final String EXT_PROTO = ".proto";
    private static final String EXT_JAVA = ".java";
    private static final String DEFAULT_SPACE = "    ";
    private static final String DEFAULT_LINE = "\n";
    private static final String[] JAVA_KEYWORDS = new String[]{"private", "protected", "public", "abstract", "class", "extends", "final", "implements", "interface", "native", "new", "static", "strictfp", "synchronized", "transient", "volatile", "break", "continue", "return", "do", "while", "if", "else", "for", "instanceof", "switch", "case", "default", "catch", "finally", "throw", "throws", "try", "import", "package", "boolean", "byte", "char", "double", "float", "int", "long", "short", "null", "true", "false", "super", "this", "void", "const", "goto"};
    private final ArrayList<ProtoFile> files = new ArrayList();
    private final AtomicBoolean echo = new AtomicBoolean(false);
    private final AtomicReference<Charset> charset;
    private final AtomicReference<String> space;
    private final AtomicReference<String> line;

    public ProtoCompiler() {
        this.charset = new AtomicReference(DEFAULT_CHARSET);
        this.space = new AtomicReference("    ");
        this.line = new AtomicReference("\n");
    }

    private void input(File input) throws ProtoException {
        if (input.exists()) {
            if (!input.isHidden()) {
                if (input.isDirectory()) {
                    File[] var2 = input.listFiles();
                    int var3 = var2.length;

                    for (int var4 = 0; var4 < var3; ++var4) {
                        File child = var2[var4];
                        this.input(child);
                    }
                } else {
                    String name = input.getName();
                    if (!name.toLowerCase().endsWith(".proto")) {
                        return;
                    }

                    ProtoFile file = new ProtoFile();
                    file.name = input.getName();
                    file.input(input);
                    this.files.add(file);
                }

            }
        }
    }

    public void input(String name, String content) throws ProtoException {
        ProtoFile file = new ProtoFile();
        file.name = name;
        file.input(content);
        this.files.add(file);
    }

    private void output(File output) throws ProtoException {
        if (output.exists()) {
            if (!output.isDirectory()) {
                throw new ProtoException("output file not directory");
            }
        } else if (!output.mkdirs()) {
            throw new ProtoException("create output directory failed");
        }

        Iterator var2 = this.files.iterator();

        while (var2.hasNext()) {
            ProtoFile file = (ProtoFile) var2.next();
            String path = file.name;
            if (!path.toLowerCase().endsWith(".proto")) {
                return;
            }

            path = path.substring(0, path.length() - ".proto".length());
            path = output.getPath() + File.separator + path + ".java";
            file.output(new File(path));
        }

    }

    public String output() throws ProtoException {
        return !this.files.isEmpty() ? ((ProtoFile) this.files.get(0)).output() : null;
    }

    private String read(File file) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileInputStream in = new FileInputStream(file);

            byte[] buff;
            try {
                buff = new byte[1024];

                int read;
                while ((read = in.read(buff, 0, buff.length)) != -1) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
            }

            buff = out.toByteArray();
            return new String(buff, (Charset) this.charset.get());
        } catch (IOException var10) {
            var10.printStackTrace();
            return null;
        }
    }

    private boolean write(File file, String content) {
        try {
            if (file.exists() && !file.delete()) {
                return false;
            } else {
                FileOutputStream out = new FileOutputStream(file);

                try {
                    if (content != null) {
                        out.write(content.getBytes((Charset) this.charset.get()));
                        out.flush();
                    }
                } finally {
                    out.close();
                }

                return true;
            }
        } catch (IOException var8) {
            var8.printStackTrace();
            return false;
        }
    }

    public ProtoCompiler compile(String input, String output) throws ProtoException {
        return this.compile(new File(input), new File(output));
    }

    public ProtoCompiler compile(File input, File output) throws ProtoException {
        this.input(input);
        this.output(output);
        return this;
    }

    public ProtoCompiler charset(Charset newCharset) {
        if (newCharset != null) {
            this.charset.set(newCharset);
        }

        return this;
    }

    public ProtoCompiler space(String newSpace) {
        if (newSpace != null) {
            this.space.set(newSpace);
        }

        return this;
    }

    public ProtoCompiler line(String newLine) {
        if (newLine != null) {
            this.line.set(newLine);
        }

        return this;
    }

    public ProtoCompiler reset() {
        this.files.clear();
        return this;
    }

    public ProtoCompiler echo(boolean enable) {
        this.echo.set(enable);
        return this;
    }

    private ProtoCompiler echo(String... msgs) {
        if (this.echo.get()) {
            String[] var2 = msgs;
            int var3 = msgs.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String msg = var2[var4];
                System.out.println(msg);
            }
        }

        return this;
    }

    private class ProtoEnumPair {
        public String name;
        public int value;

        private ProtoEnumPair() {
            this.name = null;
            this.value = 0;
        }
    }

    private class ProtoEnum extends ProtoNode {
        public ArrayList<ProtoEnumPair> pairs;

        private ProtoEnum() {
            super();
            this.pairs = new ArrayList();
        }
    }

    private class ProtoMessageField {
        public int num;
        public String type;
        public String name;
        public boolean repeated;
        public boolean required;
        public boolean optional;
        public boolean packed;
        public String defval;

        private ProtoMessageField() {
            this.num = 0;
            this.type = null;
            this.name = null;
            this.repeated = false;
            this.required = false;
            this.optional = false;
            this.packed = false;
            this.defval = null;
        }
    }

    private class ProtoMessage extends ProtoNode {
        public ArrayList<ProtoMessageField> fields;

        private ProtoMessage() {
            super();
            this.fields = new ArrayList();
        }
    }

    private class ProtoFile extends ProtoNode {
        public String syntax;
        public String pkgname;
        public ArrayList<String> imports;

        private ProtoFile() {
            super();
            this.syntax = null;
            this.pkgname = null;
            this.imports = new ArrayList();
        }

        public void input(File file) throws ProtoException {
            ProtoCompiler.this.echo("find input file [" + file.getName() + "] ...");
            String content = ProtoCompiler.this.read(file);
            if (content != null) {
                this.input(content);
            } else {
                throw new ProtoException("file content read error");
            }
        }

        public void input(String content) throws ProtoException {
            ProtoContext context = ProtoCompiler.this.new ProtoContext();
            context.solve(this, content);
        }

        public void output(File file) throws ProtoException {
            ProtoCompiler.this.echo("do file [" + file.getName() + "] output...");
            ProtoCompiler.this.write(file, this.output());
        }

        public String output() throws ProtoException {
            String className = this.name;
            if (className != null && className.toLowerCase().endsWith(".proto")) {
                className = className.substring(0, className.length() - ".proto".length());
            }

            StringBuilder sb = new StringBuilder();
            if (this.pkgname != null) {
                this.println(sb, 0, "package " + this.pkgname + ";");
            }

            this.println(sb);
            this.println(sb, 0, "import com.modnut.framework2.proto.*;");
            Iterator var3 = this.imports.iterator();

            while (var3.hasNext()) {
                String importFile = (String) var3.next();
                if (!importFile.toLowerCase().endsWith(".proto")) {
                    throw new ProtoException("import \"" + importFile + "\" is not .proto file");
                }

                String importClass = importFile.substring(0, importFile.length() - ".proto".length());
                String importPrefix = this.pkgname != null ? this.pkgname + "." : "";
                this.println(sb, 0, "import " + importPrefix + importClass + ".*;");
            }

            this.println(sb);
            this.println(sb, 0, "// Generated by the protocol buffer compiler.  DO NOT EDIT!");
            this.println(sb, 0, "// source: " + this.name);
            this.println(sb, 0, "public class " + className + " {");
            this.println(sb);
            this.output(sb, 1, (ArrayList) this.children);
            this.println(sb, 0, "}");
            this.println(sb);
            return sb.toString();
        }

        private void output(StringBuilder sb, int tabs, ArrayList<ProtoNode> nodes) throws ProtoException {
            Iterator var4 = nodes.iterator();

            while (var4.hasNext()) {
                ProtoNode node = (ProtoNode) var4.next();
                if (node instanceof ProtoEnum) {
                    this.output(sb, tabs, (ProtoEnum) node);
                } else {
                    if (!(node instanceof ProtoMessage)) {
                        throw new ProtoException("unknown inner proto node type");
                    }

                    this.output(sb, tabs, (ProtoMessage) node);
                }
            }

        }

        private void output(StringBuilder sb, int tabs, ProtoEnum node) throws ProtoException {
            this.println(sb, tabs, "public static class " + node.name + " extends ProtoEnum {");
            this.println(sb);
            this.output(sb, tabs + 1, node.children);
            this.outputConstant(sb, tabs + 1, node);
            this.outputCheck(sb, tabs + 1, node);
            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void outputConstant(StringBuilder sb, int tabs, ProtoEnum node) throws ProtoException {
            Iterator var4 = node.pairs.iterator();

            while (var4.hasNext()) {
                ProtoEnumPair pair = (ProtoEnumPair) var4.next();
                this.println(sb, tabs, "public static final int " + pair.name + " = " + pair.value + ";");
            }

            if (!node.pairs.isEmpty()) {
                this.println(sb);
            }

        }

        private void outputCheck(StringBuilder sb, int tabs, ProtoEnum node) throws ProtoException {
            this.println(sb, tabs, "public static boolean check(int _value_) {");
            if (node.pairs.isEmpty()) {
                this.println(sb, tabs + 1, "return true;");
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("return ProtoData.checkContain(_value_");
                Iterator var5 = node.pairs.iterator();

                while (var5.hasNext()) {
                    ProtoEnumPair pair = (ProtoEnumPair) var5.next();
                    sb2.append(", ").append(pair.name);
                }

                sb2.append(");");
                this.println(sb, tabs + 1, sb2.toString());
            }

            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void output(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            this.println(sb, tabs, "public static class " + node.name + " extends ProtoMessage {");
            this.println(sb);
            this.output(sb, tabs + 1, node.children);
            this.outputDeclaration(sb, tabs + 1, node);
            this.outputPacked(sb, tabs + 1, node);
            this.outputWrite(sb, tabs + 1, node);
            this.outputRead(sb, tabs + 1, node);
            this.outputCheck(sb, tabs + 1, node);
            this.outputParse(sb, tabs + 1, node);
            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void outputDeclaration(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            String fieldType;
            String fieldName;
            String fieldValue;
            String fieldComment;
            for (Iterator var4 = node.fields.iterator(); var4.hasNext(); this.println(sb, tabs, "public " + fieldType + " " + fieldName + " = " + fieldValue + ";" + fieldComment)) {
                ProtoMessageField field = (ProtoMessageField) var4.next();
                fieldType = field.type;
                fieldName = field.name;
                fieldValue = "null";
                fieldComment = "//" + (field.required ? "required" : "optional") + "(" + field.num + ")--" + field.type;
                if (this.isBasicType(field.type)) {
                    fieldType = this.getBasicTypeCode1(field.type);
                } else if (this.getTypeNode(field.type) instanceof ProtoEnum) {
                    fieldType = "Integer";
                }

                if (field.defval != null) {
                    if (this.isBasicType(field.type)) {
                        if ("bool".equals(field.type)) {
                            fieldValue = field.defval.equalsIgnoreCase("true") ? "true" : "false";
                        } else if ("string".equals(field.type)) {
                            fieldValue = "\"" + field.defval + "\"";
                        } else if ("bytes".equals(field.type)) {
                            this.println(sb, tabs, node.name + "." + field.name + " not supported default value");
                        } else {
                            fieldValue = field.defval;
                        }
                    } else {
                        this.println(sb, tabs, node.name + "." + field.name + " not supported default value");
                    }
                }

                if (field.repeated) {
                    fieldType = "ProtoList<" + fieldType + ">";
                    fieldValue = "new " + fieldType + "(" + (field.packed ? "true" : "false") + ")";
                    fieldType = "final " + fieldType;
                }
            }

            if (!node.fields.isEmpty()) {
                this.println(sb);
            }

        }

        private void outputPacked(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            ArrayList<Integer> packedNums = new ArrayList();
            Iterator var5 = node.fields.iterator();

            while (var5.hasNext()) {
                ProtoMessageField field = (ProtoMessageField) var5.next();
                if (field.repeated && field.packed) {
                    packedNums.add(field.num);
                }
            }

            if (!packedNums.isEmpty()) {
                this.println(sb, tabs, "@Override");
                this.println(sb, tabs, "public boolean packed(int _field_) throws ProtoException {");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("return ProtoData.checkContain(_field_");
                Iterator var9 = packedNums.iterator();

                while (var9.hasNext()) {
                    Integer packedNum = (Integer) var9.next();
                    sb2.append(", ").append(packedNum);
                }

                sb2.append(");");
                this.println(sb, tabs + 1, sb2.toString());
                this.println(sb, tabs, "}");
                this.println(sb);
            }

        }

        private void outputWrite(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            this.println(sb, tabs, "@Override");
            this.println(sb, tabs, "public void write(ProtoWriter _writer_) {");
            int codeTabs = 1;
            Iterator var5 = node.fields.iterator();

            while (var5.hasNext()) {
                ProtoMessageField field = (ProtoMessageField) var5.next();
                if (!field.required) {
                    String codeAddon = "";
                    if (field.repeated) {
                        codeAddon = " && !" + field.name + ".isEmpty()";
                    }

                    this.println(sb, tabs + codeTabs, "if (" + field.name + " != null" + codeAddon + ") {");
                    ++codeTabs;
                }

                if (this.isBasicType(field.type)) {
                    this.println(sb, tabs + codeTabs, "_writer_.write" + this.getBasicTypeCode2(field.type) + "(" + field.num + ", " + field.name + ");");
                } else {
                    ProtoNode typeNode = this.getTypeNode(field.type);
                    if (typeNode == null) {
                        throw new ProtoException(node.name + "." + field.name + " type is missing");
                    }

                    if (typeNode instanceof ProtoEnum) {
                        this.println(sb, tabs + codeTabs, "_writer_.writeEnum(" + field.num + ", " + field.name + ");");
                    } else if (typeNode instanceof ProtoMessage) {
                        this.println(sb, tabs + codeTabs, "_writer_.writeMessage(" + field.num + ", " + field.name + ");");
                    }
                }

                if (!field.required) {
                    --codeTabs;
                    this.println(sb, tabs + codeTabs, "}");
                }
            }

            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void outputRead(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            this.println(sb, tabs, "@Override");
            this.println(sb, tabs, "public void read(ProtoReader _reader_, int _field_, int _type_) throws ProtoException {");
            int fieldCount = 0;
            int codeTabs = 1;

            for (Iterator var6 = node.fields.iterator(); var6.hasNext(); ++fieldCount) {
                ProtoMessageField field = (ProtoMessageField) var6.next();
                if (fieldCount > 0) {
                    this.println(sb, tabs + codeTabs, "} else if (_field_ == " + field.num + ") {");
                } else {
                    this.println(sb, tabs + codeTabs, "if (_field_ == " + field.num + ") {");
                }

                ++codeTabs;
                if (this.isBasicType(field.type)) {
                    if (field.repeated) {
                        this.println(sb, tabs + codeTabs, field.name + ".add(_reader_.read" + this.getBasicTypeCode2(field.type) + "());");
                    } else {
                        this.println(sb, tabs + codeTabs, field.name + " = _reader_.read" + this.getBasicTypeCode2(field.type) + "();");
                    }
                } else {
                    ProtoNode typeNode = this.getTypeNode(field.type);
                    if (typeNode == null) {
                        throw new ProtoException(node.name + "." + field.name + " type is missing");
                    }

                    if (typeNode instanceof ProtoEnum) {
                        if (field.repeated) {
                            this.println(sb, tabs + codeTabs, field.name + ".add(_reader_.readEnum());");
                        } else {
                            this.println(sb, tabs + codeTabs, field.name + " = _reader_.readEnum();");
                        }
                    } else if (typeNode instanceof ProtoMessage) {
                        if (field.repeated) {
                            this.println(sb, tabs + codeTabs, field.name + ".add(" + field.type + ".parse(_reader_.readByteArray()));");
                        } else {
                            this.println(sb, tabs + codeTabs, field.name + " = " + field.type + ".parse(_reader_.readByteArray());");
                        }
                    }
                }

                --codeTabs;
            }

            if (fieldCount > 0) {
                this.println(sb, tabs + codeTabs, "} else {");
                ++codeTabs;
                this.println(sb, tabs + codeTabs, "unknown(_reader_, _field_, _type_);");
                --codeTabs;
                this.println(sb, tabs + codeTabs, "}");
            } else {
                this.println(sb, tabs + codeTabs, "unknown(_reader_, _field_, _type_);");
            }

            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void outputCheck(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            this.println(sb, tabs, "public boolean check() {");
            ArrayList<ProtoMessageField> enumFields = new ArrayList();
            ArrayList<String> requiredNames1 = new ArrayList();
            ArrayList<String> requiredNames2 = new ArrayList();
            Iterator var7 = node.fields.iterator();

            ProtoMessageField field;
            while (var7.hasNext()) {
                field = (ProtoMessageField) var7.next();
                if (!this.isBasicType(field.type)) {
                    ProtoNode typeNode = this.getTypeNode(field.type);
                    if (typeNode == null) {
                        throw new ProtoException(node.name + "." + field.name + " type is missing");
                    }

                    if (typeNode instanceof ProtoEnum) {
                        enumFields.add(field);
                    }
                }

                if (field.required) {
                    if (field.repeated) {
                        requiredNames1.add(field.name);
                    } else {
                        requiredNames2.add(field.name);
                    }
                }
            }

            if (requiredNames2.isEmpty()) {
                this.println(sb, tabs + 1, "return true;");
            } else {
                var7 = enumFields.iterator();

                while (var7.hasNext()) {
                    field = (ProtoMessageField) var7.next();
                    this.println(sb, tabs + 1, "if (" + field.name + " != null) {");
                    this.println(sb, tabs + 2, field.type + ".check(" + field.name + ");");
                    this.println(sb, tabs + 1, "}");
                }

                var7 = requiredNames1.iterator();

                while (var7.hasNext()) {
                    String requiredName = (String) var7.next();
                    this.println(sb, tabs + 1, "if (" + requiredName + ".isEmpty()) {");
                    this.println(sb, tabs + 2, "return false;");
                    this.println(sb, tabs + 1, "}");
                }

                StringBuilder sb2 = new StringBuilder();
                sb2.append("return ProtoData.checkNotNull(");

                for (int i = 0; i < requiredNames2.size(); ++i) {
                    if (i > 0) {
                        sb2.append(", ");
                    }

                    sb2.append((String) requiredNames2.get(i));
                }

                sb2.append(");");
                this.println(sb, tabs + 1, sb2.toString());
            }

            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void outputParse(StringBuilder sb, int tabs, ProtoMessage node) throws ProtoException {
            this.println(sb, tabs, "public static " + node.name + " parse(byte[] data) {");
            this.println(sb, tabs + 1, "try {");
            this.println(sb, tabs + 2, node.name + " obj = new " + node.name + "();");
            this.println(sb, tabs + 2, "new ProtoReader(data).solve(obj);");
            this.println(sb, tabs + 2, "return obj;");
            this.println(sb, tabs + 1, "} catch (ProtoException e) {");
            this.println(sb, tabs + 2, "return null;");
            this.println(sb, tabs + 1, "}");
            this.println(sb, tabs, "}");
            this.println(sb);
        }

        private void println(StringBuilder sb) {
            sb.append((String) ProtoCompiler.this.line.get());
        }

        private void println(StringBuilder sb, int tabs, String text) {
            for (int i = 0; i < tabs; ++i) {
                sb.append((String) ProtoCompiler.this.space.get());
            }

            if (text != null) {
                sb.append(text);
            }

            sb.append((String) ProtoCompiler.this.line.get());
        }

        private boolean isBasicType(String type) {
            return "int32".equals(type) || "uint32".equals(type) || "sint32".equals(type) || "int64".equals(type) || "uint64".equals(type) || "sint64".equals(type) || "fixed64".equals(type) || "sfixed64".equals(type) || "double".equals(type) || "fixed32".equals(type) || "sfixed32".equals(type) || "float".equals(type) || "bool".equals(type) || "string".equals(type) || "bytes".equals(type);
        }

        private String getBasicTypeCode1(String type) throws ProtoException {
            if (!"int32".equals(type) && !"uint32".equals(type) && !"sint32".equals(type) && !"fixed32".equals(type) && !"sfixed32".equals(type)) {
                if (!"int64".equals(type) && !"uint64".equals(type) && !"sint64".equals(type) && !"fixed64".equals(type) && !"sfixed64".equals(type)) {
                    if ("float".equals(type)) {
                        return "Float";
                    } else if ("double".equals(type)) {
                        return "Double";
                    } else if ("bool".equals(type)) {
                        return "Boolean";
                    } else if ("string".equals(type)) {
                        return "String";
                    } else if ("bytes".equals(type)) {
                        return "byte[]";
                    } else {
                        throw new ProtoException("unknown basic proto field type");
                    }
                } else {
                    return "Long";
                }
            } else {
                return "Integer";
            }
        }

        private String getBasicTypeCode2(String type) throws ProtoException {
            if ("int32".equals(type)) {
                return "Int32";
            } else if ("uint32".equals(type)) {
                return "UInt32";
            } else if ("sint32".equals(type)) {
                return "SInt32";
            } else if ("int64".equals(type)) {
                return "Int64";
            } else if ("uint64".equals(type)) {
                return "UInt64";
            } else if ("sint64".equals(type)) {
                return "SInt64";
            } else if ("fixed64".equals(type)) {
                return "Fixed64";
            } else if ("sfixed64".equals(type)) {
                return "SFixed64";
            } else if ("double".equals(type)) {
                return "Double";
            } else if ("fixed32".equals(type)) {
                return "Fixed32";
            } else if ("sfixed32".equals(type)) {
                return "SFixed32";
            } else if ("float".equals(type)) {
                return "Float";
            } else if ("bool".equals(type)) {
                return "Bool";
            } else if ("string".equals(type)) {
                return "String";
            } else if ("bytes".equals(type)) {
                return "ByteArray";
            } else {
                throw new ProtoException("unknown basic proto field type");
            }
        }

        private ProtoNode getTypeNode(String type) {
            ProtoNode result = this.getTypeNode(this, type, 0);
            if (result == null) {
                Iterator var3 = ProtoCompiler.this.files.iterator();

                while (var3.hasNext()) {
                    ProtoFile file = (ProtoFile) var3.next();
                    if (!this.equals(file)) {
                        result = this.getTypeNode(file, type, 0);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }

            return result;
        }

        private ProtoNode getTypeNode(ProtoNode node, String type, int level) {
            if (level > 0 && type != null && type.equals(node.name)) {
                return node;
            } else {
                Iterator var4 = node.children.iterator();

                ProtoNode result;
                do {
                    if (!var4.hasNext()) {
                        return null;
                    }

                    ProtoNode child = (ProtoNode) var4.next();
                    result = this.getTypeNode(child, type, level + 1);
                } while (result == null);

                return result;
            }
        }
    }

    private class ProtoNode {
        public String name;
        public ProtoNode parent;
        public ArrayList<ProtoNode> children;

        private ProtoNode() {
            this.name = null;
            this.parent = null;
            this.children = new ArrayList();
        }
    }

    private class ProtoContext {
        public final AtomicInteger lineCount;
        public final AtomicInteger lineIndex;
        public final ArrayList<ProtoWord> list;
        public final LinkedList<ProtoNode> stack;

        private ProtoContext() {
            this.lineCount = new AtomicInteger(0);
            this.lineIndex = new AtomicInteger(0);
            this.list = new ArrayList();
            this.stack = new LinkedList();
        }

        public void solve(ProtoFile file, String content) throws ProtoException {
            this.stack.addLast(file);
            this.solveContent(content);
            this.solveWord();
        }

        private void solveContent(String content) {
            int index = 0;

            while (true) {
                while (index < content.length()) {
                    char ch = content.charAt(index);
                    if (this.isNumber(ch)) {
                        index = this.solveNumber(content, index);
                    } else if (!this.isLetter(ch) && !this.isSymbol(ch, "_")) {
                        if (this.isQuote(ch)) {
                            index = this.solveString(content, index);
                        } else {
                            char ch2;
                            if (ch == '/') {
                                if (index + 1 < content.length()) {
                                    ch2 = content.charAt(index + 1);
                                    if (ch2 == '/') {
                                        index = this.skipComment1(content, index);
                                        continue;
                                    }

                                    if (ch2 == '*') {
                                        index = this.skipComment2(content, index);
                                        continue;
                                    }
                                }

                                this.appendWord((short) 0, Character.toString(ch), index);
                                ++index;
                            } else if (!this.isSpace(ch)) {
                                this.appendWord((short) 0, Character.toString(ch), index);
                                ++index;
                            } else if (ch != '\r' && ch != '\n') {
                                ++index;
                            } else {
                                if (index + 1 < content.length()) {
                                    ch2 = content.charAt(index + 1);
                                    if (ch == '\r' && ch2 == '\n') {
                                        ++index;
                                    }
                                }

                                ++index;
                                this.lineCount.incrementAndGet();
                                this.lineIndex.set(index);
                            }
                        }
                    } else {
                        index = this.solvePlain(content, index);
                    }
                }

                return;
            }
        }

        private int solveNumber(String content, int index) {
            boolean dot = false;

            for (int i = index + 1; i < content.length(); ++i) {
                char ch = content.charAt(i);
                if (ch == '.') {
                    if (dot) {
                        this.appendWord((short) 1, content.substring(index, i), index);
                        return i;
                    }

                    dot = true;
                } else if (!this.isNumber(ch)) {
                    this.appendWord((short) 1, content.substring(index, i), index);
                    return i;
                }
            }

            this.appendWord((short) 1, content.substring(index), index);
            return content.length();
        }

        private int solvePlain(String content, int index) {
            for (int i = index + 1; i < content.length(); ++i) {
                char ch = content.charAt(i);
                if (!this.isNumber(ch) && !this.isLetter(ch) && !this.isSymbol(ch, "_")) {
                    this.appendWord((short) 2, content.substring(index, i), index);
                    return i;
                }
            }

            this.appendWord((short) 2, content.substring(index), index);
            return content.length();
        }

        private int solveString(String content, int index) {
            char quote = content.charAt(index);

            for (int i = index + 1; i < content.length(); ++i) {
                if (content.charAt(i) == quote && content.charAt(i - 1) != '\\') {
                    this.appendWord((short) 3, content.substring(index, i + 1), index);
                    return i + 1;
                }
            }

            this.appendWord((short) 3, content.substring(index) + quote, index);
            return content.length();
        }

        private int skipComment1(String content, int index) {
            for (int i = index + 2; i < content.length(); ++i) {
                char ch = content.charAt(i);
                if (ch == '\r' || ch == '\n') {
                    return i;
                }
            }

            return content.length();
        }

        private int skipComment2(String content, int index) {
            for (int i = index + 2; i < content.length() - 1; ++i) {
                char ch1 = content.charAt(i);
                char ch2 = content.charAt(i + 1);
                if (ch1 == '*' || ch2 == '/') {
                    return i + 2;
                }
            }

            return content.length();
        }

        private boolean isSpace(char ch) {
            return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
        }

        private boolean isQuote(char ch) {
            return ch == '\'' || ch == '"';
        }

        private boolean isNumber(char ch) {
            return ch >= '0' && ch <= '9';
        }

        private boolean isLetter(char ch) {
            return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
        }

        private boolean isSymbol(char ch, String symbols) {
            return symbols != null && symbols.indexOf(ch) >= 0;
        }

        private void appendWord(short type, String text, int index) {
            int row = this.lineCount.get() + 1;
            int col = index - this.lineIndex.get();
            this.list.add(ProtoCompiler.this.new ProtoWord(type, text, row, col));
        }

        private void solveWord() throws ProtoException {
            int index = 0;

            while (index < this.list.size()) {
                ProtoWord word = (ProtoWord) this.list.get(index);
                if (word.matched("syntax")) {
                    index = this.solveSyntax(index);
                } else if (word.matched("package")) {
                    index = this.solvePackage(index);
                } else if (word.matched("import")) {
                    index = this.solveImport(index);
                } else if (word.matched("reserved")) {
                    index = this.solveReserved(index);
                } else if (word.matched("enum")) {
                    index = this.solveEnum(index);
                } else if (word.matched("message")) {
                    index = this.solveMessage(index);
                } else if (word.matched(";")) {
                    ++index;
                } else if (word.matched("}")) {
                    if (this.stack.size() <= 1) {
                        throw new ProtoException("found unmatched '}' at " + word.position());
                    }

                    this.stack.removeLast();
                    ++index;
                } else {
                    ProtoNode node = this.getContextNode();
                    if (node instanceof ProtoFile) {
                        throw new ProtoException("found unexpected word \"" + word.text + "\" at " + word.position());
                    }

                    if (node instanceof ProtoEnum) {
                        index = this.solveEnumPair(index);
                    } else {
                        if (!(node instanceof ProtoMessage)) {
                            throw new ProtoException("context node type unknown");
                        }

                        index = this.solveMessageField(index);
                    }
                }
            }

        }

        private int solveSyntax(int index) throws ProtoException {
            this.testWord(index + 1, "=", true);
            String syntax = this.getWordString(index + 2);
            this.testWord(index + 3, ";", true);
            ProtoFile file = this.getContextFile();
            file.syntax = syntax;
            return index + 4;
        }

        private int solvePackage(int index) throws ProtoException {
            StringBuilder sb = new StringBuilder();

            for (int i = index + 1; i < this.list.size(); ++i) {
                ProtoWord word = (ProtoWord) this.list.get(i);
                if (word.type == 1) {
                    throw new ProtoException("found unexpected number at " + word.position());
                }

                if (word.type == 3) {
                    throw new ProtoException("found unexpected string at " + word.position());
                }

                if (word.matched(";")) {
                    ProtoFile file = this.getContextFile();
                    file.pkgname = sb.toString();
                    return i + 1;
                }

                sb.append(word.text);
            }

            throw new ProtoException("not found ';' but reach end");
        }

        private int solveImport(int index) throws ProtoException {
            String fileName = this.getWordString(index + 1);
            this.testWord(index + 2, ";", true);
            ProtoFile file = this.getContextFile();
            file.imports.add(fileName);
            return index + 3;
        }

        private int solveReserved(int index) throws ProtoException {
            for (int i = index; i < this.list.size(); ++i) {
                if (this.testWord(i, ";", false)) {
                    return i + 1;
                }
            }

            return this.list.size();
        }

        private int solveEnum(int index) throws ProtoException {
            return this.solveNode(index, true);
        }

        private int solveMessage(int index) throws ProtoException {
            return this.solveNode(index, false);
        }

        private int solveNode(int index, boolean isEnum) throws ProtoException {
            String name = this.getWordPlain(index + 1);
            this.testWord(index + 2, "{", true);
            ProtoNode node = isEnum ? ProtoCompiler.this.new ProtoEnum() : ProtoCompiler.this.new ProtoMessage();
            ProtoNode parent = this.getContextNode();
            ((ProtoNode) node).name = name;
            ((ProtoNode) node).parent = parent;
            parent.children.add(node);
            this.stack.addLast(node);
            ProtoCompiler.this.echo("find " + (isEnum ? "Enum" : "Message") + "[" + name + "] in parent[" + parent.name + "] ...");
            return index + 3;
        }

        private int solveEnumPair(int index) throws ProtoException {
            ProtoEnumPair pair = ProtoCompiler.this.new ProtoEnumPair();
            String name = this.getWordPlain(index);
            this.testWord(index + 1, "=", true);
            int value = this.getWordInteger(index + 2);
            this.testWord(index + 3, ";", true);
            pair.name = name;
            pair.value = value;
            ProtoEnum obj = this.getContextEnum();
            obj.pairs.add(pair);
            return index + 4;
        }

        private int solveMessageField(int index) throws ProtoException {
            ProtoMessageField field = ProtoCompiler.this.new ProtoMessageField();
            int offsetEqual = -1;
            int offsetSemicolon = -1;

            int offset;
            for (offset = index; offset < this.list.size(); ++offset) {
                if (this.testWord(offset, "=", false)) {
                    if (offsetEqual < 0) {
                        offsetEqual = offset - index;
                    }
                } else if (this.testWord(offset, ";", false)) {
                    offsetSemicolon = offset - index;
                    break;
                }
            }

            if (offsetEqual < 0) {
                throw new ProtoException("not found '=' for message field at " + ((ProtoWord) this.list.get(index)).position());
            } else if (offsetSemicolon < 0) {
                throw new ProtoException("not found ';' for message field at " + ((ProtoWord) this.list.get(index)).position());
            } else if (offsetEqual >= 1) {
                field.name = this.getWordPlain(index + offsetEqual - 1);
                field.name = this.fixName(field.name);
                if (offsetEqual >= 2) {
                    field.type = this.getWordPlain(index + offsetEqual - 2);

                    String attrName;
                    for (offset = 0; offset < offsetEqual - 2; ++offset) {
                        attrName = this.getWordPlain(index + offset);
                        if ("required".equals(attrName)) {
                            field.required = true;
                        } else if ("optional".equals(attrName)) {
                            field.optional = true;
                        } else {
                            if (!"repeated".equals(attrName)) {
                                throw new ProtoException("found unknown field keyword \"" + attrName + "\" at " + ((ProtoWord) this.list.get(index + offset)).position());
                            }

                            field.repeated = true;
                        }
                    }

                    if (offsetSemicolon - offsetEqual >= 2) {
                        field.num = this.getWordInteger(index + offsetEqual + 1);
                    }

                    for (offset = offsetEqual + 2; offset < offsetSemicolon; offset += 5) {
                        this.testWord(index + offset + 0, "[", true);
                        attrName = this.getWordPlain(index + offset + 1);
                        this.testWord(index + offset + 2, "=", true);
                        ProtoWord attrValue = this.getWord(index + offset + 3);
                        this.testWord(index + offset + 4, "]", true);
                        if ("packed".endsWith(attrName)) {
                            if (attrValue.text.equalsIgnoreCase("true")) {
                                field.packed = true;
                            } else {
                                if (!attrValue.text.equalsIgnoreCase("false")) {
                                    throw new ProtoException("found unexpected attribute value at " + attrValue.position());
                                }

                                field.packed = false;
                            }
                        } else {
                            if (!"default".endsWith(attrName)) {
                                throw new ProtoException("found unknown attribute keyword \"" + attrName + "\" at " + ((ProtoWord) this.list.get(index + offset)).position());
                            }

                            if (attrValue.type == 1) {
                                field.defval = attrValue.text;
                            } else {
                                if (attrValue.type != 3) {
                                    throw new ProtoException("found unexpected attribute value at " + attrValue.position());
                                }

                                field.defval = attrValue.text;
                            }
                        }
                    }

                    ProtoMessage obj = this.getContextMessage();
                    obj.fields.add(field);
                    return index + offsetSemicolon + 1;
                } else {
                    throw new ProtoException("missing type for message field at " + ((ProtoWord) this.list.get(index + offsetEqual - 2)).position());
                }
            } else {
                throw new ProtoException("missing name for message field at " + ((ProtoWord) this.list.get(index + offsetEqual - 1)).position());
            }
        }

        private ProtoNode getContextNode() throws ProtoException {
            ProtoNode context = (ProtoNode) this.stack.getLast();
            if (context != null) {
                return context;
            } else {
                throw new ProtoException("context node not found");
            }
        }

        private ProtoFile getContextFile() throws ProtoException {
            ProtoNode context = this.getContextNode();
            if (context instanceof ProtoFile) {
                return (ProtoFile) context;
            } else {
                throw new ProtoException("context node not proto file");
            }
        }

        private ProtoEnum getContextEnum() throws ProtoException {
            ProtoNode context = this.getContextNode();
            if (context instanceof ProtoEnum) {
                return (ProtoEnum) context;
            } else {
                throw new ProtoException("context node not proto enum");
            }
        }

        private ProtoMessage getContextMessage() throws ProtoException {
            ProtoNode context = this.getContextNode();
            if (context instanceof ProtoMessage) {
                return (ProtoMessage) context;
            } else {
                throw new ProtoException("context node not proto message");
            }
        }

        private boolean testWord(int index, String str, boolean exception) throws ProtoException {
            if (index < this.list.size()) {
                ProtoWord word = (ProtoWord) this.list.get(index);
                if (word.matched(str)) {
                    return true;
                }

                if (exception) {
                    throw new ProtoException("not found \"" + str + "\" at " + word.position());
                }
            } else if (exception) {
                throw new ProtoException("need \"" + str + "\" but reach end");
            }

            return false;
        }

        private int getWordInteger(int index) throws ProtoException {
            ProtoWord word = this.getWord(index);
            if (word.type == 1) {
                try {
                    return Integer.parseInt(word.text);
                } catch (Exception var4) {
                    throw new ProtoException("not found integer at " + word.position());
                }
            } else {
                throw new ProtoException("not found number at " + word.position());
            }
        }

        private String getWordPlain(int index) throws ProtoException {
            ProtoWord word = this.getWord(index);
            if (word.type == 2) {
                return word.text;
            } else {
                throw new ProtoException("not found plain at " + word.position());
            }
        }

        private String getWordString(int index) throws ProtoException {
            ProtoWord word = this.getWord(index);
            String str = word.text;
            if (word.type == 3) {
                return str.substring(1, str.length() - 1);
            } else {
                throw new ProtoException("not found string at " + word.position());
            }
        }

        private ProtoWord getWord(int index) throws ProtoException {
            if (index < this.list.size()) {
                return (ProtoWord) this.list.get(index);
            } else {
                throw new ProtoException("reach file end unexpected");
            }
        }

        private String fixName(String name) {
            if (name == null) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();
                boolean upper = true;
                char[] var4 = name.toCharArray();
                int var5 = var4.length;

                int var6;
                for (var6 = 0; var6 < var5; ++var6) {
                    char ch = var4[var6];
                    if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                        if (sb.length() > 0) {
                            sb.append(upper ? Character.toUpperCase(ch) : ch);
                        } else {
                            sb.append(Character.toLowerCase(ch));
                        }

                        upper = false;
                    } else {
                        upper = true;
                    }
                }

                String result = sb.toString();
                String[] var10 = ProtoCompiler.JAVA_KEYWORDS;
                var6 = var10.length;

                for (int var11 = 0; var11 < var6; ++var11) {
                    String keyword = var10[var11];
                    if (keyword.equals(result)) {
                        result = result + "Val";
                    }
                }

                return result;
            }
        }
    }

    private class ProtoWord {
        public short type;
        public String text;
        public int row;
        public int col;

        public ProtoWord(short type, String text, int row, int col) {
            this.type = type;
            this.text = text;
            this.row = row;
            this.col = col;
        }

        public boolean matched(String str) {
            return this.text.equals(str);
        }

        public String position() {
            return "line[" + this.row + ":" + this.col + "]";
        }
    }
}
