package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

public class UINT32 extends Uint implements ByteSerializable {

    public static final UINT32 DEFAULT = new UINT32(BigInteger.ZERO);

    public UINT32(BigInteger value) {
        super(32, value);
    }


    public List<Byte> toBytes(){
        ByteBuffer b = ByteBuffer.allocate(4);

        b.putInt(Integer.reverseBytes(value.intValue()));

        return Util.convertBytes(b.array());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
