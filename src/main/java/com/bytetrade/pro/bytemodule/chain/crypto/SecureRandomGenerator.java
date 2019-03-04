package com.bytetrade.pro.bytemodule.chain.crypto;

import java.security.SecureRandom;

/**
 * Created by nelson on 12/20/16.
 */
public class SecureRandomGenerator {

    public static SecureRandom getSecureRandom(){
        SecureRandomStrengthener randomStrengthener = SecureRandomStrengthener.getInstance();
//        randomStrengthener.addEntropySource(new AndroidRandomSource());
        return randomStrengthener.generateAndSeedRandomNumberGenerator();
    }
}
