package com.bytetrade.pro.bytemodule.chain;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import com.bytetrade.pro.bytemodule.chain.operations.OrderCancelOperation;
import com.bytetrade.pro.bytemodule.chain.operations.OrderCreateOperation;
import com.bytetrade.pro.bytemodule.chain.operations.ProposeOperation;
import com.bytetrade.pro.bytemodule.chain.operations.Transfer2Operation;
import com.bytetrade.pro.bytemodule.chain.operations.TransferOperation;
import com.bytetrade.pro.bytemodule.chain.operations.Withdraw2Operation;
import com.bytetrade.pro.bytemodule.chain.operations.WithdrawOperation;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

/**
 * Class used to encapsulate common utility methods
 */
public class Util {

    public static final String TAG = "Util";
    private static final char[] hexArray = "0123456789abcdef".toCharArray();
    public static final int LZMA = 0;
    public static final int XZ = 1;


    public static BigInteger getBigInteger(JSONObject o, String name) {
        try {
            return new BigInteger(o.getString(name));
        } catch (Exception e) {
            try {
                return new BigInteger("" + o.getLong(name));
            } catch (Exception e2) {
                try {
                    return new BigInteger("" + o.getInt(name));
                } catch (Exception e3) {
                    return new BigInteger("" + 0);
                }
            }

        }
    }

