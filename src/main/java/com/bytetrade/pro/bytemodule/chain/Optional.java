package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container template class used whenever we have an optional field.
 *
 * The idea here is that the binary serialization of this field should be performed
 * in a specific way determined by the field implementing the {@link ByteSerializable}
 * interface, more specifically using the {@link ByteSerializable#toBytes()} method.
 *
 * However, if the field is missing, the Optional class should be able to know how
 * to serialize it, as this is always done by placing an zero byte.
 */
public class Optional<T extends ByteSerializable> implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    public T optionalField;

    public Optional(T field){
        optionalField = field;
    }

    public boolean isNull() {
        return (optionalField == null);
    }

    @Override
    public List<Byte> toBytes() {
        if(optionalField == null) {
            return Util.convertBytes(new byte[]{(byte) 0});
        } else {
            List<Byte> a = new ArrayList<Byte>();
            a.addAll(Util.convertBytes(new byte[]{(byte) 1}));
            a.addAll(optionalField.toBytes());
            return a;
        }
    }

    public boolean isSet(){
        return this.optionalField != null;
    }


    @Override
    public String toString() {
        if(optionalField == null) {
            return "null";
        } else {
            return optionalField.toString();
        }
    }

}
