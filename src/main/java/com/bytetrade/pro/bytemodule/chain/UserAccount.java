package com.bytetrade.pro.bytemodule.chain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

/**
 * Class that represents a graphene user account.
 */
public class UserAccount implements ByteSerializable {

    public static final String KEY_NAME = "id";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_ACTIVE = "active";

    private String id;

    private Authority owner;

    private Authority active;

    public UserAccount(String name) {
        this.id = name;
    }

    public Authority parseAutyority(JSONObject o) {

        Authority a = new Authority();

        try {

            HashMap<Address, Long> address_auths = new HashMap<Address, Long>();
            a.setWeightThreshold(o.getLong("weight_threshold"));
            JSONArray array = o.getJSONArray("address_auths");
            for (int i = 0; i < array.length(); ++i) {
                String add = array.getJSONArray(i).getString(0);
                
                Address ad = new Address(add.trim());
                address_auths.put(ad, array.getJSONArray(i).getLong(1));
            }
            a.setAddressAuthorities(address_auths);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;

    }

    public UserAccount(JSONObject o) {
        try {
            id = o.getString("id");
            owner = parseAutyority(o.getJSONObject("owner"));
            active = parseAutyority(o.getJSONObject("active"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the account name field.
     *
     * @return: The name of this account.
     */
    public String getName() {
        return id;
    }


    /**
     * Setter for the account name field.
     *
     * @param id: The account name.
     */
    public void setName(String id) {
        this.id = id;
    }

//    @Override
//    public boolean equals(Object o) {
//        return this.getObjectId().equals(((UserAccount)o).getObjectId());
//    }
//
//    @Override
//    public int hashCode() {
//        return this.getObjectId().hashCode();
//    }

    public String getObjectId() {
        return id;
    }

    @Override
    public List<Byte> toBytes() {

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        DataOutput out = new DataOutputStream(byteArrayOutputStream);
//        try {
//            //Varint.writeUnsignedVarLong(this.instance, out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return byteArrayOutputStream.toByteArray();

        List<Byte> bytes = new ArrayList<>();
        return bytes;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }


    public JSONObject toJsonObject() {
        JSONObject authority = new JSONObject();
        try {
            authority.put("id", id);
            authority.put("owner", owner.toJsonObject());
            authority.put("active", active.toJsonObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authority;
    }

    @Override
    public String toString() {
        return this.toJsonString();
    }

    public Authority getOwner() {
        return owner;
    }

    public void setOwner(Authority owner) {
        this.owner = owner;
    }

    public Authority getActive() {
        return active;
    }

    public void setActive(Authority active) {
        this.active = active;
    }


}
