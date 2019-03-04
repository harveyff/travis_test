package com.bytetrade.pro.bytemodule.chain;


import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.util.ArrayList;
import java.util.List;

public class Ripemd160 implements ByteSerializable {

    byte[] id;

    public Ripemd160(String tid) {
        id = Util.hexToBytes(tid);
        //this.id = tid;
    }


    public List<Byte> toBytes() {

        List<Byte> b  = new ArrayList<Byte>();
        //b.addAll( Bytes.asList((Varint.writeUnsignedVarInt(id.length))));
        //LogUtils.i("MainActivity","transfer " + id.getBytes().length);
        b.addAll(Util.convertBytes(id));
       // b.addAll(Util.convertStringToBytes(id));

        return b;
    }

    @Override
    public String toString() {
         return Util.bytesToHex(id);
    }
}
