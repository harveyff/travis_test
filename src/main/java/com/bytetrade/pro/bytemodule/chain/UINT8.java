package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

public class UINT8 extends Uint implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    public static final UINT8 DEFAULT = new UINT8(BigInteger.ZERO);

    public UINT8(BigInteger value) {
        super(8, value);
    }


    public List<Byte> toBytes(){
        ByteBuffer b = ByteBuffer.allocate(1);

        b.put(value.byteValue());
        //b.putShort(Short.reverseBytes(value.shortValue()));

        return Util.convertBytes(b.array());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
