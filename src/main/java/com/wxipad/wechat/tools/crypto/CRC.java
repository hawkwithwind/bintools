package com.wxipad.wechat.tools.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

public class CRC {
    private static final int DEFAULT_BUFF_SIZE = 1024;
    private final CRC32 crc;

    public CRC() {
        this.crc = new CRC32();
    }

    public static long encode(byte[] data) {
        return new CRC().update(data).value();
    }

    public static long encode(InputStream in)
            throws IOException {
        CRC c = new CRC();
        byte[] data = new byte['Ѐ'];
        int read;
        while ((read = in.read(data)) != -1) {
            c.update(data, 0, read);
        }
        return c.value();
    }

    public CRC update(byte input) {
        this.crc.update(input);
        return this;
    }

    public CRC update(byte[] input) {
        this.crc.update(input);
        return this;
    }

    public CRC update(byte[] input, int offset, int len) {
        this.crc.update(input, offset, len);
        return this;
    }

    public long value() {
        return this.crc.getValue();
    }
}
