package com.bytetrade.pro.bytemodule.chain.operations;

import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.OperationType;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.UINT32;
import com.bytetrade.pro.bytemodule.chain.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//import cy.agorise.graphenej.objects.Memo;

/**
 * Class used to encapsulate the TransferOperation operation related functionalities.
 */
public class TransferOperation extends BaseOperation {

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";
    public static final String KEY_MEMO = "memo";

    public TransferOperation(JSONObject o) {
        super(OperationType.TRANSFER_OPERATION);

        try {
            fee = new UINT128(Util.getBigInteger(o,"fee"));
            from = new AccountID(o.getString("from"));
            to = new AccountID(o.getString("to"));
            asset_type = new UINT32(Util.getBigInteger(o,"asset_type"));
            amount = new UINT128(Util.getBigInteger(o,"amount"));


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UINT128 fee;
    public AccountID from;
    public AccountID to;
    public UINT32 asset_type;
    public UINT128 amount;

    public TransferOperation(){
        super(OperationType.TRANSFER_OPERATION);

    }

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
            jsonObject.put("from", from.toString());
            jsonObject.put("to", to.toString());
            jsonObject.put("asset_type", asset_type.toString());
            jsonObject.put("amount", amount.toString());
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
        byteArray.addAll(  this.from.toBytes() );
        byteArray.addAll(  this.to.toBytes() );
        byteArray.addAll(  this.asset_type.toBytes());
        byteArray.addAll(  this.amount.toBytes());

        return byteArray;
    }

}
