package manager;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.ECKey;
import org.hibernate.Session;
import com.alibaba.fastjson.JSONObject;
import com.bytetrade.pro.bytemodule.chain.AccountID;
import com.bytetrade.pro.bytemodule.chain.BaseOperation;
import com.bytetrade.pro.bytemodule.chain.ByteString;
import com.bytetrade.pro.bytemodule.chain.Chains;
import com.bytetrade.pro.bytemodule.chain.Optional;
import com.bytetrade.pro.bytemodule.chain.Ripemd160;
import com.bytetrade.pro.bytemodule.chain.TimeSpec;
import com.bytetrade.pro.bytemodule.chain.Transaction;
import com.bytetrade.pro.bytemodule.chain.UINT128;
import com.bytetrade.pro.bytemodule.chain.UINT32;
import com.bytetrade.pro.bytemodule.chain.UINT8;
import com.bytetrade.pro.bytemodule.chain.Util;
import com.bytetrade.pro.bytemodule.chain.operations.Transfer2Operation;
import createImg.ImageUtils;
import createImg.QRCodeUtil;
import hibernate.HibernateUtil;
import hibernate.hbm.LoveChain;
import redis.RedisUtil;
import utils.DesUtil;
import utils.HttpRequest;
import utils.Utils;

public class LoveChainManager {
	
	public static Logger log4j = LogManager.getLogger(LoveChainManager.class);
	final static String QrCodePath = "/home/ubuntu/tempImg/";
//	final static String QrCodePath = "D://tempImg/";
	public static boolean canSendEmail(String ip) {
		if (ip.equals("61.148.52.46")||ip.equals("209.97.186.90")) {
			return true;
		}
		int count = 0;
		String sendEmailCount = RedisUtil.getKV("loveChainCount_" + ip);
		if (Utils.isStringEmpty(sendEmailCount)) {
			return true;
		}
		count = Integer.parseInt(sendEmailCount);
		if (count >= 10) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean canCreatePoster(String ip) {
		if (ip.equals("61.148.52.46")) {
			return true;
		}
		int count = 0;
		String sendEmailCount = RedisUtil.getKV("loveChainCount_" + ip);
		if (Utils.isStringEmpty(sendEmailCount)) {
			return true;
		}
		count = Integer.parseInt(sendEmailCount);
		if (count >= 50) {
			return false;
		} else {
			return true;
		}
	}

	public static JSONObject loveChainCreatePoster(JSONObject args) {
		JSONObject res = new JSONObject();
		String ip = Utils.getString(res, "ip", "");
		if (canCreatePoster(ip) == false) {
			res.put("code", 200001);
			return res;
		}
		String qrCode = Utils.getString(args, "qrCode", "");
		String content = Utils.getString(args, "content", "");
		String time = Utils.getString(args, "time", "");
		String name = Utils.getString(args, "name", "");
		if (Utils.isStringEmpty(qrCode) || Utils.isStringEmpty(content) || Utils.isStringEmpty(time)
				|| Utils.isStringEmpty(name)) {
			res.put("code", 100001);
			return res;
		}
		try {
			String qrCodePath = QrCodePath + "q_"+Utils.getUUID() + ".png";
			QRCodeUtil.createQRCode(qrCode, qrCodePath, 298, 298);
			String eryyPath = QrCodePath +"p_"+Utils.getUUID()+".png";
			ImageUtils.pressText(time, QrCodePath + "tday.png", eryyPath, "宋体", Font.BOLD, Color.white, 50, -138,
					-570, 1f);
			ImageUtils.pressText(name, eryyPath, eryyPath, "宋体", Font.BOLD, Color.white, 50, 298, -570, 1f);
			ImageUtils.pressImage(qrCodePath, eryyPath, eryyPath, -120, 500, 1f);
			String text = "<p style='width:630px;height:216px;font-size:40px; line-height:56px;color:white;'>"
					+ content + "</p>";
			String tempTxtPath = QrCodePath + "text_" + Utils.getUUID() + ".png";
			ImageUtils.drawStringCentered(text, "宋体", tempTxtPath, 50, Font.BOLD, Color.white, 815, 216);
			ImageUtils.pressImage(tempTxtPath, eryyPath, eryyPath, 15, -340, 1f);
			String base64Str=getImgStr(eryyPath);
			res.put("code", 0);
			res.put("base64Str", "data:image/png;base64,"+base64Str);
		} catch (Exception e) {
			res.put("code", 100000);
		}
		return res;
	}
	
	public static JSONObject tdayCreatePoster(JSONObject args) {
		JSONObject res = new JSONObject();
		String ip = Utils.getString(res, "ip", "");
		if (canCreatePoster(ip) == false) {
			res.put("code", 200001);
			return res;
		}
		String content = Utils.getString(args, "content", "");
		String time = Utils.getString(args, "time", "");
		String name = Utils.getString(args, "name", "");
		if (Utils.isStringEmpty(content) || Utils.isStringEmpty(time)|| Utils.isStringEmpty(name)) {
			res.put("code", 100001);
			return res;
		}
		try {
			int lengthDiff=length(time)-length(name);
			if(lengthDiff>0){
				for (int i = 0; i < lengthDiff; ++i) {
					name+=" ";
				}
			}else{
				for (int i = lengthDiff; i <0 ; ++i) {
					time+=" ";
				}
			}
			String tdayPath = QrCodePath +"tday_"+Utils.getUUID()+".png";
			ImageUtils.pressText(time, QrCodePath + "tday.png", tdayPath, "宋体", Font.BOLD, Color.white, 54, 0,
					-240, 1f);
			ImageUtils.pressText(name, tdayPath, tdayPath, "宋体", Font.BOLD, Color.white, 54, 0, -65, 1f);
			String text = "<p style='width:630px;height:216px;font-size:40px; line-height:56px;color:white;'>"
					+ content + "</p>";
			String tempTxtPath = QrCodePath + "text_" + Utils.getUUID() + ".png";
			ImageUtils.drawStringCentered(text, "宋体", tempTxtPath, 50, Font.BOLD, Color.white, 815, 216);
			ImageUtils.pressImage(tempTxtPath, tdayPath, tdayPath, 16, 120, 1f);
			String base64Str=getImgStr(tdayPath);
			res.put("code", 0);
			res.put("base64Str", "data:image/png;base64,"+base64Str);
		} catch (Exception e) {
			res.put("code", 100000);
		}
		return res;
	}
	
	public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }
	
