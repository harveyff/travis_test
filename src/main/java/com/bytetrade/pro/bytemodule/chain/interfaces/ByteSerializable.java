package com.bytetrade.pro.bytemodule.chain.interfaces;

import java.util.List;

/**
 * Interface implemented by all entities for which makes sense to have a byte-array representation.
 */
public interface ByteSerializable {

    List<Byte> toBytes();

}
