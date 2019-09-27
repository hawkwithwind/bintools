package com.webot.bintools.tools;

import java.io.ByteArrayInputStream;

public class PbTools {
    public static int parseVarint(ByteArrayInputStream bs) {
        int k = 0;
        int i = 0;
        while (true) {
            final int b = bs.read();
            if(b == -1) {
                return -1;
            }
            

            if((b & 0x80) > 0) {
                k = k | ((b & 0x7f) << (7*i));
            } else {
                return k | (b<<(7*i));
            }
            
            i += 1;
        }
    }

    public static String parse(ByteArrayInputStream bs, String tag) {
        String ret = "";
        while(true) {
            int header = parseVarint(bs);
            if(header == -1) {
                break;
            }
            
            int type = header & 0x7;
            int seq = header >> 3;
            switch(type) {
            case 0:
                int value = parseVarint(bs);
                ret += String.format("%s [%d] varint %d \n", tag, seq, value);
                break;
            case 2:
                int len = parseVarint(bs);
                byte[] buffer = new byte[len];
                bs.read(buffer, 0, len);
                ret += String.format("%s [%d] embedded [%d] %s \n",
                                     tag, seq, len, DecodeTools.encodeHex(buffer));
                break;
            default:
                ret += String.format("%s [%d] unknown type %d \n",
                                     tag, seq, type);
                break;
            }
            
        }

        return ret;
    }
}
