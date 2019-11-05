package com.wxipad.wechat.tools.tool;

import java.io.*;
import java.nio.charset.Charset;

public class ToolStream {
    public static final int DEFAULT_BUFF_SIZE = 1024;

    public static byte[] needBytes(InputStream in, int length)
            throws IOException {
        if (length <= 0) {
            return new byte[0];
        }
        byte[] buff = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = in.read(buff, offset, length - offset);
            if (read <= 0) {
                return null;
            }
            offset += read;
        }
        return buff;
    }

    public static byte[] getBytes(InputStream in)
            throws IOException {
        return getBytes(in, 1024);
    }

    public static byte[] getBytes(InputStream in, int size)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            pipe(in, out, size);
            return out.toByteArray();
        } finally {
            out.close();
        }
    }

    public static void pipe(InputStream in, OutputStream out)
            throws IOException {
        pipe(in, out, 1024);
    }

    public static void pipe(InputStream in, OutputStream out, int size)
            throws IOException {
        byte[] buff = new byte[size];
        int read;
        while ((read = in.read(buff)) != -1) {
            out.write(buff, 0, read);
        }
    }

    public static String read(InputStream in)
            throws IOException {
        return read(in, Charset.defaultCharset());
    }

    public static String read(InputStream in, String charsetName)
            throws IOException {
        return read(in, Charset.forName(charsetName));
    }

    public static String read(InputStream in, Charset charset)
            throws IOException {
        charset = charset == null ? Charset.defaultCharset() : charset;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        StringBuilder sb = new StringBuilder();
        int ch = reader.read();
        while (ch != -1) {
            sb.append((char) ch);
            ch = reader.read();
        }
        return sb.toString();
    }

    public static void write(OutputStream out, String str)
            throws IOException {
        write(out, str, Charset.defaultCharset());
    }

    public static void write(OutputStream out, String str, String charsetName)
            throws IOException {
        write(out, str, Charset.forName(charsetName));
    }

    public static void write(OutputStream out, String str, Charset charset)
            throws IOException {
        charset = charset == null ? Charset.defaultCharset() : charset;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset));
        writer.write(str, 0, str.length());
    }
}
