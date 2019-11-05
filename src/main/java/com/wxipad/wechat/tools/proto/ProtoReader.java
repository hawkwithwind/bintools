package com.wxipad.wechat.tools.proto;

public class ProtoReader {
    private final byte[] data;
    private int cursor;

    public ProtoReader(byte[] data) {
        this.data = data;
        this.cursor = 0;
    }

    public double readDouble() throws ProtoException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    public float readFloat() throws ProtoException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    public long readUInt64() throws ProtoException {
        return readRawVarint64();
    }

    public long readInt64() throws ProtoException {
        return readRawVarint64();
    }

    public int readInt32() throws ProtoException {
        return readRawVarint32();
    }

    public long readFixed64() throws ProtoException {
        return readRawLittleEndian64();
    }

    public int readFixed32() throws ProtoException {
        return readRawLittleEndian32();
    }

    public boolean readBool() throws ProtoException {
        return readRawVarint64() != 0L;
    }

    public String readString() throws ProtoException {
        int length = readRawVarint32();
        if (length == 0)
            return "";
        if (length < 0) {
            throw ProtoException.dataLengthNegative();
        }
        byte[] bytes = readRawBytes(length);
        return new String(bytes, ProtoData.CHARSET);
    }

    public byte[] readByteArray() throws ProtoException {
        return readRawBytes(readRawVarint32());
    }

    public int readUInt32() throws ProtoException {
        return readRawVarint32();
    }

    public int readEnum() throws ProtoException {
        return readRawVarint32();
    }

    public int readSFixed32() throws ProtoException {
        return readRawLittleEndian32();
    }

    public long readSFixed64() throws ProtoException {
        return readRawLittleEndian64();
    }

    public int readSInt32() throws ProtoException {
        return ProtoData.decodeZigZag32(readRawVarint32());
    }

    public long readSInt64() throws ProtoException {
        return ProtoData.decodeZigZag64(readRawVarint64());
    }

    public int readRawVarint32() throws ProtoException {
        int len = testVarintLength();
        if (len > 10)
            throw ProtoException.varintLengthOverflow();
        if (len > 5) {
            return (int) readRawVarint64();
        }
        int value = 0;
        for (int i = this.cursor + len - 1; i >= this.cursor; i--) {
            value <<= 7;
            value |= this.data[i] & 0x7F;
        }
        this.cursor += len;
        return value;
    }

    public long readRawVarint64() throws ProtoException {
        int len = testVarintLength();
        if (len > 10) {
            throw ProtoException.varintLengthOverflow();
        }
        long value = 0L;
        for (int i = this.cursor + len - 1; i >= this.cursor; i--) {
            value <<= 7;
            value |= this.data[i] & 0x7F;
        }
        this.cursor += len;
        return value;
    }

    public int testVarintLength() throws ProtoException {
        int pos = this.cursor;
        for (; ; ) {
            if (pos < this.data.length) {
                if ((this.data[pos] & 0xFFFFFF80) == 0) {
                    break;
                }
            } else {
                throw ProtoException.byteArrayReachEnd();
            }
            pos++;
        }
        return pos - this.cursor + 1;
    }

    public int readRawLittleEndian32() throws ProtoException {
        if (this.cursor + 4 > this.data.length) {
            throw ProtoException.byteArrayReachEnd();
        }
        int value = ProtoData.bytes2int(this.data, this.cursor);
        this.cursor += 4;
        return value;
    }

    public long readRawLittleEndian64() throws ProtoException {
        if (this.cursor + 8 > this.data.length) {
            throw ProtoException.byteArrayReachEnd();
        }
        long value = ProtoData.bytes2long(this.data, this.cursor);
        this.cursor += 8;
        return value;
    }

    public byte[] readRawBytes(int length) throws ProtoException {
        if (length < 0)
            throw ProtoException.dataLengthNegative();
        if (length == 0) {
            return new byte[0];
        }
        if (this.cursor + length > this.data.length) {
            throw ProtoException.byteArrayReachEnd();
        }
        byte[] bytes = new byte[length];
        System.arraycopy(this.data, this.cursor, bytes, 0, length);
        this.cursor += length;
        return bytes;
    }

    public void skipRawBytes(int length) throws ProtoException {
        if (length < 0)
            throw ProtoException.dataLengthNegative();
        if (length == 0) {
            return;
        }
        if (this.cursor + length > this.data.length) {
            throw ProtoException.byteArrayReachEnd();
        }
        this.cursor += length;
    }

    public void solve(Callback callback) throws ProtoException {
        while (this.cursor < this.data.length) {
            int tag = readRawVarint32();
            int field = ProtoData.getTagFieldNumber(tag);
            int type = ProtoData.getTagWireType(tag);
            if (callback.packed(field)) {
                int length = readRawVarint32();
                for (int i = 0; i < length; i++) {
                    callback.read(this, field, type);
                }
            } else {
                callback.read(this, field, type);
            }
        }
    }

    public static abstract interface Callback {
        public abstract boolean packed(int paramInt)
                throws ProtoException;

        public abstract void read(ProtoReader paramProtoReader, int paramInt1, int paramInt2)
                throws ProtoException;
    }
}
