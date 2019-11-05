//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinYin {
    private static final ArrayList<Char> data = new ArrayList();

    public PinYin() {
    }

    public static boolean load() {
        try {
            InputStream in = PinYin.class.getResourceAsStream("PinYin.txt");
            if (in == null) {
                return false;
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                Pattern pattern = Pattern.compile("([a-z]*)(\\s*)([1-5])(\\s*)(.)(\\s*)");
                data.clear();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!"".equals(line.trim())) {
                        Matcher matcher = pattern.matcher(line);
                        boolean matches = matcher.matches();
                        if (matches) {
                            char chinese = matcher.group(5).toCharArray()[0];
                            String spell = matcher.group(1);
                            byte tone = Byte.parseByte(matcher.group(3));
                            data.add(new Char(chinese, spell, tone));
                        }
                    }
                }

                reader.close();
                return true;
            }
        } catch (Exception var9) {
            Logger.getLogger(PinYin.class.getName()).log(Level.SEVERE, (String) null, var9);
            return false;
        }
    }

    public static ArrayList<Char> getData() {
        if (data.isEmpty()) {
            return load() ? data : null;
        } else {
            return data;
        }
    }

    public static ArrayList<String> getStrFirst(String text) {
        ArrayList<String> pinyin = new ArrayList();
        pinyin.add("");

        for (int i = 0; i < text.length(); ++i) {
            pinyin = addCharFirst(pinyin, text.charAt(i));
        }

        return pinyin;
    }

    private static ArrayList<String> addCharFirst(ArrayList<String> oldPinyin, char ch) {
        ArrayList<String> newPinyin = new ArrayList();
        ArrayList<String> chPinyin = getCharPinyin(ch);
        if (chPinyin.isEmpty()) {
            return oldPinyin;
        } else {
            Iterator var4 = oldPinyin.iterator();

            while (var4.hasNext()) {
                String prefix = (String) var4.next();
                Iterator var6 = chPinyin.iterator();

                while (var6.hasNext()) {
                    String suffix = (String) var6.next();
                    String str = prefix + suffix;
                    if (!newPinyin.contains(str)) {
                        newPinyin.add(str);
                    }
                }
            }

            return newPinyin;
        }
    }

    private static ArrayList<String> getCharPinyin(char ch) {
        ArrayList<String> pinyin = new ArrayList();
        Iterator var2 = getData().iterator();

        while (var2.hasNext()) {
            Char charObj = (Char) var2.next();
            if (charObj.chinese == ch && charObj.spell != null && charObj.spell.length() > 0) {
                pinyin.add(charObj.spell.toUpperCase().substring(0, 1));
            }
        }

        return pinyin;
    }

    public static final class Char {
        public char chinese;
        public String spell;
        public byte tone;

        public Char(char chinese, String spell, byte tone) {
            this.chinese = chinese;
            this.spell = spell;
            this.tone = tone;
        }
    }
}
