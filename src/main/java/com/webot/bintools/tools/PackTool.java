package com.webot.bintools.tools;

import com.wxipad.wechat.tools.proto.ProtoData;
import com.wxipad.wechat.tools.proto.ProtoException;
import com.wxipad.wechat.tools.tool.ToolBytes;
import org.apache.tomcat.jni.Local;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PackTool {
    public static long bytesReaderReadVarint(ToolBytes.BytesReader reader, byte[] data) throws ProtoException {
        long ret = ProtoData.bytes2varint64(data, reader.getCursor());

        int skip = ProtoData.testVarintLength(data, reader.getCursor());
        reader.skip(skip);

        return ret;
    }

    public static Map parseHeader(String text) throws Exception {
        HashMap result = new LinkedHashMap();

        short temp;
        byte []data = DecodeTools.decodeHex(text);
        ToolBytes.BytesReader reader = new ToolBytes.BytesReader(data, ToolBytes.BIG_ENDIAN);

        if (data[0] == (byte) 0xbf) {
            reader.readUByte();//跳过协议标志位
        }

        temp = reader.readUByte();
        int headerLen = temp >> 2;
        result.put("headerLen", headerLen);

        boolean compress = (temp & 0x01) == 1;
        result.put("compressed", compress);

        temp = reader.readUByte();
        int encryptAlgo = temp >> 4;
        result.put("encryptAlgo", encryptAlgo);

        int cookieLen = temp & 0x0f;
        result.put("cookieLen", cookieLen);

        int version = reader.readInt();
        result.put("version", "0x" + Integer.toHexString(version).toUpperCase());

        long uin = reader.readUInt();
        result.put("uin", uin);

        byte[] cookieData = reader.readBytes(cookieLen);
        if (cookieLen > 0) {
            result.put("cookieData", DecodeTools.encodeHex(cookieData));
        } else {
            result.put("cookieData", "");
        }

        long cgi = bytesReaderReadVarint(reader, data);
        result.put("cgi", cgi);

        long originalDataLen = bytesReaderReadVarint(reader, data);
        result.put("originalDataLen", originalDataLen);

        long compressedDataLen = bytesReaderReadVarint(reader, data);
        result.put("compressedDataLen", compressedDataLen);

        long certVersion = bytesReaderReadVarint(reader, data);
        result.put("certVersion", certVersion);

        long deviceType = bytesReaderReadVarint(reader, data);
        result.put("deviceType", deviceType);

        long signature = bytesReaderReadVarint(reader, data);
        result.put("signature", signature);

        short flag = reader.readUByte();
        result.put("flag", "0x" + Integer.toHexString(flag).toUpperCase());

        long rqtData = bytesReaderReadVarint(reader, data);
        result.put("rqtData", "0x" + Long.toHexString(rqtData).toUpperCase());

        byte routeInfo = reader.readByte();
        result.put("routeInfo", routeInfo);

        return result;
    }
}
