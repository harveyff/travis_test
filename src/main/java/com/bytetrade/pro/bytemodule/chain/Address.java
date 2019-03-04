package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.errors.MalformedAddressException;
import com.google.common.primitives.Bytes;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA512Digest;

import java.util.Arrays;

/**
 * Class used to encapsulate address-related operations.
 */
public class Address {

    public final static String BITSHARES_PREFIX = "BTT";

    private PublicKey publicKey = null;
    private String prefix;
    private String a;

    public Address(ECKey key) {
        this.publicKey = new PublicKey(key);
        this.prefix = BITSHARES_PREFIX;
    }

    public Address(ECKey key, String prefix) {
        this.publicKey = new PublicKey(key);
        this.prefix = prefix;
    }

    public Address(String address) throws MalformedAddressException {
        this.a = address;
        this.prefix = address.substring(0, 3);
        byte[] decoded = Base58.decode(address.substring(3, address.length()));
        byte[] pubKey = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
        byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);
        //publicKey = new PublicKey(ECKey.fromPublicOnly(pubKey));
        byte[] calculatedChecksum = calculateChecksum(pubKey);
        for (int i = 0; i < calculatedChecksum.length; i++) {
            if (checksum[i] != calculatedChecksum[i]) {
                throw new MalformedAddressException("Checksum error");
            }
        }
    }

    public String getAddress() {

        if( this.publicKey == null) {
            return this.a;
        } else {
            byte[] pubKey = Util.convertBytesArray(this.publicKey.toBytes());
            RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
            SHA512Digest sha512Digest = new SHA512Digest();
            sha512Digest.update(pubKey, 0, pubKey.length);
            byte[] intermediate = new byte[512 / 8];
            sha512Digest.doFinal(intermediate, 0);
            ripemd160Digest.update(intermediate, 0, intermediate.length);
            byte[] output = new byte[160 / 8];
            ripemd160Digest.doFinal(output, 0);
            String encoded = Base58.encode(output);
            byte[] checksum = new byte[(160 / 8) + 4];
            System.arraycopy(calculateChecksum(output), 0, checksum, checksum.length - 4, 4);
            System.arraycopy(output, 0, checksum, 0, output.length);

            return ("BTT" + Base58.encode(checksum));
        }
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public String toString() {
        byte[] pubKey = Util.convertBytesArray(this.publicKey.toBytes());
        byte[] checksum = calculateChecksum(pubKey);
        byte[] pubKeyChecksummed = Bytes.concat(pubKey, checksum);
        return this.prefix + Base58.encode(pubKeyChecksummed);
    }

    private byte[] calculateChecksum(byte[] data) {
        byte[] checksum = new byte[160 / 8];
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        ripemd160Digest.update(data, 0, data.length);
        ripemd160Digest.doFinal(checksum, 0);
        return Arrays.copyOfRange(checksum, 0, 4);
    }


}
