package com.wxipad.wechat.tools.crypto;

import com.wxipad.wechat.tools.constant.ConstFramework;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BASE64 {
    public static final int TYPE_STANDARD = 0;
    public static final int TYPE_URL_SAFE = 1;
    public static final int ENCODE_BREAKLINE = 4;
    private static final byte[] STANDARD_ENCODE_MAP = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] STANDARD_DECODE_MAP = {-3, -3, -3, -3, -3, -3, -3, -3, -3, -2, -2, -3, -3, -2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -2, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, 62, -3, -3, -3, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -3, -3, -3, -1, -3, -3, -3, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -3, -3, -3, -3, -3, -3, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3, -3};
    private static final byte ENCODE_END = 61;
    private static final byte DECODE_END = -1;
    private static final byte DECODE_SPACE = -2;
    private static final byte DECODE_ERR = -3;
    private static final byte NEW_LINE = 10;
    private static final int LINE_MAX = 76;
    private static final int BUFF_DECODE_BLOCK = 1024;

    private static void byte3to4(byte[] encodeMap, byte[] data, int index, int len, byte[] buff, int pos) {
        switch (len) {
            case 1:
                buff[pos] = encodeMap[((data[index] & 0xFF) >> 2)];
                buff[(pos + 1)] = encodeMap[((data[index] & 0x3) << 4)];
                buff[(pos + 2)] = 61;
                buff[(pos + 3)] = 61;
                break;
            case 2:
                buff[pos] = encodeMap[((data[index] & 0xFF) >> 2)];
                buff[(pos + 1)] = encodeMap[((data[index] & 0x3) << 4 | (data[(index + 1)] & 0xFF) >> 4)];
                buff[(pos + 2)] = encodeMap[((data[(index + 1)] & 0xF) << 2)];
                buff[(pos + 3)] = 61;
                break;
            default:
                buff[pos] = encodeMap[((data[index] & 0xFF) >> 2)];
                buff[(pos + 1)] = encodeMap[((data[index] & 0x3) << 4 | (data[(index + 1)] & 0xFF) >> 4)];
                buff[(pos + 2)] = encodeMap[((data[(index + 1)] & 0xF) << 2 | (data[(index + 2)] & 0xFF) >> 6)];
                buff[(pos + 3)] = encodeMap[(data[(index + 2)] & 0x3F)];
        }
    }

    private static void byte4to3(byte[] decodeMap, byte[] data, int len, byte[] buff, int pos) {
        switch (len) {
            case 1:
                break;
            case 2:
                buff[pos] = ((byte) (decodeMap[data[0]] << 2 | decodeMap[data[1]] >> 4));
                break;
            case 3:
                buff[pos] = ((byte) (decodeMap[data[0]] << 2 | decodeMap[data[1]] >> 4));
                buff[(pos + 1)] = ((byte) ((decodeMap[data[1]] & 0xF) << 4 | decodeMap[data[2]] >> 2));
                break;
            default:
                buff[pos] = ((byte) (decodeMap[data[0]] << 2 | decodeMap[data[1]] >> 4));
                buff[(pos + 1)] = ((byte) ((decodeMap[data[1]] & 0xF) << 4 | decodeMap[data[2]] >> 2));
                buff[(pos + 2)] = ((byte) ((decodeMap[data[2]] & 0x3) << 6 | decodeMap[data[3]]));
        }
    }

    public static String encode(byte[] data) {
        return encode(data, 0);
    }

    public static String encode(byte[] data, int options) {
        byte[] encodeMap = STANDARD_ENCODE_MAP;
        boolean breakline = (options & 4) != 0;
        int len = (data.length + 2) / 3;
        int lineSize = 19;
        int lenAdd = breakline ? len / lineSize : 0;
        byte[] buff = new byte[len * 4 + lenAdd];
        int blCnt = 0;

        for (int i = 0; i < len; ++i) {
            if (breakline && i > 0 && i % lineSize == 0) {
                buff[i * 4 + blCnt++] = 10;
            }

            byte3to4(encodeMap, data, i * 3, data.length - i * 3, buff, i * 4 + blCnt);
        }

        String str = new String(buff);
        if ((options & 1) != 0) {
            str = str.replace('+', '-').replace('/', '_');
        }

        return str;
    }

    public static boolean encode(InputStream in, OutputStream out) {
        byte[] encodeMap = STANDARD_ENCODE_MAP;
        byte[] buff = new byte[4];
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            byte[] data = new byte[3];
            int readCnt;
            while ((readCnt = bin.read(data)) != -1) {
                if (readCnt > 0) {
                    byte3to4(encodeMap, data, 0, readCnt, buff, 0);
                    bout.write(buff);
                }
            }
            bout.flush();
            return true;
        } catch (IOException ex) {
            if (ConstFramework.getDebug())
                Logger.getLogger(BASE64.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static byte[] decode(String str) {
        return decode(str, 0);
    }

    public static byte[] decode(String str, int options) {
        byte[] decodeMap = STANDARD_DECODE_MAP;
        byte[] data = new byte[4];
        byte[] buff = new byte[3072];
        ArrayList<byte[]> buffs = new ArrayList();
        int dataCnt = 0;
        int buffCnt = 0;
        if ((options & 1) != 0) {
            str = str.replace('-', '+').replace('_', '/');
        }

        int totalLen;
        for (totalLen = 0; totalLen < str.length(); ++totalLen) {
            char ch = str.charAt(totalLen);
            if (ch > 255) {
                return null;
            }

            byte b = (byte) ch;
            byte db = decodeMap[b];
            if (db == -3) {
                return null;
            }

            if (db != -2 && db != -1) {
                if (dataCnt == 4) {
                    byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                    dataCnt = 0;
                    buffCnt += 3;
                    if (buffCnt >= buff.length) {
                        buffs.add(buff);
                        buff = new byte[3072];
                        buffCnt = 0;
                    }
                }

                data[dataCnt++] = b;
            }
        }

        if (dataCnt > 0) {
            byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
            buffCnt += dataCnt - 1;
        }

        totalLen = buffCnt;

        byte[] bs;
        for (Iterator var12 = buffs.iterator(); var12.hasNext(); totalLen += bs.length) {
            bs = (byte[]) var12.next();
        }

        byte[] result = new byte[totalLen];

        for (int i = 0; i < buffs.size(); ++i) {
            System.arraycopy(buffs.get(i), 0, result, i * 3 * 1024, 3072);
        }

        if (buffCnt > 0) {
            System.arraycopy(buff, 0, result, buffs.size() * 3 * 1024, buffCnt);
        }

        return result;
    }

    public static boolean decode(InputStream in, OutputStream out) {
        byte[] decodeMap = STANDARD_DECODE_MAP;
        byte[] data = new byte[4];
        byte[] buff = new byte['ఀ'];
        int dataCnt = 0;
        int buffCnt = 0;
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            int read;
            while ((read = bin.read()) != -1) {
                char ch = (char) read;
                if (ch > 'ÿ') {
                    return false;
                }
                byte b = (byte) ch;
                byte db = decodeMap[b];
                if (db == -3)
                    return false;
                if ((db != -2) &&
                        (db != -1)) {
                    if (dataCnt == 4) {
                        byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                        dataCnt = 0;
                        buffCnt += 3;
                        if (buffCnt >= buff.length) {
                            bout.write(buff, 0, buffCnt);
                            buffCnt = 0;
                        }
                    }
                    data[(dataCnt++)] = b;
                }
            }
            if (dataCnt > 0) {
                byte4to3(decodeMap, data, dataCnt, buff, buffCnt);
                buffCnt += dataCnt - 1;
            }
            if (buffCnt > 0) {
                bout.write(buff, 0, buffCnt);
            }
            bout.flush();
            return true;
        } catch (IOException ex) {
            if (ConstFramework.getDebug())
                Logger.getLogger(BASE64.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
