//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.proto;

public abstract class ProtoMessage implements ProtoReader.Callback {
    public ProtoMessage() {
    }

    public boolean packed(int _field_) throws ProtoException {
        return false;
    }

    public byte[] build() {
        ProtoWriter writer = new ProtoWriter();
        this.write(writer);
        return writer.finish();
    }

    public void unknown(ProtoReader _reader_, int _field_, int _type_) throws ProtoException {
        int skip = 0;
        if (_type_ == 0) {
            skip = _reader_.testVarintLength();
        } else if (_type_ == 1) {
            skip = 8;
        } else if (_type_ == 2) {
            skip = _reader_.readRawVarint32();
        } else if (_type_ != 3 && _type_ != 4 && _type_ == 5) {
            skip = 4;
        }

        _reader_.skipRawBytes(skip);
    }

    public abstract void write(ProtoWriter var1);
}
