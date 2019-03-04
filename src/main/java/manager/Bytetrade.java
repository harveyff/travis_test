package manager;

import java.math.BigInteger;

import com.bytetrade.pro.bytemodule.chain.UINT128;

public class Bytetrade {
	private static UINT128 mFee = new UINT128(BigInteger.valueOf(300000000000000l));
	private static String mDapp = "Sagittarius";

	public static void settPackFee(UINT128 fee) {
		mFee = fee;
	}

	public static UINT128 getPackFee() {
		return mFee;
	}

	public static String getDapp() {
		return mDapp;
	}

	public static void setDapp(String app) {
		mDapp = app;
	}

}
