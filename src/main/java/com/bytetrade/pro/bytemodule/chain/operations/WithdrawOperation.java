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
public class WithdrawOperation extends BaseOperation {

    public WithdrawOperation(JSONObject o) {
        super(OperationType.WITHDRAW_OPERATION);

        try {
            fee = new UINT128(Util.getBigInteger(o,"fee"));
            from = new AccountID(o.getString("from"));
            toExternalAddress = o.getString("to_external_address");
            assetType = new UINT32(Util.getBigInteger(o,"asset_type"));
            amount = new UINT128(Util.getBigInteger(o,"amount"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UINT128 fee;
    public AccountID from;
    public String toExternalAddress;
    public UINT32 assetType;
    public UINT128 amount;

    public WithdrawOperation(){
        super(OperationType.WITHDRAW_OPERATION);

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
            jsonObject.put("to_external_address", toExternalAddress.toString());
            jsonObject.put("asset_type", assetType.toString());
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
        byteArray.addAll(  Util.convertStringToBytes(this.toExternalAddress) );
        byteArray.addAll(  this.assetType.toBytes());
        byteArray.addAll(  this.amount.toBytes());

        return byteArray;
    }

}
