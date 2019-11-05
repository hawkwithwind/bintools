package com.wxipad.wechat.tools.proto;

import java.io.IOException;

public class ProtoException
        extends IOException {
    public ProtoException(String message) {
        super(message);
    }

    public static ProtoException byteArrayReachEnd() {
        return new ProtoException("byte array reach end");
    }

    public static ProtoException varintLengthOverflow() {
        return new ProtoException("varint length overflow");
    }

    public static ProtoException dataLengthNegative() {
        return new ProtoException("data length negative");
    }
}
