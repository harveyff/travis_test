package com.bytetrade.pro.bytemodule.chain;

import com.google.common.primitives.Bytes;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class used to represent the weighted set of keys and accounts that must approve operations.
 *
 * {@see <a href="https://bitshares.org/doxygen/structgraphene_1_1chain_1_1authority.html">Authority</a>}
 */
public class Authority  {
	public static Logger log4j = LogManager.getLogger(Authority.class);
    public static final String KEY_ACCOUNT_AUTHS = "account_auths";
    public static final String KEY_KEY_AUTHS = "key_auths";
    public static final String KEY_ADDRESS_AUTHS = "address_auths";
    public static final String KEY_WEIGHT_THRESHOLD = "weight_threshold";
  //  public static final String KEY_EXTENSIONS = "extensions";

    private long weight_threshold;
    private HashMap<UserAccount, Long> account_auths;
    private HashMap<PublicKey, Long> key_auths;
    private HashMap<Address, Long> address_auths;
    //private Extensions extensions;

    public Authority(){
        this.weight_threshold = 1;
        this.account_auths = new HashMap<UserAccount, Long>();
        this.key_auths = new HashMap<PublicKey, Long>();
        this.address_auths = new HashMap<Address, Long>();
        //  extensions = new Extensions();
    }

    /**
     * Constructor for the authority class that takes every possible detail.
     * @param weight_threshold: The total weight threshold
     * @param keyAuths: Map of key to weights relationships. Can be null.
     * @param accountAuths: Map of account to weights relationships. Can be null.
     * @throws MalformedAddressException
     */
    public Authority(long weight_threshold, HashMap<PublicKey, Long> keyAuths, HashMap<UserAccount, Long> accountAuths) {
        this();
        this.weight_threshold = weight_threshold;
        if(keyAuths != null)
            this.key_auths = keyAuths;
        else
            this.key_auths = new HashMap<>();
        if(accountAuths != null)
            this.account_auths = accountAuths;
        else
            this.account_auths = new HashMap<>();
    }

    public long getWeightThreshold() {
        return weight_threshold;
    }

    public void setWeightThreshold(long weight_threshold) {
        this.weight_threshold = weight_threshold;
    }

    public void setKeyAuthorities(HashMap<Address, Long> keyAuths){
        if(keyAuths != null){
            for(Address address : keyAuths.keySet()){
                key_auths.put(address.getPublicKey(), keyAuths.get(address));
            }
        }
    }

    public void setAddressAuthorities(HashMap<Address, Long> keyAuths){
        if(keyAuths != null){
            for(Address address : keyAuths.keySet()){
                address_auths.put(address, keyAuths.get(address));
            }
        }
    }

    public void setAccountAuthorities(HashMap<UserAccount, Long> accountAuthorities){
        this.account_auths = accountAuthorities;
    }

    /**
     * @return: Returns a list of public keys linked to this authority
     */
    public List<PublicKey> getKeyAuthList(){
        ArrayList<PublicKey> keys = new ArrayList<>();
        for(PublicKey pk : key_auths.keySet()){
            keys.add(pk);
        }
        return keys;
    }

    /**
     * @return: Returns a list of accounts linked to this authority
     */
    public List<UserAccount> getAccountAuthList(){
        ArrayList<UserAccount> accounts = new ArrayList<>();
        for(UserAccount account : account_auths.keySet()){
            accounts.add(account);
        }
        return accounts;
    }

    public HashMap<PublicKey, Long> getKeyAuths(){
        return this.key_auths;
    }

    public HashMap<UserAccount, Long> getAccountAuths(){
        return this.account_auths;
    }


    public String toJsonString() {
        return null;
    }


    public JSONObject toJsonObject() {

        JSONObject authority = new JSONObject();

        try {

            authority.put(KEY_WEIGHT_THRESHOLD, weight_threshold);
            JSONObject keyAuthArray = new JSONObject();
            JSONObject accountAuthArray = new JSONObject();
            JSONObject addressAuthArray = new JSONObject();

            try {

                for (PublicKey publicKey : key_auths.keySet()) {

                    Address address = new Address(publicKey.getKey());
                    keyAuthArray.put(address.toString(), key_auths.get(publicKey));

                }

                for (UserAccount key : account_auths.keySet()) {

                    accountAuthArray.put(key.toString(), account_auths.get(key));

                }

                for (Address address : address_auths.keySet()) {

                    addressAuthArray.put(address.getAddress(), address_auths.get(address));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            authority.put(KEY_KEY_AUTHS, keyAuthArray);
            authority.put(KEY_ACCOUNT_AUTHS, accountAuthArray);
            authority.put(KEY_ADDRESS_AUTHS, addressAuthArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //  authority.add(KEY_EXTENSIONS, extensions.toJsonObject());
        return authority;

    }


    public List<Byte> toBytes() {

        List<Byte> byteArray = new ArrayList<Byte>();
        // Adding number of authorities
        byteArray.add(Byte.valueOf((byte) (account_auths.size() + key_auths.size())));

        // If the authority is not empty of references, we serialize its contents
        // otherwise its only contribution will be a zero byte
        if(account_auths.size() + key_auths.size() > 0){
            // Weight threshold
            byteArray.addAll(Bytes.asList(Util.revertInteger(new Integer((int) weight_threshold))));

            // Number of account authorities
            byteArray.add((byte) account_auths.size());

            //TODO: Check the account authorities serialization
            // Serializing individual accounts and their corresponding weights
            for(UserAccount account : account_auths.keySet()){
                byteArray.addAll(account.toBytes());
                byteArray.addAll(Bytes.asList(Util.revertShort(account_auths.get(account).shortValue())));
            }

            // Number of key authorities
            byteArray.add((byte) key_auths.size());

            // Serializing individual keys and their corresponding weights
            for(PublicKey publicKey : key_auths.keySet()){
                byteArray.addAll(publicKey.toBytes());
                byteArray.addAll(Bytes.asList(Util.revertShort(key_auths.get(publicKey).shortValue())));
            }

            // Adding number of extensions
           // byteArray.add((byte) extensions.size());
        }
        return Util.convertBytes(Bytes.toArray(byteArray));

    }

    @Override
    public boolean equals(Object obj) {
        Authority authority = (Authority) obj;
        HashMap<PublicKey, Long> keyAuths = authority.getKeyAuths();
        HashMap<UserAccount, Long> accountAuths = authority.getAccountAuths();
        log4j.info("key auths match: "+this.key_auths.equals(keyAuths));
        log4j.info("account auths match: "+this.account_auths.equals(accountAuths));
        log4j.info("weight threshold matches: "+(this.weight_threshold == authority.weight_threshold));
        return this.key_auths.equals(keyAuths) &&
                this.account_auths.equals(accountAuths) &&
                this.weight_threshold == authority.weight_threshold;
    }


}