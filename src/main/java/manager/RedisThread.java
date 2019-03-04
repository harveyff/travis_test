package manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.ECKey;
import org.hibernate.Session;

import com.alibaba.fastjson.JSONObject;

import hibernate.HibernateUtil;
import com.bytetrade.pro.bytemodule.chain.Address;
import hibernate.hbm.Users;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import utils.AmazonSESUtil;
import utils.DesUtil;
import utils.HttpRequest;
import utils.Utils;

public class RedisThread extends Thread {
	public static Logger log4j = LogManager.getLogger(RedisThread.class);
	@Override
	public void run() {
		Jedis redis = new Jedis("localhost");
		JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
			}

			@Override
			public void onMessage(String channel, String message) {
				System.out.println("channel " + channel + ",message" + message);
				if (channel.equals("sendEmail")) {
					JSONObject params = JSONObject.parseObject(message);
					String email=params.getString("email");
					String type=params.getString("type");
					String lang=params.getString("lang");
					if(type.equals("register")||type.equals("reregister")){
						// 将生成的信息保存到用户表
						Users user=UsersManager.getUserByEmail(email);
						if(user!=null){
							if(Utils.isStringEmpty(user.getBttPrivateId())){
								ECKey mPrivateKey =new ECKey();
								final Address a = new Address(ECKey.fromPublicOnly(mPrivateKey.getPubKey()));
								String bttAddress=a.getAddress();
								String privateId=mPrivateKey.getPrivateKeyAsHex();
								user.setBttPrivateId(DesUtil.encrypt(privateId));
								String userid=DesUtil.MD5(email+new Date().getTime()).substring(16);
								user.setBttUserid(userid);
								// 请求btt账户注册
								JSONObject res=UsersManager.createBttUserParams(bttAddress,userid);
								Map<String, String> paramsMap=new HashMap<String, String>();
								paramsMap.put("cmd", "registerAccount");
								paramsMap.put("account", res.getString("account"));
								String resultStr=HttpRequest.sendPostSign("0",paramsMap);
								log4j.info(new Date()+" ,"+email+":BTT账户注册结果："+resultStr);
								JSONObject resStr=JSONObject.parseObject(resultStr);
								if(resStr.getInteger("code")==0){
									Session s = null;
									s = HibernateUtil.getSessionFactory().openSession();
									s.beginTransaction();
									s.update(user);
									s.getTransaction().commit();
									s.close();
									
									String bttAndEthUrl="http://pixiu.bytetrade.io/api/v1";
									if(Utils.isDug){
										bttAndEthUrl="https://newton.bytetrade.io/pixiu/api/v1";
									}
									Map<String, String> bttAndEthUrlParamsMap=new HashMap<String, String>();
									bttAndEthUrlParamsMap.put("cmd", "requestAddress");
									bttAndEthUrlParamsMap.put("userid", userid);
									// 充值回调地址
									bttAndEthUrlParamsMap.put("url", "https://newton.bytetrade.io/newton/api/v1?cmd=depositNotify");
									if(Utils.isDug){
										bttAndEthUrlParamsMap.put("url", "https://newton-test.bytetrade.io/newton/api/v1?cmd=depositNotify");
									}
									String bttAndEthUrlResStr=HttpRequest.sendPostUrl(bttAndEthUrl, bttAndEthUrlParamsMap);
									JSONObject bttAndEthUrlRes=JSONObject.parseObject(bttAndEthUrlResStr);
									
									if(bttAndEthUrlRes.getInteger("code")==0){
										// 保存用户btc以及eth充值地址信息
										if(AssetManager.AssetSymbolMap.containsKey("BTC")){
											hibernate.hbm.Address addressBtc=new hibernate.hbm.Address();
											addressBtc.setUserId(email);
											addressBtc.setAssetId(AssetManager.AssetSymbolMap.get("BTC").getInteger("id")+"");
											addressBtc.setShowIndex(AssetManager.AssetSymbolMap.get("BTC").getInteger("id"));
											addressBtc.setAddressName("BTC");
											addressBtc.setAddress(bttAndEthUrlRes.getString("bitcoin"));
											addressBtc.setStatus("1");
											addressBtc.setType("2");
											s = HibernateUtil.getSessionFactory().openSession();
											s.beginTransaction();
											s.save(addressBtc);
											s.getTransaction().commit();
											s.close();
										}
										hibernate.hbm.Address addressCmt=new hibernate.hbm.Address();
										addressCmt.setUserId(email);
										addressCmt.setAssetId(AssetManager.AssetSymbolMap.get("CMT").getInteger("id")+"");
										addressCmt.setShowIndex(AssetManager.AssetSymbolMap.get("CMT").getInteger("id"));
										addressCmt.setAddressName("CMT");
										addressCmt.setAddress(bttAndEthUrlRes.getString("cmt"));
										addressCmt.setStatus("1");
										addressCmt.setType("2");
										s = HibernateUtil.getSessionFactory().openSession();
										s.beginTransaction();
										s.save(addressCmt);
										s.getTransaction().commit();
										s.close();
										
										hibernate.hbm.Address addressEth=new hibernate.hbm.Address();
										addressEth.setUserId(email);
										addressEth.setAssetId(AssetManager.AssetSymbolMap.get("ETH").getInteger("id")+"");
										addressEth.setShowIndex(AssetManager.AssetSymbolMap.get("ETH").getInteger("id"));
										addressEth.setAddressName("ETH");
										addressEth.setAddress(bttAndEthUrlRes.getString("ethereum"));
										addressEth.setStatus("1");
										addressEth.setType("2");
										s = HibernateUtil.getSessionFactory().openSession();
										s.beginTransaction();
										s.save(addressEth);
										s.getTransaction().commit();
										s.close();
										if(AssetManager.AssetSymbolMap.containsKey("USDT")){
											hibernate.hbm.Address addressUsdt=new hibernate.hbm.Address();
											addressUsdt.setUserId(email);
											addressUsdt.setAssetId(AssetManager.AssetSymbolMap.get("USDT").getInteger("id")+"");
											addressUsdt.setShowIndex(AssetManager.AssetSymbolMap.get("USDT").getInteger("id"));
											addressUsdt.setAddressName("USDT");
											addressUsdt.setAddress(bttAndEthUrlRes.getString("bitcoin"));
											addressUsdt.setStatus("1");
											addressUsdt.setType("2");
											s = HibernateUtil.getSessionFactory().openSession();
											s.beginTransaction();
											s.save(addressUsdt);
											s.getTransaction().commit();
											s.close();
										}
									}
								}
							}
							// 发送邮件
							AmazonSESUtil.sendTextEmail(params.getString("type"), email,lang);
							UsersManager.SendEmailSuccess(email);
						}
					}else{
						// 发送邮件
						AmazonSESUtil.sendTextEmail(params.getString("type"), email,lang);
						UsersManager.SendEmailSuccess(email);
					}
				}
			}
		};

		log4j.info("start2");
		redis.subscribe(jedisPubSub, "sendEmail");
		while (true) {

		}
	}
}