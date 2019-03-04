package com.bytetrade.pro.bytemodule.chain;


import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.util.List;

public class ByteString implements ByteSerializable {

    String name;

    public ByteString() {
        this.name = "";
    }

    public ByteString(String name) {
       this.name = name;
    }


    public List<Byte> toBytes(){
        return Util.convertStringToBytes(name);
    }

    @Override
    public String toString() {
         return name;
    }

}
