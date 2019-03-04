package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

public class UINT64 extends Uint implements ByteSerializable {

    public static final UINT64 DEFAULT = new UINT64(BigInteger.ZERO);

    public UINT64(BigInteger value) {
        super(64, value);
    }


    public List<Byte> toBytes(){
        ByteBuffer b = ByteBuffer.allocate(8);

        b.putLong(Long.reverseBytes(value.longValue()));

        return Util.convertBytes(b.array());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
