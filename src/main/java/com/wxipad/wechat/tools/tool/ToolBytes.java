//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Random;

public class ToolBytes {
    public static final String CHARSET_NAME = "UTF-8";
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final byte BYTE_TRUE = 1;
    public static final byte BYTE_FASLE = 0;
    public static final boolean LITTLE_ENDIAN = false;
    public static final boolean BIG_ENDIAN = true;
    public static final boolean DEFAULT_ENDIAN = false;
    public static final boolean DEFAULT_BOOLEAN = false;
    public static final byte DEFAULT_BYTE = 0;
    public static final char DEFAULT_CHAR = '\u0000';
    public static final short DEFAULT_SHORT = 0;
    public static final int DEFAULT_INT = 0;
    public static final long DEFAULT_LONG = 0L;
    public static final float DEFAULT_FLOAT = 0.0F;
    public static final double DEFAULT_DOUBLE = 0.0D;
    public static final int SIZE_BOOLEAN = 1;
    public static final int SIZE_BYTE = 1;
    public static final int SIZE_CHAR = 2;
    public static final int SIZE_SHORT = 2;
    public static final int SIZE_INT = 4;
    public static final int SIZE_LONG = 8;
    public static final int SIZE_FLOAT = 4;
    public static final int SIZE_DOUBLE = 8;
    private static final int BUFF_SIZE = 1024;
    private static final BytesConvert BCL = new BytesConvert(false);
    private static final BytesConvert BCB = new BytesConvert(true);

    public ToolBytes() {
    }

    public static BytesConvert i() {
        return i(false);
    }

    public static BytesConvert i(boolean endian) {
        return endian ? BCB : BCL;
    }

    public static short byte2ubyte(byte val) {
        if (val < 0) {
            short result = 256;
            result = (short) (result + val);
            return result;
        } else {
            return (short) val;
        }
    }

    public static int short2ushort(short val) {
        if (val < 0) {
            int result = 65536;
            result += val;
            return result;
        } else {
            return val;
        }
    }

    public static long int2uint(int val) {
        if (val < 0) {
            long result = 4294967296L;
            result += (long) val;
            return result;
        } else {
            return (long) val;
        }
    }

    public static byte ubyte2byte(short val) {
        return (byte) (val & 255);
    }

    public static short ushort2short(int val) {
        return (short) (val & '\uffff');
    }

    public static int uint2int(long val) {
        return (int) (val & -1L);
    }

    public static String bytes2Hex(byte[] data) {
        return ToolStr.bytes2Hex(data, (String) null);
    }

    public static String bytes2Hex(byte[] data, String prefix) {
        return ToolStr.bytes2Hex(data, prefix);
    }

    public static byte[] hex2Bytes(String hex) {
        return ToolStr.hex2Bytes(hex);
    }

    public static byte[] cutBytes(byte[] data, int offset, int length) {
        if (data == null) {
            return null;
        } else if (offset + length > data.length) {
            return null;
        } else {
            byte[] cutBytes = new byte[length];
            System.arraycopy(data, offset, cutBytes, 0, length);
            return cutBytes;
        }
    }

    public static byte[] subBytes(byte[] data, int start, int end) {
        if (data == null) {
            return null;
        } else if (start >= 0 && end >= start && end <= data.length) {
            byte[] cutBytes = new byte[end - start];
            System.arraycopy(data, start, cutBytes, 0, cutBytes.length);
            return cutBytes;
        } else {
            return null;
        }
    }

    public static void fillBytes(byte[] dest, byte[] src, int offset) {
        if (dest != null && src != null) {
            int pos1 = 0;
            int pos2 = offset;
            int len = src.length;
            if (offset < 0) {
                pos1 -= offset;
                len += offset;
                pos2 = 0;
            }

            if (pos2 + len > dest.length) {
                len = dest.length - pos2;
            }

            if (len <= 0) {
                return;
            }

            System.arraycopy(src, pos1, dest, pos2, len);
        }

    }

    public static byte[] cloneBytes(byte[] data) {
        if (data != null) {
            byte[] result = new byte[data.length];
            System.arraycopy(data, 0, result, 0, data.length);
            return result;
        } else {
            return null;
        }
    }