    public static TimeSpec getTimeSpec(String t) {
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mFinalTime = utc2Local(t.replace("T", " "),"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss");
            Date dd = sdf.parse(mFinalTime);
            
            return new TimeSpec((int)(dd.getTime()/1000l));
        } catch (Exception e) {
            return new TimeSpec(0);
        }
        //return new TimeSpec((int)(System.currentTimeMillis() / 1000l));
    }

    public static BaseOperation getOperation(int j, JSONObject o) {
        
        if( j == OperationType.TRANSFER_OPERATION.ordinal()) {
            return new TransferOperation(o);
        } else if( j == OperationType.TRANSFER2_OPERATION.ordinal()) {
            return new Transfer2Operation(o);
        } else if( j == OperationType.ORDER_CREATE_OPERATION.ordinal()) {
          
            return new OrderCreateOperation(o);
        } else if( j == OperationType.ORDER_CREATE2_OPERATION.ordinal()) {
            return null;
        } else if( j == OperationType.ORDER_CANCEL_OPERATION.ordinal()) {
            return new OrderCancelOperation(o);
        } else if( j == OperationType.proposal_OPERATION.ordinal()) {
            return new ProposeOperation(o);
        } else if( j == OperationType.WITHDRAW_OPERATION.ordinal()) {
            return new WithdrawOperation(o);
        } else if( j == OperationType.WITHDRAW2_OPERATION.ordinal()) {
            return new Withdraw2Operation(o);
        } else {
            return null;
        }
    }

    /**
     * AES encryption key length in bytes
     */
    public static final int KEY_LENGTH = 32;

    /**
     * Time format used across the platform
     */
    public static final String TIME_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static List<Byte> convertBytes(byte[] bytes) {
        List<Byte> byteArray = new ArrayList<Byte>();

        for( byte b : bytes) {
            byteArray.add(b);
        }

        return byteArray;
    }

    public static byte[] convertBytesArray(List<Byte> byteArray) {
        byte[] b = new byte[byteArray.size()];
        for( int i = 0; i < byteArray.size(); ++i ) {
            b[i] = byteArray.get(i);
        }

        return b;
    }

    public static List<Byte> convertStringToBytes(String b) {
        List<Byte> byteArray = new ArrayList<Byte>();
        byteArray.addAll( Bytes.asList((Varint.writeUnsignedVarInt(b.length()))));
        byteArray.addAll( Bytes.asList(b.getBytes()));

        return byteArray;
    }

    /**
     * Converts an hexadecimal string to its corresponding byte[] value.
     * @param s: String with hexadecimal numbers representing a byte array.
     * @return: The actual byte array.
     */
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Converts a byte array, into a user-friendly hexadecimal string.
     * @param bytes: A byte array.
     * @return: A string with the representation of the byte array.
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Decodes an ascii string to a byte array.
     * @param data: Arbitrary ascii-encoded string.
     * @return: Array of bytes.
     */
    public static byte[] hexlify(String data){
        ByteBuffer buffer = ByteBuffer.allocate(data.length());
        for(char letter : data.toCharArray()){
            buffer.put((byte) letter);
        }
        return buffer.array();
    }

    /**
     * Serializes long value to a byte array.
     * @param data Long value.
     * @return Array of bytes.
     */
    public static byte[] serializeLongToBytes(long data) {
        List<Byte> bytes = new LinkedList<>();
        long value = data;
        do {
            byte b = (byte)(value & 0x7F);
            value >>= 7;
            if (value != 0) {
                b |= 0x80;
            }
            bytes.add(b);
        } while (value != 0);

        return Bytes.toArray(bytes);
    }



    /**
     * Returns an array of bytes with the underlying data used to represent an integer in the reverse form.
     * This is useful for endianess switches, meaning that if you give this function a big-endian integer
     * it will return it's little-endian bytes.
     * @param input An Integer value.
     * @return The array of bytes that represent this value in the reverse format.
     */
    public static byte[] revertInteger(Integer input){
        return ByteBuffer.allocate(Integer.SIZE / 8).putInt(Integer.reverseBytes(input)).array();
    }

    /**
     * Same operation as in the revertInteger function, but in this case for a short (2 bytes) value.
     * @param input A Short value
     * @return The array of bytes that represent this value in the reverse format.
     */
    public static byte[] revertShort(Short input){
        return ByteBuffer.allocate(Short.SIZE / 8).putShort(Short.reverseBytes(input)).array();
    }

    /**
     * Same operation as in the revertInteger function, but in this case for a long (8 bytes) value.
     * @param input A Long value
     * @return The array of bytes that represent this value in the reverse format.
     */
    public static byte[] revertLong(Long input) {
        return ByteBuffer.allocate(Long.SIZE / 8).putLong(Long.reverseBytes(input)).array();
    }

    /**
     * Same operation as in the revertInteger function, but with an UnsignedLong object as argument.
     * @param input An UnsignedLong class instance
     * @return The array of bytes that represent this value in the reverse format.
     */
    public static byte[] revertUnsignedLong(UnsignedLong input){
        return ByteBuffer.allocate(Long.SIZE / 8).putLong(Long.reverseBytes(input.longValue())).array();
    }

    public static byte[] revertUnsignedInteger(UnsignedInteger input){
        return ByteBuffer.allocate(Integer.SIZE / 8).putInt(Integer.reverseBytes(input.intValue())).array();
    }

    public static byte[] revertBytes(byte[] array){
        byte[] reverted = new byte[array.length];
        for(int i = 0; i < reverted.length; i++){
            reverted[i] = array[array.length - i - 1];
        }
        return reverted;
    }

    /**
     * Function to encrypt a message with AES
     * @param input data to encrypt
     * @param key key for encryption
     * @return AES Encription of input 
     */
    public static byte[] encryptAES(byte[] input, byte[] key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] result = md.digest(key);
            byte[] ivBytes = new byte[16];
            System.arraycopy(result, 32, ivBytes, 0, 16);
            byte[] sksBytes = new byte[32];
            System.arraycopy(result, 0, sksBytes, 0, 32);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            cipher.init(true, new ParametersWithIV(new KeyParameter(sksBytes), ivBytes));
            byte[] temp = new byte[input.length + (16 - (input.length % 16))];
            System.arraycopy(input, 0, temp, 0, input.length);
            Arrays.fill(temp, input.length, temp.length, (byte) (16 - (input.length % 16)));
            byte[] out = new byte[cipher.getOutputSize(temp.length)];
            int proc = cipher.processBytes(temp, 0, temp.length, out, 0);
            cipher.doFinal(out, proc);
            temp = new byte[out.length - 16];
            System.arraycopy(out, 0, temp, 0, temp.length);
            return temp;
        } catch (NoSuchAlgorithmException | DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
         //   LogUtils.i("encryptAES error ->" + ex.toString().trim());
        }
        return null;
    }

    /**
     * Function to decrypt a message with AES encryption
     * @param input data to decrypt
     * @param key key for decryption
     * @return input decrypted with AES. Null if the decrypt failed (Bad Key)
     */
    public static byte[] decryptAES(byte[] input, byte[] key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] result = md.digest(key);
            byte[] ivBytes = new byte[16];
            System.arraycopy(result, 32, ivBytes, 0, 16);
            byte[] sksBytes = new byte[32];
            System.arraycopy(result, 0, sksBytes, 0, 32);
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            cipher.init(false, new ParametersWithIV(new KeyParameter(sksBytes), ivBytes));

            byte[] pre_out = new byte[cipher.getOutputSize(input.length)];
            int proc = cipher.processBytes(input, 0, input.length, pre_out, 0);
            int proc2 = cipher.doFinal(pre_out, proc);
            byte[] out = new byte[proc+proc2]; 
            System.arraycopy(pre_out, 0, out, 0, proc+proc2);
            
            //Unpadding
            byte countByte = (byte)((byte)out[out.length-1] % 16);
            int count = countByte & 0xFF;
                       
            if ((count > 15) || (count <= 0)){
                return out;
            }
            
            byte[] temp = new byte[count];
            System.arraycopy(out, out.length - count, temp, 0, temp.length);
            byte[] temp2 = new byte[count];
            Arrays.fill(temp2, (byte) count);
            if (Arrays.equals(temp, temp2)) {
                temp = new byte[out.length - count];
                System.arraycopy(out, 0, temp, 0, out.length - count);
                return temp;
            } else {
                return out;
            }            
        } catch (NoSuchAlgorithmException | DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Transform an array of bytes to an hex String representation
     * @param input array of bytes to transform as a string
     * @return Input as a String
     */
    public static String byteToString(byte[] input) {
        StringBuilder result = new StringBuilder();
        for (byte in : input) {
            if ((in & 0xff) < 0x10) {
                result.append("0");
            }
            result.append(Integer.toHexString(in & 0xff));
        }
        return result.toString();
    }

    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcTime;
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }

}
