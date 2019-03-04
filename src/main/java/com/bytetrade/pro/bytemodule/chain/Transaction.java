package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;
import com.google.common.primitives.Bytes;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Class used to represent a generic Graphene transaction.
 */
public class Transaction implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    /* Default expiration time */
    public static final int DEFAULT_EXPIRATION_TIME = 30;

    /* Constant field names used for serialization/deserialization purposes */

//    public static final String KEY_EXPIRATION = "expiration";
//    public static final String KEY_SIGNATURES = "signatures";
//    public static final String KEY_OPERATIONS = "operations";
//    public static final String KEY_EXTENSIONS = "extensions";
//    public static final String KEY_REF_BLOCK_NUM = "ref_block_num";
//    public static final String KEY_REF_BLOCK_PREFIX = "ref_block_prefix";

    // Using the bitshares mainnet chain id by default

    private byte[] chainId = Util.hexToBytes(Chains.CHAIN_ID);

    // private ECKey privateKey;

    public TimeSpec timestamp;
    public Optional<TimeSpec> expiration;
    private List<BaseOperation> operations;
    public UINT8 validate_type;
    public Optional<AccountID> dapp;
    public Optional<Ripemd160> proposal_transaction_id;
    public byte[] signature = null;

    /**
     * Transaction constructor
     * @param chainId       The chain id

     * @param operations    List of operations contained in this transaction
     */
    public Transaction(byte[] chainId, List<BaseOperation> operations){
        this.chainId = chainId;
        this.operations = operations;
    }

    /**
     * Transaction constructor.
     * @param operationList List of operations to include in the transaction.
     */
    public Transaction(List<BaseOperation> operationList){
        this(Util.hexToBytes(Chains.CHAIN_ID), operationList);
    }

    /**
     * Transaction constructor.

     */
