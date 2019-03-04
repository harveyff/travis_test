package com.bytetrade.pro.bytemodule.chain.operations;


import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.OperationType;
import com.bytetrade.pro.bytemodule.chain.Ripemd160;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelson on 3/21/17.
 */
public class OrderCancelOperation extends BaseOperation {

    // Constants used in the JSON representation
    public static final String KEY_FEE_PAYING_ACCOUNT = "fee_paying_account";
    public static final String KEY_ORDER_ID = "order";

    public OrderCancelOperation() {
        super(OperationType.ORDER_CANCEL_OPERATION);
    }

    public OrderCancelOperation(JSONObject o) {
        super(OperationType.ORDER_CANCEL_OPERATION);

        try {
            fee = new UINT128(Util.getBigInteger(o,"fee"));
            order_id = new Ripemd160(o.getString("order_id"));
            market_name = o.getString("market_name");
            creator = new AccountID(o.getString("creator"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner fields of a limit order cancel operation
    public UINT128 fee;
    public Ripemd160 order_id;
    public String market_name;
    public AccountID creator;

    @Override
    public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override
    public JSONArray toJsonObject() {
        JSONArray array = (JSONArray) super.toJsonObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fee", fee.toString());
            jsonObject.put("order_id", order_id.toString());
            jsonObject.put("market_name", market_name);
            jsonObject.put("creator", creator.toString());

            array.put(jsonObject);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    @Override
    public List<Byte> toBytes() {
        List<Byte> byteArray = new ArrayList<Byte>();
        byteArray.addAll(  this.fee.toBytes() );
        byteArray.addAll(  this.creator.toBytes() );
        byteArray.addAll(  Util.convertStringToBytes(this.market_name) );
        byteArray.addAll(  this.order_id.toBytes() );

        return byteArray;
    }

}
