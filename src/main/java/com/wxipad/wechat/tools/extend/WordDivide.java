//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordDivide {
    public static final int MODE_LEFT = 1;
    public static final int MODE_EACH = 0;
    public static final int MODE_RIGHT = -1;
    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
    public static final String SKIP_CHARS = " \t\r\n`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?";
    private final int mode;
    private final HashSet<String> dict;
    private final AtomicReference<String> skip;

    public WordDivide() {
        this.mode = -1;
        this.dict = new HashSet();
        this.skip = new AtomicReference(" \t\r\n`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?");
    }

    public WordDivide(int mode) {
        this.mode = mode;
        this.dict = new HashSet();
        this.skip = new AtomicReference(" \t\r\n`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?");
    }

    private static boolean block(String str) {
        char[] var1 = str.toCharArray();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            char ch = var1[var3];
            if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        synchronized (this.dict) {
            return this.dict.size();
        }
    }

    public boolean exist(String word) {
        synchronized (this.dict) {
            return this.dict.contains(word);
        }
    }

    public void skip(String chars) {
        if (chars != null) {
            this.skip.set(chars);
        }

    }

    public String skip() {
        return (String) this.skip.get();
    }

    public WordDivide clear() {
        synchronized (this.dict) {
            this.dict.clear();
            return this;
        }
    }

    public int add(InputStream in) {
        return this.add(in, DEFAULT_CHARSET);
    }

    public int add(InputStream in, Charset charset) {
        int count = 0;
        synchronized (this.dict) {
            InputStreamReader reader = new InputStreamReader(in, charset);

            try {
                try {
                    StringBuilder sb = new StringBuilder();

                    while (true) {
                        int ch;
                        String word;
                        while ((ch = reader.read()) != -1) {
                            if (ch != 32 && ch != 9 && ch != 13 && ch != 10) {
                                sb.append((char) ch);
                            } else if (sb.length() > 0) {
                                word = sb.toString().trim();
                                if (!word.isEmpty() && !this.dict.contains(word)) {
                                    this.dict.add(word);
                                    ++count;
                                }

                                sb.setLength(0);
                            }
                        }

                        if (sb.length() > 0) {
                            word = sb.toString().trim();
                            if (!word.isEmpty() && !this.dict.contains(word)) {
                                this.dict.add(word);
                                ++count;
                            }
                        }
                        break;
                    }
                } finally {
                    reader.close();
                }
            } catch (IOException var15) {
                Logger.getLogger(WordDivide.class.getName()).log(Level.SEVERE, (String) null, var15);
            }

            return count;
        }
    }

    public int add(String[] words) {
        int count = 0;
        synchronized (this.dict) {
            String[] var4 = words;
            int var5 = words.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String word = var4[var6];
                if (!this.dict.contains(word)) {
                    this.dict.add(word);
                    ++count;
                }
            }

            return count;
        }
    }

    public boolean add(String word) {
        boolean success = false;
        synchronized (this.dict) {
            if (!this.dict.contains(word)) {
                this.dict.add(word);
                success = true;
            }

            return success;
        }
    }

    public int remove(String[] words) {
        int count = 0;
        synchronized (this.dict) {
            String[] var4 = words;
            int var5 = words.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String word = var4[var6];
                if (this.dict.contains(word)) {
                    this.dict.remove(word);
                    ++count;
                }
            }

            return count;
        }
    }

    public boolean remove(String word) {
        boolean success = false;
        synchronized (this.dict) {
            if (this.dict.contains(word)) {
                this.dict.remove(word);
                success = true;
            }

            return success;
        }
    }

    public ArrayList<String> export() {
        ArrayList<String> list = new ArrayList();
        synchronized (this.dict) {
            list.addAll(this.dict);
            return list;
        }
    }

    public LinkedList<String> divide(String input) {
        if (input == null) {
            return new LinkedList();
        } else if (this.mode > 0) {
            return this.divideLeft(input);
        } else if (this.mode < 0) {
            return this.divideRight(input);
        } else {
            LinkedList<String> list1 = this.divideLeft(input);
            LinkedList<String> list2 = this.divideRight(input);
            return list1.size() < list2.size() ? list1 : list2;
        }
    }

    private LinkedList<String> divideLeft(String input) {
        LinkedList<String> list = new LinkedList();
        int index = 0;

        while (index < input.length()) {
            int find = -1;

            for (int i = input.length(); i > index; --i) {
                String part = input.substring(index, i);
                if (this.exist(part) || block(part)) {
                    list.addLast(part);
                    find = i;
                    break;
                }
            }

            if (find >= 0) {
                index = find;
            } else {
                char ch = input.charAt(index);
                if (" \t\r\n`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?".indexOf(ch) < 0) {
                    list.addLast(Character.toString(ch));
                }

                ++index;
            }
        }

        return list;
    }

    private LinkedList<String> divideRight(String input) {
        LinkedList<String> list = new LinkedList();
        int index = input.length();

        while (index > 0) {
            int find = -1;

            for (int i = 0; i < index; ++i) {
                String part = input.substring(i, index);
                if (this.exist(part) || block(part)) {
                    list.addFirst(part);
                    find = i;
                    break;
                }
            }

            if (find >= 0) {
                index = find;
            } else {
                char ch = input.charAt(index - 1);
                if (" \t\r\n`~!@#$%^&*()_+-=[]\\{}|;':\",./<>?".indexOf(ch) < 0) {
                    list.addFirst(Character.toString(ch));
                }

                --index;
            }
        }

        return list;
    }
}