//    public Transaction(String wif, List<BaseOperation> operation_list){
//        this(DumpedPrivateKey.fromBase58(null, wif).getKey(), operation_list);
//    }

   // public ECKey getPrivateKey(){
   //     return this.privateKey;
   // }

    public List<BaseOperation> getOperations(){ return this.operations; }

    /**
     * This method is used to query whether the instance has a private key.
     * @return
     */
   // public boolean hasPrivateKey(){
   //     return this.privateKey != null;
   // }

    /**
     * Obtains a signature of this transaction. Please note that due to the current reliance on
     * bitcoinj to generate the signatures, and due to the fact that it uses deterministic
     * ecdsa signatures, we are slightly modifying the expiration time of the transaction while
     * we look for a signature that will be accepted by the graphene network.
     *
     * This should then be called before any other serialization method.
     * @return: A valid signature of the current transaction.
     */
    public byte[] getGrapheneSignature(ECKey privateKey){

        boolean isGrapheneCanonical = false;
        byte[] sigData = null;

        while(!isGrapheneCanonical) {

            byte[] serializedTransaction = Util.convertBytesArray(this.toBytes());
            Sha256Hash hash = Sha256Hash.wrap(Sha256Hash.hash(serializedTransaction));

            int recId = -1;
            ECKey.ECDSASignature sig = privateKey.sign(hash);

            // Now we have to work backwards to figure out the recId needed to recover the signature.
            for (int i = 0; i < 4; i++) {
                ECKey k = ECKey.recoverFromSignature(i, sig, hash, privateKey.isCompressed());
                if (k != null && k.getPubKeyPoint().equals(privateKey.getPubKeyPoint())) {
                    recId = i;
                    break;
                }
            }

            sigData = new byte[65];  // 1 header + 32 bytes for R + 32 bytes for S
            int headerByte = recId + 27 + (privateKey.isCompressed() ? 4 : 0);
            sigData[0] = (byte) headerByte;
            System.arraycopy(Utils.bigIntegerToBytes(sig.r, 32), 0, sigData, 1, 32);
            System.arraycopy(Utils.bigIntegerToBytes(sig.s, 32), 0, sigData, 33, 32);

            // Further "canonicality" tests
            if(((sigData[0] & 0x80) != 0) || (sigData[0] == 0) ||
                    ((sigData[1] & 0x80) != 0) || ((sigData[32] & 0x80) != 0) ||
                    (sigData[32] == 0) || ((sigData[33] & 0x80)  != 0)){
                expiration.optionalField.value = expiration.optionalField.value+1;
            }else{
                isGrapheneCanonical = true;
            }
        }
        return sigData;
    }

    public void setChainId(String chainId){
        this.chainId = Util.hexToBytes(chainId);
    }

    public void setChainId(byte[] chainId){
        this.chainId = chainId;
    }

    public byte[] getChainId(){
        return this.chainId;
    }

    public List<Byte> toBytes(){
        return toBytes(true);
    }

    /**
     * Method that creates a serialized byte array with compact information about this transaction
     * that is needed for the creation of a signature.
     * @return: byte array with serialized information about this transaction.
     */
    public List<Byte> toBytes(boolean containChainid){

        List<Byte> byteArray = new ArrayList<Byte>();

        try {
            // Creating a List of Bytes and adding the first bytes from the chain apiId

            if( containChainid ) {
                byteArray.addAll(Bytes.asList(chainId));
            }

            byteArray.addAll(this.timestamp.toBytes());
            byteArray.addAll(this.expiration.toBytes());
            // Adding the block data

            // Adding the number of operations
            byteArray.add((byte) this.operations.size());

            // Adding all the operations
            for (BaseOperation operation : operations) {
                byteArray.add(operation.getId());
                byteArray.addAll(operation.toBytes());
            }

            byteArray.addAll(this.validate_type.toBytes());
            byteArray.addAll(this.dapp.toBytes());
            byteArray.addAll(this.proposal_transaction_id.toBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding extensions byte
        //byteArray.addAll(Bytes.asList(this.extensions.toBytes()));

        return byteArray;
    }


    public String toJsonString() {
        return toJsonObject().toString();
    }

    public void setSignature(byte[] s) {
        signature = s;
    }

    public void signTransaction(ECKey key) {
        byte[] b = getGrapheneSignature(key);
        setSignature(b);
    }

    public String id() {
        byte[] serializedTransaction = Util.convertBytesArray(this.toBytes(false));
        //Sha256Hash hash = Sha256Hash.wrap();
        //return Util.bytesToHex(Sha256Hash.hash(serializedTransaction));
        byte[] b = Sha256Hash.hash(serializedTransaction);
        byte[] c = new byte[20];
        for( int i = 0; i< 20; ++i ) {
            c[i] = b[i];
        }
        return Util.bytesToHex(c);
    }

    public JSONObject toJsonObject() {

        JSONObject obj = new JSONObject();

        try {

            // Getting the signature before anything else,
            // since this might change the transaction expiration data slightly
            // byte[] signature = getGrapheneSignature();
            // Formatting expiration time

//        Date expirationTime = new Date(blockData.getExpiration() * 1000);
//        SimpleDateFormat dateFormat = new SimpleDateFormat(Util.TIME_DATE_FORMAT);
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            // Adding expiration
            if (this.expiration != null && this.expiration.isNull() == false) {
                obj.put("expiration", this.expiration.toString());
            }

            obj.put("timestamp", this.timestamp.toString());

            // Adding signatures
            JSONArray operationsArray = new JSONArray();
            //for (BaseOperation operation : operations) {
            for( int i =0; i < operations.size(); ++i ) {
                operationsArray.put(i, operations.get(i).toJsonObject());
            }
            // Adding operations
            obj.put("operations", operationsArray);

            // Adding extensions
            if (this.dapp != null && this.dapp.isNull() == false) {
                obj.put("dapp", dapp.toString());
            }

            obj.put("validate_type", validate_type.toString());

            if (this.proposal_transaction_id != null && this.proposal_transaction_id.isNull() == false) {
                obj.put("proposal_transaction_id", proposal_transaction_id.toString());
            }

            JSONArray signatureArray = new JSONArray();

            if( signature != null ) {
                signatureArray.put(Util.bytesToHex(signature));
            }

            obj.put("signatures", signatureArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    public Transaction(JSONObject o) {
        try {
            if( o.has("expiration")) {
                expiration = new Optional<TimeSpec>(Util.getTimeSpec(
                        o.getString("expiration")));
            } else {
                expiration = new Optional<TimeSpec>(null);
            }

            timestamp =  Util.getTimeSpec(o.getString("timestamp"));

            JSONArray o_array = o.getJSONArray("operations");
            operations = new ArrayList<BaseOperation>();
            for( int i = 0; i < o_array.length(); ++i ) {
                JSONArray array = o_array.getJSONArray(i);

                int j = array.getInt(0);
                operations.add(Util.getOperation(j, array.getJSONObject(1)));
            }

            validate_type = new UINT8(new BigInteger("" + o.getInt("validate_type")));

            if( o.has("dapp") ) {
                dapp = new Optional<AccountID>(new AccountID(o.getString("dapp")));
            } else {
                dapp = new Optional<AccountID>(null);
            }

            if( o.has("proposal_transaction_id") ) {
                proposal_transaction_id = new Optional<Ripemd160>(new Ripemd160(o.getString("proposal_transaction_id")));
            } else {
                proposal_transaction_id = new Optional<Ripemd160>(null);
            }

            if( o.has("signatures") ) {

                JSONArray s_array = o.getJSONArray("signatures");
                if( s_array.length() > 0 ) {
                    signature = Util.hexToBytes(s_array.getString(0));
                } else {
                    signature = null;
                }

            } else {
                signature = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}