	public static String getImgStr(String imgFile){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try 
        {
            in = new FileInputStream(imgFile);        
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }

	public static JSONObject loveChainSend(JSONObject args) {
		log4j.info("loveChainSend:"+args.toJSONString());
		JSONObject res = new JSONObject();
		String ip = Utils.getString(res, "ip", "");
		if (canSendEmail(ip) == false) {
			res.put("code", 200001);
			return res;
		}

		String phone = Utils.getString(args, "phone", "");
		String message = Utils.getString(args, "message", "");
		if (Utils.isStringEmpty(phone) || Utils.isStringEmpty(message)) {
			res.put("code", 100000);
			return res;
		}
		if (message.length() > 90) {
			res.put("code", 10000);
			res.put("msg", "err msg");
			return res;
		}
		Transaction t = getTrObject(DesUtil.encrypt(message));
		org.json.JSONObject o = t.toJsonObject();
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("cmd", "putTransaction");
		paramsMap.put("method", "blockchain.put_transaction");
		paramsMap.put("trObject", o.toString());
		String resultStr = HttpRequest.sendPost("0", paramsMap);
		log4j.info("loveChainSend,resultStr:"+resultStr);
		LoveChain love = new LoveChain();
		String transId = t.id();
		love.setAgent(Utils.getString(args, "agent", ""));
		love.setIp(Utils.getString(args, "ip", ""));
		if(phone.indexOf("@")==-1||phone.length()<13){
			love.setPhone(phone);
		}
		love.setTransId(transId);
		love.setMessage(message);
		love.setCreateAt((int) (new Date().getTime() / 1000));
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.save(love);
		s.getTransaction().commit();
		s.close();
		res.put("code", 0);
		res.put("transUrl", "https://explorer.bytetrade.io/transaction-info.html?id=" + transId + "&blockType=1");
		return res;
	}

	public static Transaction getTrObject(String message) {
		message = message.replace("+", "@").replace("+", "@").replace("+", "@");
		Transfer2Operation transfer2Operation = new Transfer2Operation();
		transfer2Operation.fee = Bytetrade.getPackFee();
		transfer2Operation.from = new AccountID("OneLife");
		transfer2Operation.to = new AccountID("OneLover");
		transfer2Operation.asset_type = new UINT32(new BigInteger("1"));
		transfer2Operation.amount = new UINT128(new BigInteger("131400000000000000"));
		transfer2Operation.message = new Optional<ByteString>(new ByteString(message));
		;

		ArrayList<BaseOperation> operations = new ArrayList<BaseOperation>();
		operations.add(transfer2Operation);

		Transaction transaction = new Transaction(Util.hexToBytes(Chains.CHAIN_ID), operations);
		transaction.timestamp = TimeSpec.now();
		transaction.expiration = new Optional<>(new TimeSpec(transaction.timestamp.value + 30));
		transaction.validate_type = new UINT8(BigInteger.valueOf(0));
		transaction.dapp = new Optional<AccountID>(new AccountID(Bytetrade.getDapp()));
		transaction.proposal_transaction_id = new Optional<Ripemd160>(null);

		ECKey mPrivateKey = org.bitcoinj.core.ECKey
				.fromPrivate(Util.hexToBytes("709bb548bea049c1ea580bf414ede8a4612c16c007b8f511f3f8990fdd37d41a"));
		transaction.signTransaction(mPrivateKey);
		return transaction;
	}

}