    public static byte[] joinBytes(byte[]... datas) {
        int length = 0;
        int offset = 0;
        byte[][] var3 = datas;
        int var4 = datas.length;

        int var5;
        for (var5 = 0; var5 < var4; ++var5) {
            byte[] data = var3[var5];
            if (data != null) {
                length += data.length;
            }
        }

        byte[] result = new byte[length];
        byte[][] var9 = datas;
        var5 = datas.length;

        for (int var10 = 0; var10 < var5; ++var10) {
            byte[] data = var9[var10];
            if (data != null) {
                System.arraycopy(data, 0, result, offset, data.length);
                offset += data.length;
            }
        }

        return result;
    }

    public static int indexBytes(byte[] data, byte[] target) {
        for (int i = 0; i <= data.length - target.length; ++i) {
            boolean ok = true;

            for (int j = 0; j < target.length; ++j) {
                if (data[i + j] != target[j]) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return i;
            }
        }

        return -1;
    }

    public static byte[] randomBytes(int length) {
        byte[] data = new byte[Math.max(length, 0)];
        (new Random()).nextBytes(data);
        return data;
    }

    public static byte[] reverseBytes(byte[] data) {
        if (data == null) {
            return null;
        } else {
            byte[] result = new byte[data.length];

            for (int i = 0; i < data.length; ++i) {
                result[i] = data[data.length - i - 1];
            }

            return result;
        }
    }

