package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.io.Serializable;
import java.util.List;

public class AccountID implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    String name;

    public AccountID() {
        this.name = "";
    }

    public AccountID(String name) {
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
