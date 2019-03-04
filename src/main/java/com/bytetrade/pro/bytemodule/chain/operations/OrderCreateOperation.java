package com.bytetrade.pro.bytemodule.chain.operations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.OperationType;
import com.bytetrade.pro.bytemodule.chain.Optional;
import com.bytetrade.pro.bytemodule.chain.TimeSpec;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.UINT8;
import com.bytetrade.pro.bytemodule.chain.Util;

public class OrderCreateOperation extends BaseOperation {

    // Number of bytes used for the expiration field.

    private final int EXPIRATION_BYTE_LENGTH = 4;

    // Constants used in the JSON representation
//    public static final String KEY_SELLER = "seller";
//    public static final String KEY_AMOUNT_TO_SELL = "amount_to_sell";
//    public static final String KEY_MIN_TO_RECEIVE = "min_to_receive";
//    public static final String KEY_EXPIRATION = "expiration";
//    public static final String KEY_FILL_OR_KILL = "fill_or_kill";

    // Inner fields of a limit order

    public OrderCreateOperation(JSONObject o) {

        super(OperationType.ORDER_CREATE_OPERATION);

        
        try {
            fee = new UINT128(Util.getBigInteger(o, "fee"));
        
            creator = new AccountID(o.getString("creator"));
            side = new UINT8(Util.getBigInteger(o, "side"));
        
            order_type = new UINT8(Util.getBigInteger(o, "order_type"));
            market_name = o.getString("market_name");
            amount = new UINT128(Util.getBigInteger(o, "amount"));
            price = new UINT128(Util.getBigInteger(o, "price"));
        

            use_btt_as_fee = o.getBoolean("use_btt_as_fee");
            if (o.has("freeze_btt_fee")) {
                freeze_btt_fee = new Optional<UINT128>(new UINT128(Util.getBigInteger(o, "freeze_btt_fee")));
            } else {
                freeze_btt_fee = new Optional<UINT128>(null);
            }

            now = Util.getTimeSpec(o.getString("now"));
        

            expiration = Util.getTimeSpec(o.getString("expiration"));

        } catch (Exception e) {
        
            e.printStackTrace();
        }
    }

    public UINT128 fee;
    public AccountID creator;
    public UINT8 side;
    public UINT8 order_type;
    public String market_name;
    public UINT128 amount;
    public UINT128 price;
    public Boolean use_btt_as_fee;
    public Optional<UINT128> freeze_btt_fee;
    public TimeSpec now;
    public TimeSpec expiration;

    public OrderCreateOperation() {
        super(OperationType.ORDER_CREATE_OPERATION);
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
            jsonObject.put("creator", creator.toString());
            jsonObject.put("side", side.toString());
            jsonObject.put("order_type", order_type.toString());
            jsonObject.put("market_name", market_name.toString());
            jsonObject.put("amount", amount.toString());
            jsonObject.put("price", price.toString());

            jsonObject.put("use_btt_as_fee", use_btt_as_fee ? "true" : "false");
            if (this.freeze_btt_fee != null && this.freeze_btt_fee.isNull() == false) {
                jsonObject.put("freeze_btt_fee", freeze_btt_fee.toString());
            }

            jsonObject.put("now", now.toString());
            jsonObject.put("expiration", expiration.toString());

            array.put(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }


    @Override
    public List<Byte> toBytes() {

        List<Byte> byteArray = new ArrayList<Byte>();
        byteArray.addAll(this.fee.toBytes());
        byteArray.addAll(this.creator.toBytes());
        byteArray.addAll(this.side.toBytes());
        byteArray.addAll(this.order_type.toBytes());
        byteArray.addAll(Util.convertStringToBytes(this.market_name));
        byteArray.addAll(this.amount.toBytes());
        byteArray.addAll(this.price.toBytes());
        byteArray.addAll(Util.convertBytes(this.use_btt_as_fee ? new byte[]{0x1} : new byte[]{0x0}));
        byteArray.addAll(this.freeze_btt_fee.toBytes());
        byteArray.addAll(this.now.toBytes());
        byteArray.addAll(this.expiration.toBytes());

//        ByteBuffer buffer1 = ByteBuffer.allocate(EXPIRATION_BYTE_LENGTH);
//        buffer1.putInt(this.now);
//        byte[] nowBytes = Util.revertBytes(buffer1.array());
//
//        ByteBuffer buffer = ByteBuffer.allocate(EXPIRATION_BYTE_LENGTH);
//        buffer.putInt(this.expiration);
//        byte[] expirationBytes = Util.revertBytes(buffer.array());

//        byte[] b = new byte[byteArray.size()];
//        for( int i = 0; i < byteArray.size(); ++i ) {
//            b[i] = byteArray.get(i);
//        }
//
//        return b;

        return byteArray;
    }

}
