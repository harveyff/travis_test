package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OpWrapper implements ByteSerializable {

    public BaseOperation op;

    public OpWrapper(BaseOperation o) {
        op = o;
    }

    public OpWrapper(JSONArray o) {
        try {
            int i = o.getInt(0);
            op = Util.getOperation(i,o.getJSONObject(1));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Byte> toBytes(){
        List<Byte> byteArray = new ArrayList<Byte>();
        byteArray.add(op.getId());
        byteArray.addAll(op.toBytes());

        return byteArray;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }


    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();

        try{
            // JSONArray operationsArray = new JSONArray();
            // operationsArray.put(0,);
            // Adding operations
            obj.put("op", op.toJsonObject());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