    public static boolean equals(byte[] data1, byte[] data2) {
        if (data1 != null && data2 != null && data1.length == data2.length) {
            for (int i = 0; i < data1.length; ++i) {
                if (data1[i] != data2[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean equals(byte[] data1, int pos1, byte[] data2, int pos2, int len) {
        if (data1 != null && data2 != null) {
            for (int i = 0; i < len; ++i) {
                int index1 = pos1 + i;
                int index2 = pos2 + i;
                if (index1 >= 0 && index1 < data1.length) {
                    return false;
                }

                if (index2 >= 0 && index2 < data2.length) {
                    return false;
                }

                if (data1[index1] != data2[index2]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static String toString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            for (int i = 0; i < data.length; ++i) {
                if (sb.length() > 0) {
                    sb.append(",");
                }

                int value = data[i] < 0 ? data[i] + 256 : data[i];
                String part = value < 16 ? "0" : "";
                part = part + Integer.toHexString(value).toUpperCase();
                sb.append(part);
            }

            return "[" + sb.toString() + "]";
        } else {
            return "null";
        }
    }

    public static class BytesWriter {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        private final BytesConvert bc;

        public BytesWriter() {
            this.bc = ToolBytes.i(false);
        }

        public BytesWriter(boolean endian) {
            this.bc = ToolBytes.i(endian);
        }

        public BytesWriter write(boolean val) {
            return this.writeBoolean(val);
        }

        public BytesWriter write(byte val) {
            return this.writeByte(val);
        }

        public BytesWriter write(char val) {
            return this.writeChar(val);
        }

        public BytesWriter write(short val) {
            return this.writeShort(val);
        }

        public BytesWriter write(int val) {
            return this.writeInt(val);
        }

        public BytesWriter write(long val) {
            return this.writeLong(val);
        }

        public BytesWriter write(float val) {
            return this.writeFloat(val);
        }

        public BytesWriter write(double val) {
            return this.writeDouble(val);
        }

        public BytesWriter write(byte[] val) {
            return this.writeBytes(val);
        }

        public BytesWriter write(byte[] val, int off, int len) {
            return this.writeBytes(val, off, len);
        }

        public BytesWriter write(String val) {
            return this.writeString(val);
        }

        public BytesWriter write(String val, Charset charset) {
            return this.writeString(val, charset);
        }

        public BytesWriter writeBoolean(boolean val) {
            this.writeBytes(this.bc.boolean2bytes(val));
            return this;
        }

        public BytesWriter writeByte(byte val) {
            this.out.write(val);
            return this;
        }

        public BytesWriter writeUByte(short val) {
            this.writeBytes(this.bc.ubyte2bytes(val));
            return this;
        }

        public BytesWriter writeChar(char val) {
            this.writeBytes(this.bc.char2bytes(val));
            return this;
        }

        public BytesWriter writeShort(short val) {
            this.writeBytes(this.bc.short2bytes(val));
            return this;
        }

        public BytesWriter writeUShort(int val) {
            this.writeBytes(this.bc.ushort2bytes(val));
            return this;
        }

        public BytesWriter writeInt(int val) {
            this.writeBytes(this.bc.int2bytes(val));
            return this;
        }

        public BytesWriter writeUInt(long val) {
            this.writeBytes(this.bc.uint2bytes(val));
            return this;
        }

        public BytesWriter writeLong(long val) {
            this.writeBytes(this.bc.long2bytes(val));
            return this;
        }

        public BytesWriter writeULong(long val) {
            this.writeBytes(this.bc.ulong2bytes(val));
            return this;
        }

        public BytesWriter writeFloat(float val) {
            this.writeBytes(this.bc.float2bytes(val));
            return this;
        }

        public BytesWriter writeDouble(double val) {
            this.writeBytes(this.bc.double2bytes(val));
            return this;
        }

        public BytesWriter writeBytes(byte[] val) {
            if (val != null) {
                this.out.write(val, 0, val.length);
            }

            return this;
        }

        public BytesWriter writeBytes(byte[] val, int off, int len) {
            if (val != null) {
                this.out.write(val, off, len);
            }

            return this;
        }

        public BytesWriter writeString(String val) {
            return this.writeString(val, ToolBytes.CHARSET);
        }

        public BytesWriter writeString(String val, Charset charset) {
            if (val != null) {
                byte[] data = val.getBytes(charset);
                this.out.write(data, 0, data.length);
                this.out.write(0);
            }

            return this;
        }

        public void reset() {
            this.out.reset();
        }

        public byte[] finish() {
            return this.out.toByteArray();
        }

        public byte[] finish(boolean reset) {
            byte[] data = this.out.toByteArray();
            if (reset) {
                this.out.reset();
            }

            return data;
        }
    }

    public static class BytesReader {
        private final byte[] data;
        private final BytesConvert bc;
        private int cursor;

        public BytesReader(byte[] data) {
            this.data = data;
            this.cursor = 0;
            this.bc = ToolBytes.i(false);
        }

        public BytesReader(byte[] data, boolean endian) {
            this.data = data;
            this.cursor = 0;
            this.bc = ToolBytes.i(endian);
        }

        public boolean readBoolean() {
            boolean val = this.bc.bytes2boolean(this.data, this.cursor);
            ++this.cursor;
            return val;
        }

        public byte readByte() {
            byte val = this.bc.bytes2byte(this.data, this.cursor);
            ++this.cursor;
            return val;
        }

        public short readUByte() {
            short val = this.bc.bytes2ubyte(this.data, this.cursor);
            ++this.cursor;
            return val;
        }

        public char readChar() {
            char val = this.bc.bytes2char(this.data, this.cursor);
            this.cursor += 2;
            return val;
        }

        public short readShort() {
            short val = this.bc.bytes2short(this.data, this.cursor);
            this.cursor += 2;
            return val;
        }

        public int readUShort() {
            int val = this.bc.bytes2ushort(this.data, this.cursor);
            this.cursor += 2;
            return val;
        }

        public int readInt() {
            int val = this.bc.bytes2int(this.data, this.cursor);
            this.cursor += 4;
            return val;
        }

        public long readUInt() {
            long val = this.bc.bytes2uint(this.data, this.cursor);
            this.cursor += 4;
            return val;
        }

        public long readLong() {
            long val = this.bc.bytes2long(this.data, this.cursor);
            this.cursor += 8;
            return val;
        }

        public long readULong() {
            long val = this.bc.bytes2ulong(this.data, this.cursor);
            this.cursor += 8;
            return val;
        }

        public float readFloat() {
            float val = this.bc.bytes2float(this.data, this.cursor);
            this.cursor += 4;
            return val;
        }

        public double readDouble() {
            double val = this.bc.bytes2double(this.data, this.cursor);
            this.cursor += 8;
            return val;
        }

        public byte[] readBytes(int length) {
            if (length > 0) {
                byte[] val = this.bc.cutbytes(this.data, this.cursor, length);
                this.cursor += length;
                return val;
            } else {
                return length == 0 ? new byte[0] : null;
            }
        }

        public String readString() {
            return this.readString(ToolBytes.CHARSET);
        }

        public String readString(Charset charset) {
            for (int length = 0; this.cursor + length < this.data.length; ++length) {
                if (this.data[this.cursor + length] == 0) {
                    byte[] val = this.bc.cutbytes(this.data, this.cursor, length);
                    this.cursor += length + 1;
                    return new String(val, charset);
                }
            }

            return null;
        }

        public void reset() {
            this.cursor = 0;
        }

        public int getCursor() {
            return this.cursor;
        }

        public void setCursor(int cursor) {
            this.cursor = cursor;
        }

        public void skip(int length) {
            this.cursor += Math.max(length, 0);
        }
    }

    public static class BytesConvert {
        private final boolean endian;

        public BytesConvert(boolean endian) {
            this.endian = endian;
        }

        public static byte[] subbytes(byte[] data, int start, int end) {
            return ToolBytes.subBytes(data, start, end);
        }

        public byte[] boolean2bytes(boolean val) {
            byte[] data = new byte[]{(byte) (val ? 1 : 0)};
            return data;
        }

        public byte[] byte2bytes(byte val) {
            byte[] data = new byte[]{val};
            return data;
        }

        public byte[] ubyte2bytes(short val) {
            byte[] data = new byte[]{(byte) (val & 255)};
            return data;
        }

        public byte[] char2bytes(char val) {
            byte[] data = new byte[2];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 2 - i - 1 : i;
                data[i] = (byte) (val >> x * 8 & 255);
            }

            return data;
        }

        public byte[] short2bytes(short val) {
            byte[] data = new byte[2];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 2 - i - 1 : i;
                data[i] = (byte) (val >> x * 8 & 255);
            }

            return data;
        }

        public byte[] ushort2bytes(int val) {
            byte[] data = new byte[2];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 2 - i - 1 : i;
                data[i] = (byte) (val >> x * 8 & 255);
            }

            return data;
        }

        public byte[] int2bytes(int val) {
            byte[] data = new byte[4];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 4 - i - 1 : i;
                data[i] = (byte) (val >> x * 8 & 255);
            }

            return data;
        }

        public byte[] uint2bytes(long val) {
            byte[] data = new byte[4];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 4 - i - 1 : i;
                data[i] = (byte) ((int) (val >> x * 8 & 255L));
            }

            return data;
        }

        public byte[] long2bytes(long val) {
            byte[] data = new byte[8];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 8 - i - 1 : i;
                data[i] = (byte) ((int) (val >> x * 8 & 255L));
            }

            return data;
        }

        public byte[] ulong2bytes(long val) {
            return this.long2bytes(val);
        }

        public byte[] float2bytes(float val) {
            int valNum = Float.floatToIntBits(val);
            byte[] data = new byte[4];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 4 - i - 1 : i;
                data[i] = (byte) (valNum >> x * 8 & 255);
            }

            return data;
        }

        public byte[] double2bytes(double val) {
            long valNum = Double.doubleToLongBits(val);
            byte[] data = new byte[8];

            for (int i = 0; i < data.length; ++i) {
                int x = this.endian ? 8 - i - 1 : i;
                data[i] = (byte) ((int) (valNum >> x * 8 & 255L));
            }

            return data;
        }

        public boolean bytes2boolean(byte[] data) {
            return this.bytes2boolean(data, 0);
        }

        public boolean bytes2boolean(byte[] data, int offset) {
            if (offset + 1 > data.length) {
                return false;
            } else {
                return data[offset] != 0;
            }
        }

        public byte bytes2byte(byte[] data) {
            return this.bytes2byte(data, 0);
        }

        public byte bytes2byte(byte[] data, int offset) {
            return offset + 1 > data.length ? 0 : data[offset];
        }

        public short bytes2ubyte(byte[] data) {
            return this.bytes2ubyte(data, 0);
        }

        public short bytes2ubyte(byte[] data, int offset) {
            if (offset + 1 > data.length) {
                return 0;
            } else {
                short val = 0;
                val = (short) (val | (short) data[offset] & 255);
                return val;
            }
        }

        public char bytes2char(byte[] data) {
            return this.bytes2char(data, 0);
        }

        public char bytes2char(byte[] data, int offset) {
            if (offset + 2 > data.length) {
                return '\u0000';
            } else {
                char val = 0;

                for (int i = 0; i < 2; ++i) {
                    int x = this.endian ? 2 - i - 1 : i;
                    val = (char) (val | ((char) data[offset + i] & 255) << x * 8);
                }

                return val;
            }
        }

        public short bytes2short(byte[] data) {
            return this.bytes2short(data, 0);
        }

        public short bytes2short(byte[] data, int offset) {
            if (offset + 2 > data.length) {
                return 0;
            } else {
                short val = 0;

                for (int i = 0; i < 2; ++i) {
                    int x = this.endian ? 2 - i - 1 : i;
                    val = (short) (val | ((short) data[offset + i] & 255) << x * 8);
                }

                return val;
            }
        }

        public int bytes2ushort(byte[] data) {
            return this.bytes2ushort(data, 0);
        }

        public int bytes2ushort(byte[] data, int offset) {
            if (offset + 2 > data.length) {
                return 0;
            } else {
                int val = 0;

                for (int i = 0; i < 2; ++i) {
                    int x = this.endian ? 2 - i - 1 : i;
                    val |= (data[offset + i] & 255) << x * 8;
                }

                return val;
            }
        }

        public int bytes2int(byte[] data) {
            return this.bytes2int(data, 0);
        }

        public int bytes2int(byte[] data, int offset) {
            if (offset + 4 > data.length) {
                return 0;
            } else {
                int val = 0;

                for (int i = 0; i < 4; ++i) {
                    int x = this.endian ? 4 - i - 1 : i;
                    val |= (data[offset + i] & 255) << x * 8;
                }

                return val;
            }
        }

        public long bytes2uint(byte[] data) {
            return this.bytes2uint(data, 0);
        }

        public long bytes2uint(byte[] data, int offset) {
            if (offset + 4 > data.length) {
                return 0L;
            } else {
                long val = 0L;

                for (int i = 0; i < 4; ++i) {
                    int x = this.endian ? 4 - i - 1 : i;
                    val |= ((long) data[offset + i] & 255L) << x * 8;
                }

                return val;
            }
        }

        public long bytes2long(byte[] data) {
            return this.bytes2long(data, 0);
        }

        public long bytes2long(byte[] data, int offset) {
            if (offset + 8 > data.length) {
                return 0L;
            } else {
                long val = 0L;

                for (int i = 0; i < 8; ++i) {
                    int x = this.endian ? 8 - i - 1 : i;
                    val |= ((long) data[offset + i] & 255L) << x * 8;
                }

                return val;
            }
        }

        public long bytes2ulong(byte[] data) {
            return this.bytes2ulong(data, 0);
        }

        public long bytes2ulong(byte[] data, int offset) {
            return this.bytes2long(data, offset);
        }

        public float bytes2float(byte[] data) {
            return this.bytes2float(data, 0);
        }

        public float bytes2float(byte[] data, int offset) {
            if (offset + 4 > data.length) {
                return 0.0F;
            } else {
                int val = 0;

                for (int i = 0; i < 4; ++i) {
                    int x = this.endian ? 4 - i - 1 : i;
                    val |= (data[offset + i] & 255) << x * 8;
                }

                return Float.intBitsToFloat(val);
            }
        }

        public double bytes2double(byte[] data) {
            return this.bytes2double(data, 0);
        }

        public double bytes2double(byte[] data, int offset) {
            if (offset + 8 > data.length) {
                return 0.0D;
            } else {
                long val = 0L;

                for (int i = 0; i < 8; ++i) {
                    int x = this.endian ? 8 - i - 1 : i;
                    val |= ((long) data[offset + i] & 255L) << x * 8;
                }

                return Double.longBitsToDouble(val);
            }
        }

        public byte[] cutbytes(byte[] data, int offset, int length) {
            return ToolBytes.cutBytes(data, offset, length);
        }
    }
}
