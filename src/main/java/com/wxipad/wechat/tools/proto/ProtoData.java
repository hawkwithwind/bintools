//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.proto;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class ProtoData {
    public static final String CHARSET_NAME = "UTF-8";
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final int FIXED32_SIZE = 4;
    public static final int FIXED64_SIZE = 8;
    public static final int MAX_VARINT32_SIZE = 5;
    public static final int MAX_VARINT64_SIZE = 10;
    public static final int WIRETYPE_VARINT = 0;
    public static final int WIRETYPE_FIXED64 = 1;
    public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    public static final int WIRETYPE_START_GROUP = 3;
    public static final int WIRETYPE_END_GROUP = 4;
    public static final int WIRETYPE_FIXED32 = 5;
    static final int TAG_TYPE_BITS = 3;
    static final int TAG_TYPE_MASK = 7;

    public ProtoData() {
    }

    public static int getTagWireType(int tag) {
        return tag & 7;
    }

    public static int getTagFieldNumber(int tag) {
        return tag >>> 3;
    }

    static int makeTag(int fieldNumber, int wireType) {
        return fieldNumber << 3 | wireType;
    }

    public static int decodeZigZag32(int n) {
        return n >>> 1 ^ -(n & 1);
    }

    public static long decodeZigZag64(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    public static int encodeZigZag32(int n) {
        return n << 1 ^ n >> 31;
    }

    public static long encodeZigZag64(long n) {
        return n << 1 ^ n >> 63;
    }

    public static byte[] int2bytes(int val) {
        byte[] data = new byte[4];

        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) (val >> i * 8 & 255);
        }

        return data;
    }

    public static byte[] long2bytes(long val) {
        byte[] data = new byte[8];

        for (int i = 0; i < data.length; ++i) {
            data[i] = (byte) ((int) (val >> i * 8 & 255L));
        }

        return data;
    }

    public static byte[] varint2bytes(int val) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int value;
        for (value = val; (value & -128) != 0; value >>>= 7) {
            out.write((byte) (value & 127 | 128));
        }

        out.write((byte) value);
        return out.toByteArray();
    }

    public static byte[] varint2bytes(long val) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        long value;
        for (value = val; (value & -128L) != 0L; value >>>= 7) {
            out.write((byte) ((int) value & 127 | 128));
        }

        out.write((byte) ((int) value));
        return out.toByteArray();
    }

    public static int bytes2int(byte[] data, int offset) throws ProtoException {
        if (offset + 4 > data.length) {
            throw ProtoException.byteArrayReachEnd();
        } else {
            int val = 0;

            for (int i = 0; i < 4; ++i) {
                val |= (data[offset + i] & 255) << i * 8;
            }

            return val;
        }
    }

    public static long bytes2long(byte[] data, int offset) throws ProtoException {
        if (offset + 8 > data.length) {
            throw ProtoException.byteArrayReachEnd();
        } else {
            long val = 0L;

            for (int i = 0; i < 8; ++i) {
                val |= ((long) data[offset + i] & 255L) << i * 8;
            }

            return val;
        }
    }

    public static int bytes2varint32(byte[] data, int offset) throws ProtoException {
        int len = testVarintLength(data, offset);
        if (len > 10) {
            throw ProtoException.varintLengthOverflow();
        } else if (len > 5) {
            return (int) bytes2varint64(data, offset);
        } else {
            int value = 0;

            for (int i = offset + len - 1; i >= offset; --i) {
                value <<= 7;
                value |= data[i] & 127;
            }

            return value;
        }
    }

    public static long bytes2varint64(byte[] data, int offset) throws ProtoException {
        int len = testVarintLength(data, offset);
        if (len > 10) {
            throw ProtoException.varintLengthOverflow();
        } else {
            long value = 0L;

            for (int i = offset + len - 1; i >= offset; --i) {
                value <<= 7;
                value |= (long) (data[i] & 127);
            }

            return value;
        }
    }

    public static int testVarintLength(byte[] data, int offset) throws ProtoException {
        for (int pos = offset; pos < data.length; ++pos) {
            if ((data[pos] & -128) == 0) {
                return pos - offset + 1;
            }
        }

        throw ProtoException.byteArrayReachEnd();
    }

    public static boolean checkNotNull(Object... fields) {
        Object[] var1 = fields;
        int var2 = fields.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            Object field = var1[var3];
            if (field == null) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkContain(int val, int... values) {
        int[] var2 = values;
        int var3 = values.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            int value = var2[var4];
            if (value == val) {
                return true;
            }
        }

        return false;
    }
}
