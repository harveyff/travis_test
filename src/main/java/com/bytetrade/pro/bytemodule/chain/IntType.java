package com.bytetrade.pro.bytemodule.chain;

import java.math.BigInteger;

public abstract class IntType extends NumericType {

    protected int mBitSize;

    public IntType(String typePrefix, int bitSize, BigInteger value) {
        super(typePrefix + bitSize, value);
        if (!valid(bitSize, value)) {
            throw new UnsupportedOperationException(
                    "Bitsize must be 8 bit aligned, and in range 0 < bitSize <= 256");
        }
        mBitSize = bitSize;
    }

    boolean valid(int bitSize, BigInteger value) {
        return isValidBitSize(bitSize)
                && isValidBitCount(bitSize, value);
    }

    static boolean isValidBitSize(int bitSize) {
        return bitSize % 8 == 0
                && bitSize > 0
                && bitSize <= MAX_BIT_LENGTH;
    }

    private static boolean isValidBitCount(int bitSize, BigInteger value) {
        return value.bitLength() <= bitSize;
    }
}
