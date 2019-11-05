package com.wxipad.wechat.tools.proto;

import java.util.ArrayList;

public class ProtoList<T>
        extends ArrayList<T> {
    public final boolean packed;

    public ProtoList(boolean packed) {
        this.packed = packed;
    }

    public T fetch(int index) {
        if ((index >= 0) && (index < size())) {
            return (T) get(index);
        }
        return null;
    }
}
