package com.bytetrade.pro.bytemodule.chain;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;
import com.bytetrade.pro.bytemodule.chain.interfaces.JsonSerializable;

/**
 * Created by nelson on 11/5/16.
 */
public abstract class BaseOperation implements ByteSerializable, JsonSerializable {


    public static final String KEY_FEE = "fee";
    //public static final String KEY_EXTENSIONS = "extensions";

    protected OperationType type;
    //protected Extensions extensions;

    public BaseOperation(OperationType type){
        this.type = type;
        //  this.extensions = new Extensions();
    }

    public BaseOperation(JSONObject o) {
        
    }

    public byte getId() {
        return (byte) this.type.ordinal();
    }

    public JSONArray toJsonObject() {
        JSONArray array = new JSONArray();
        array.put(this.getId());
        return array;
    }

}
