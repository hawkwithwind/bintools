//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.json;

import java.util.ArrayList;
import java.util.HashMap;

class JSONSolver {
    private int index;
    private char[] charArr;

    public JSONSolver(String source) throws JSONException {
        if (source != null && source.length() != 0) {
            this.index = 0;
            this.charArr = source.toCharArray();
        } else {
            throw new JSONException("no source string for JSONSolver()");
        }
    }

    private static int getHexCharVal(char ch) throws JSONException {
        if (ch >= 'A' && ch <= 'F') {
            return ch - 65 + 10;
        } else if (ch >= 'a' && ch <= 'f') {
            return ch - 97 + 10;
        } else if (ch >= '0' && ch <= '9') {
            return ch - 48;
        } else {
            throw new JSONException("expected hex char but get '" + ch + "'");
        }
    }

    private static boolean isSymbol(char ch) {
        return ch == '+' || ch == '-';
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isE(char ch) {
        return ch == 'e' || ch == 'E';
    }

    private static boolean isPoint(char ch) {
        return ch == '.';
    }

    public HashMap<String, Object> solveJSONObject(boolean noException) throws JSONException {
        HashMap<String, Object> rtn = new HashMap();
        this.nextIndex();
        if (this.indexChSolve() != '{') {
            throw new JSONException("expected '{' at index " + this.index);
        } else {
            this.nextIndex();

            for (; this.indexCh() != '}'; this.nextIndex()) {
                String name = this.solveName();
                this.nextIndex();
                if (this.indexChSolve() != ':') {
                    throw new JSONException("expected ':' at index " + this.index);
                }

                this.nextIndex();
                Object value = this.solveObject(noException);
                if (rtn.containsKey(name)) {
                    rtn.remove(name);
                }

                rtn.put(name, value);
                this.nextIndex();
                if (this.indexCh() != ',' && this.indexCh() != '}') {
                    throw new JSONException("expected '}' or ',' at index " + this.index);
                }

                if (this.indexCh() == ',') {
                    ++this.index;
                }
            }

            ++this.index;
            return rtn;
        }
    }

    public ArrayList<Object> solveJSONArray(boolean noException) throws JSONException {
        ArrayList<Object> rtn = new ArrayList();
        this.nextIndex();
        if (this.indexChSolve() != '[') {
            throw new JSONException("expected '[' at index " + this.index);
        } else {
            this.nextIndex();

            for (; this.indexCh() != ']'; this.nextIndex()) {
                Object value = this.solveObject(noException);
                rtn.add(value);
                this.nextIndex();
                if (this.indexCh() != ',' && this.indexCh() != ']') {
                    throw new JSONException("expected ']' or ',' at index " + this.index);
                }

                if (this.indexCh() == ',') {
                    ++this.index;
                }
            }

            ++this.index;
            return rtn;
        }
    }

    private int solveUnicodeHex() throws JSONException {
        int val = 0;

        for (int i = 0; i < 4; ++i) {
            val = val * 16 + getHexCharVal(this.indexChSolve());
        }

        return val;
    }

    private String solveString() throws JSONException {
        StringBuilder sb = new StringBuilder();
        char quote = this.indexCh();
        if (quote != '\'' && quote != '"') {
            throw new JSONException("expected \" or ' at index " + this.index);
        } else {
            ++this.index;

            while (true) {
                while (true) {
                    while (this.index < this.charArr.length) {
                        if (this.indexCh() == quote) {
                            ++this.index;
                            return sb.toString();
                        }

                        if (this.indexCh() == '\\') {
                            ++this.index;
                            if (this.indexCh() != '\'' && this.indexCh() != '"' && this.indexCh() != '\\') {
                                if (this.indexCh() == '/') {
                                    sb.append(this.indexChSolve());
                                } else if (this.indexCh() == 'b') {
                                    ++this.index;
                                    sb.append("\b");
                                } else if (this.indexCh() == 'f') {
                                    ++this.index;
                                    sb.append("\f");
                                } else if (this.indexCh() == 'n') {
                                    ++this.index;
                                    sb.append("\n");
                                } else if (this.indexCh() == 'r') {
                                    ++this.index;
                                    sb.append("\r");
                                } else if (this.indexCh() == 't') {
                                    ++this.index;
                                    sb.append("\t");
                                } else {
                                    if (this.indexCh() != 'u') {
                                        throw new JSONException("unsolved escapes at index " + this.index);
                                    }

                                    ++this.index;
                                    int chVal = this.solveUnicodeHex();
                                    char unicodeCh = (char) chVal;
                                    sb.append(unicodeCh);
                                }
                            } else {
                                sb.append(this.indexChSolve());
                            }
                        } else {
                            sb.append(this.indexChSolve());
                        }
                    }

                    throw new JSONException("unexpected string end");
                }
            }
        }
    }

    private Object solveNumber() throws JSONException {
        boolean isDouble = false;
        boolean isNegative = false;
        boolean isIndexNegative = false;
        int decimalCnt = 0;
        long partInteger = 0L;
        double partDecimal = 0.0D;
        int partIndex = 0;
        int status = 0;
        if (!this.matchStr("0x")) {
            while (status != -1) {
                char ch = this.indexCh();
                switch (status) {
                    case 0:
                        if (isSymbol(ch)) {
                            isNegative = this.indexChSolve() == '-';
                            status = 1;
                        } else {
                            if (!isDigit(ch)) {
                                throw new JSONException("expect symbol or digit at index " + this.index);
                            }

                            partInteger = partInteger * 10L + (long) (this.indexChSolve() - 48);
                            status = 2;
                        }
                        break;
                    case 1:
                        if (!isDigit(ch)) {
                            throw new JSONException("expect digit at index " + this.index);
                        }

                        partInteger = partInteger * 10L + (long) (this.indexChSolve() - 48);
                        status = 2;
                        break;
                    case 2:
                        if (isPoint(ch)) {
                            this.indexChSolve();
                            isDouble = true;
                            status = 3;
                        } else if (isDigit(ch)) {
                            partInteger = partInteger * 10L + (long) (this.indexChSolve() - 48);
                        } else {
                            status = -1;
                        }
                        break;
                    case 3:
                        if (isDigit(ch)) {
                            double var10001 = (double) (this.indexChSolve() - 48);
                            ++decimalCnt;
                            partDecimal += var10001 / Math.pow(10.0D, (double) decimalCnt);
                        } else if (isE(ch)) {
                            this.indexChSolve();
                            isDouble = true;
                            status = 4;
                        } else {
                            status = -1;
                        }
                        break;
                    case 4:
                        if (isSymbol(ch)) {
                            isIndexNegative = this.indexChSolve() == '-';
                            status = 5;
                        } else {
                            if (!isDigit(ch)) {
                                throw new JSONException("expect symbol or digit at index " + this.index);
                            }

                            partIndex = partIndex * 10 + (this.indexChSolve() - 48);
                            status = 6;
                        }
                        break;
                    case 5:
                        if (!isDigit(ch)) {
                            throw new JSONException("expect digit at index " + this.index);
                        }

                        partIndex = partIndex * 10 + (this.indexChSolve() - 48);
                        status = 6;
                        break;
                    case 6:
                        if (isDigit(ch)) {
                            partIndex = partIndex * 10 + (this.indexChSolve() - 48);
                        } else {
                            status = -1;
                        }
                        break;
                    default:
                        throw new JSONException("solve number unknow status at index " + this.index);
                }
            }

            if (isDouble) {
                partIndex = isIndexNegative ? -partIndex : partIndex;
                double value = ((double) partInteger + partDecimal) * Math.pow(10.0D, (double) partIndex);
                Double number = isNegative ? -value : value;
                return number;
            } else {
                Long number = new Long(isNegative ? -partInteger : partInteger);
                return number;
            }
        } else {
            this.index += "0x".length();
            StringBuilder sb = new StringBuilder();

            for (char ch = this.indexCh(); ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f'; ch = this.indexCh()) {
                sb.append(ch);
                ++this.index;
            }

            return Long.parseLong(sb.toString(), 16);
        }
    }

    private Object solveObject(boolean noException) throws JSONException {
        Object rtn = null;
        this.nextIndex();
        char beginCh = this.indexCh();
        if (beginCh == '{') {
            HashMap data = this.solveJSONObject(noException);
            rtn = new JSONObject(data, noException);
        } else if (beginCh == '[') {
            ArrayList<Object> data = this.solveJSONArray(noException);
            rtn = new JSONArray(data);
        } else if (beginCh != '\'' && beginCh != '"') {
            if (beginCh != '-' && (beginCh < '0' || beginCh > '9')) {
                if (this.matchStr("true")) {
                    this.index += "true".length();
                    return Boolean.TRUE;
                }

                if (this.matchStr("false")) {
                    this.index += "false".length();
                    return Boolean.FALSE;
                }

                if (this.matchStr("null")) {
                    this.index += "null".length();
                    return null;
                }
            } else {
                rtn = this.solveNumber();
            }
        } else {
            rtn = this.solveString();
        }

        if (rtn == null) {
            throw new JSONException("unsolved object at index " + this.index);
        } else {
            return rtn;
        }
    }

    private String solveName() throws JSONException {
        this.nextIndex();
        char beginCh = this.indexCh();
        if (beginCh != '\'' && beginCh != '"') {
            StringBuilder sb = new StringBuilder();
            char ch = beginCh;

            for (int startIndex = this.index; startIndex != this.index && ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '$' || ch == '_'; ch = this.indexCh()) {
                sb.append(ch);
                ++this.index;
            }

            return sb.toString();
        } else {
            return this.solveString();
        }
    }

    private int nextIndex() throws JSONException {
        while (this.index < this.charArr.length) {
            if (this.indexCh() == '/') {
                if (this.matchStr("//")) {
                    for (this.index += 2; this.index < this.charArr.length; ++this.index) {
                        if (this.indexCh() == '\n' || this.indexCh() == '\r') {
                            ++this.index;
                            break;
                        }
                    }

                    if (this.index >= this.charArr.length) {
                        return -1;
                    }
                } else if (this.matchStr("/*")) {
                    for (this.index += 2; this.index < this.charArr.length; ++this.index) {
                        if (this.matchStr("*/")) {
                            this.index += 2;
                            break;
                        }
                    }

                    if (this.index >= this.charArr.length) {
                        return -1;
                    }
                }
            }

            if (this.indexCh() != ' ' && this.indexCh() != '\t' && this.indexCh() != '\n' && this.indexCh() != '\r') {
                return this.index;
            }

            ++this.index;
        }

        return -1;
    }

    private char indexCh() throws JSONException {
        return this.indexCh(0);
    }

    private char indexCh(int offset) throws JSONException {
        if (this.index + offset >= 0 && this.index + offset < this.charArr.length) {
            return this.charArr[this.index + offset];
        } else {
            throw new JSONException("unexpected json end");
        }
    }

    private char indexChSolve() throws JSONException {
        if (this.index < this.charArr.length) {
            return this.charArr[this.index++];
        } else {
            throw new JSONException("unexpected json end");
        }
    }

    private boolean matchStr(String str) throws JSONException {
        if (str != null && str.length() != 0) {
            if (this.charArr.length >= str.length() + this.index) {
                char[] tmpArr = str.toCharArray();

                for (int i = 0; i < tmpArr.length; ++i) {
                    if (this.indexCh(i) != tmpArr[i]) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
