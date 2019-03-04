package com.bytetrade.pro.bytemodule.chain.operations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.OpWrapper;
import com.bytetrade.pro.bytemodule.chain.OperationType;
import com.bytetrade.pro.bytemodule.chain.TimeSpec;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.Util;

/**
 * Class used to encapsulate the TransferOperation operation related functionalities.
 */
public class ProposeOperation extends BaseOperation {

    public ProposeOperation(JSONObject o) {
        super(OperationType.proposal_OPERATION);
        try {
            fee = new UINT128(Util.getBigInteger(o,"fee"));
            proposaler = new AccountID(o.getString("proposaler"));
            JSONArray o_array = o.getJSONArray("proposed_ops");
            for( int i = 0; i < o_array.length(); ++i ) {
                //JSONArray array = o_array.getJSONArray(i);
                JSONObject op = o_array.getJSONObject(i);
              
                JSONArray array = op.getJSONArray("op");
              
                proposed_ops.add(new OpWrapper(array));
            }
            expiration_time = Util.getTimeSpec( o.getString("expiration_time"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UINT128 fee;
    public AccountID proposaler;
    public ArrayList<OpWrapper> proposed_ops = new ArrayList<>();
    public TimeSpec expiration_time;

    public ProposeOperation(){
        super(OperationType.proposal_OPERATION);

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
            jsonObject.put("proposaler", proposaler.toString());

            JSONArray operationsArray = new JSONArray();
            //for (BaseOperation operation : operations) {
            for( int i =0; i < proposed_ops.size(); ++i ) {
                operationsArray.put(i, proposed_ops.get(i).toJsonObject());
            }

//            JSONObject op = new JSONObject();
//            op.put("op",operationsArray);

            // Adding operations

            jsonObject.put("proposed_ops", operationsArray);

            jsonObject.put("expiration_time", expiration_time.toString());

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
        byteArray.addAll(  this.proposaler.toBytes() );
        byteArray.addAll(this.expiration_time.toBytes());

        byteArray.add((byte) this.proposed_ops.size());
        for( int i = 0; i < proposed_ops.size(); ++i ) {
            byteArray.addAll(proposed_ops.get(i).toBytes());
        }
        return byteArray;

    }

}
