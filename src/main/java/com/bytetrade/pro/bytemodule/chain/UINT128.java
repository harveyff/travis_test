package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UINT128 extends Uint implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    public static final UINT128 DEFAULT = new UINT128(BigInteger.ZERO);

    public UINT128(BigInteger value) {
        super(128, value);
    }

    public boolean isNull() {
        return value == null;
    }

    public List<Byte> toBytes() {

//      ByteBuffer b = ByteBuffer.allocate(16);

        BigInteger base = BigInteger.ONE;
        for (int i = 0; i < 64; ++i) {
            base = base.multiply(BigInteger.valueOf(2));
        }

//        LogUtils.i("MainActivity","" + value.toString());
//        LogUtils.i("MainActivity","" + base.toString());

        BigInteger high = value.divide(base);
        BigInteger low = value.mod(base);

        List<Byte> a = new ArrayList<>();

        ByteBuffer highBuffer = ByteBuffer.allocate(8);
        highBuffer.putLong(Long.reverseBytes(high.longValue()));
        a.addAll(Util.convertBytes(highBuffer.array()));

        ByteBuffer lowBuffer = ByteBuffer.allocate(8);
        lowBuffer.putLong(Long.reverseBytes(low.longValue()));
        a.addAll(Util.convertBytes(lowBuffer.array()));

//        LogUtils.i("MainActivity","" + high);
//        LogUtils.i("MainActivity","" + low);

//        b.putLong(0,);
//        b.putLong(8,Long.reverseBytes(low.longValue()));


        return a;
    }

    @Override
    public String toString() {
        return value.toString();
    }


}
