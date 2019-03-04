package newton.newton;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.ECKey;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bytetrade.pro.bytemodule.chain.Address;
import com.google.zxing.WriterException;

import createImg.ImageUtils;
import createImg.QRCodeUtil;
import manager.AssetManager;
import manager.UsersManager;
import manager.WithdrawManager;
import utils.DesUtil;
import utils.HttpRequest;
import utils.Utils;

public abstract class Test {
	public static void main(String[] args) {
//		createAccount();
//		withdraw();
		String bttPrivateId = DesUtil.decrypt("QnOwB+/BV2G4pTdjtwaPpZ6yZ7Uwb1IAlXMmfQbjfY/0t6Y7Sd+3kHWHPhxcIriRdt9YiR8wTZfZ/C421K/OFczaX4uqu3Un");
		System.out.println(bttPrivateId);
//		HttpRequest.init();
//		System.out.println(new Timestamp(new Date("1970-01-01 00:00:01").getTime()));
	}

	public static void createAccount(){
		ECKey mPrivateKey =new ECKey();
		final Address a = new Address(ECKey.fromPublicOnly(mPrivateKey.getPubKey()));
		String bttAddress=a.getAddress();
		String privateId=mPrivateKey.getPrivateKeyAsHex();
		String userid=DesUtil.MD5(new Date().getTime()+"").substring(16);
		System.out.println(userid);
		System.out.println(privateId);
		// 请求btt账户注册
		JSONObject res=UsersManager.createBttUserParams(bttAddress,userid);
		Map<String, String> paramsMap=new HashMap<String, String>();
		paramsMap.put("cmd", "registerAccount");
		paramsMap.put("account", res.getString("account"));
		String resultStr=HttpRequest.sendPostSign("0",paramsMap);
		System.out.println(resultStr);
	}
	
	public static void withdraw(){
		HttpRequest.init();
		AssetManager.init();
		WithdrawManager.init();
		String bttUserid = "ccad66e65d9c10ec";
		String bttPrivateId = DesUtil.decrypt("QnOwB+/BV2G4pTdjtwaPpZ6yZ7Uwb1IAlXMmfQbjfY/0t6Y7Sd+3kHWHPhxcIriRdt9YiR8wTZfZ/C421K/OFczaX4uqu3Un");
		int assetId = 2;
		BigInteger amount = new BigInteger("1030000000000000");
		String pixiuAddress = "0x7572ea57558379c8b4441ebe8451e7b81547e8ad";
		
		String toExternalAddress = "0x34aF13D89eBCdbc0548FdeB15e5FFF5c2eD93036";
		int chainType=1;
		if(AssetManager.AssetIdMap.containsKey(assetId+"")){
			chainType=AssetManager.AssetIdMap.get(assetId+"").getInteger("chain_type");
		}
		WithdrawManager.doWithdraw(bttUserid, bttPrivateId, assetId, amount, pixiuAddress, toExternalAddress,chainType);
	}
}
