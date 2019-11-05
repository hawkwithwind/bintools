//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.proto;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class ProtoWriter {
    private static final int BUFF_SIZE = 1024;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

    public ProtoWriter() {
    }

    public void writeTag(int field, int type) {
        this.writeUInt32NoTag(ProtoData.makeTag(field, type));
    }

    public void writeInt32(int field, int value) {
        this.writeTag(field, 0);
        this.writeInt32NoTag(value);
    }

    public void writeInt32(int field, ProtoList<Integer> list) {
        Iterator var3;
        Integer value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeInt32NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeTag(field, 0);
                this.writeInt32NoTag(value);
            }
        }

    }

    public void writeSInt32(int field, int value) {
        this.writeUInt32(field, ProtoData.encodeZigZag32(value));
    }

    public void writeSInt32(int field, ProtoList<Integer> list) {
        Iterator var3;
        Integer value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeUInt32NoTag(ProtoData.encodeZigZag32(value));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeTag(field, 0);
                this.writeUInt32NoTag(ProtoData.encodeZigZag32(value));
            }
        }

    }

    public void writeUInt32(int field, int value) {
        this.writeTag(field, 0);
        this.writeUInt32NoTag(value);
    }

    public void writeUInt32(int field, ProtoList<Integer> list) {
        Iterator var3;
        Integer value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeUInt32NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeTag(field, 0);
                this.writeUInt32NoTag(value);
            }
        }

    }

    public void writeFixed32(int field, int value) {
        this.writeTag(field, 5);
        this.writeFixed32NoTag(value);
    }

    public void writeFixed32(int field, ProtoList<Integer> list) {
        Iterator var3;
        Integer value;
        if (list.packed) {
            this.writeTag(field, 5);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeFixed32NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Integer) var3.next();
                this.writeTag(field, 5);
                this.writeFixed32NoTag(value);
            }
        }

    }

    public void writeSFixed32(int field, int value) {
        this.writeFixed32(field, value);
    }

    public void writeSFixed32(int field, ProtoList<Integer> list) {
        this.writeFixed32(field, list);
    }

    public void writeInt64(int field, long value) {
        this.writeTag(field, 0);
        this.writeInt64NoTag(value);
    }

    public void writeInt64(int field, ProtoList<Long> list) {
        Iterator var3;
        Long value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeInt64NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeTag(field, 0);
                this.writeInt64NoTag(value);
            }
        }

    }

    public void writeUInt64(int field, long value) {
        this.writeTag(field, 0);
        this.writeUInt64NoTag(value);
    }

    public void writeUInt64(int field, ProtoList<Long> list) {
        Iterator var3;
        Long value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeUInt64NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeTag(field, 0);
                this.writeUInt64NoTag(value);
            }
        }

    }

    public void writeSInt64(int field, long value) {
        this.writeUInt64(field, ProtoData.encodeZigZag64(value));
    }

    public void writeSInt64(int field, ProtoList<Long> list) {
        Iterator var3;
        Long value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeUInt64NoTag(ProtoData.encodeZigZag64(value));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeTag(field, 0);
                this.writeUInt64NoTag(ProtoData.encodeZigZag64(value));
            }
        }

    }

    public void writeFixed64(int field, long value) {
        this.writeTag(field, 1);
        this.writeFixed64NoTag(value);
    }

    public void writeFixed64(int field, ProtoList<Long> list) {
        Iterator var3;
        Long value;
        if (list.packed) {
            this.writeTag(field, 1);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeFixed64NoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Long) var3.next();
                this.writeTag(field, 1);
                this.writeFixed64NoTag(value);
            }
        }

    }

    public void writeSFixed64(int field, long value) {
        this.writeFixed64(field, value);
    }

    public void writeSFixed64(int field, ProtoList<Long> list) {
        this.writeFixed64(field, list);
    }

    public void writeFloat(int field, float value) {
        this.writeFixed32(field, Float.floatToRawIntBits(value));
    }

    public void writeFloat(int field, ProtoList<Float> list) {
        Iterator var3;
        Float value;
        if (list.packed) {
            this.writeTag(field, 5);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Float) var3.next();
                this.writeFixed32NoTag(Float.floatToRawIntBits(value));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Float) var3.next();
                this.writeTag(field, 5);
                this.writeFixed32NoTag(Float.floatToRawIntBits(value));
            }
        }

    }

    public void writeDouble(int field, double value) {
        this.writeFixed64(field, Double.doubleToRawLongBits(value));
    }

    public void writeDouble(int field, ProtoList<Double> list) {
        Iterator var3;
        Double value;
        if (list.packed) {
            this.writeTag(field, 1);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Double) var3.next();
                this.writeFixed64NoTag(Double.doubleToRawLongBits(value));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Double) var3.next();
                this.writeTag(field, 1);
                this.writeFixed64NoTag(Double.doubleToRawLongBits(value));
            }
        }

    }

    public void writeBool(int field, boolean value) {
        this.writeTag(field, 0);
        this.write((byte) (value ? 1 : 0));
    }

    public void writeBool(int field, ProtoList<Boolean> list) {
        Iterator var3;
        Boolean value;
        if (list.packed) {
            this.writeTag(field, 0);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Boolean) var3.next();
                this.write((byte) (value ? 1 : 0));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (Boolean) var3.next();
                this.writeTag(field, 0);
                this.write((byte) (value ? 1 : 0));
            }
        }

    }

    public void writeEnum(int field, int value) {
        this.writeInt32(field, value);
    }

    public void writeEnum(int field, ProtoList<Integer> list) {
        this.writeInt32(field, list);
    }

    public void writeString(int field, String value) {
        if (value != null) {
            this.writeByteArray(field, value.getBytes(ProtoData.CHARSET));
        }

    }

    public void writeString(int field, ProtoList<String> list) {
        Iterator var3;
        String value;
        if (list.packed) {
            this.writeTag(field, 2);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (String) var3.next();
                this.writeByteArrayNoTag(value.getBytes(ProtoData.CHARSET));
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (String) var3.next();
                this.writeTag(field, 2);
                this.writeByteArrayNoTag(value.getBytes(ProtoData.CHARSET));
            }
        }

    }

    public void writeByteArray(int field, byte[] value) {
        this.writeTag(field, 2);
        this.writeByteArrayNoTag(value);
    }

    public void writeByteArray(int field, ProtoList<byte[]> list) {
        Iterator var3;
        byte[] value;
        if (list.packed) {
            this.writeTag(field, 2);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (byte[]) var3.next();
                this.writeByteArrayNoTag(value);
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (byte[]) var3.next();
                this.writeTag(field, 2);
                this.writeByteArrayNoTag(value);
            }
        }

    }

    public <T extends ProtoMessage> void writeMessage(int field, T value) {
        this.writeTag(field, 2);
        this.writeByteArrayNoTag(value.build());
    }

    public <T extends ProtoMessage> void writeMessage(int field, ProtoList<T> list) {
        Iterator var3;
        ProtoMessage value;
        if (list.packed) {
            this.writeTag(field, 2);
            this.writeUInt32NoTag(list.size());
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (ProtoMessage) var3.next();
                this.writeByteArrayNoTag(value.build());
            }
        } else {
            var3 = list.iterator();

            while (var3.hasNext()) {
                value = (ProtoMessage) var3.next();
                this.writeTag(field, 2);
                this.writeByteArrayNoTag(value.build());
            }
        }

    }

    public void write(byte value) {
        this.out.write(value);
    }

    public void writeByteArrayNoTag(byte[] value) {
        this.writeUInt32NoTag(value.length);
        this.out.write(value, 0, value.length);
    }

    public void writeInt32NoTag(int value) {
        if (value >= 0) {
            this.writeUInt32NoTag(value);
        } else {
            this.writeUInt64NoTag((long) value);
        }

    }

    public void writeInt64NoTag(long value) {
        this.writeUInt64NoTag(value);
    }

    public void writeUInt32NoTag(int value) {
        while ((value & -128) != 0) {
            this.out.write((byte) (value & 127 | 128));
            value >>>= 7;
        }

        this.out.write((byte) value);
    }

    public void writeFixed32NoTag(int value) {
        byte[] b = ProtoData.int2bytes(value);
        this.out.write(b, 0, b.length);
    }

    public void writeUInt64NoTag(long value) {
        while ((value & -128L) != 0L) {
            this.out.write((byte) ((int) value & 127 | 128));
            value >>>= 7;
        }

        this.out.write((byte) ((int) value));
    }

    public void writeFixed64NoTag(long value) {
        byte[] b = ProtoData.long2bytes(value);
        this.out.write(b, 0, b.length);
    }

    public void reset() {
        this.out.reset();
    }

    public byte[] finish() {
        return this.out.toByteArray();
    }
}
