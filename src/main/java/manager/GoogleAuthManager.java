package manager;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import com.alibaba.fastjson.JSONObject;
import google.authenticator.util.GoogleAuthenticatorUtils;
import hibernate.HibernateUtil;
import hibernate.hbm.Users;
import redis.RedisUtil;
import utils.Utils;

public class GoogleAuthManager {
	public static Logger log4j = LogManager.getLogger(GoogleAuthManager.class);
	public static JSONObject googleAuthCreate(JSONObject args) {
		log4j.info(new Date() + ",googleAuthCreate,args:" + args.toJSONString());
		JSONObject res = new JSONObject();
		String email = Utils.getString(args, "email", "");
		Users user = UsersManager.getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		String issuer = "coinnewton";
		// 生成密钥
		String secretKey = GoogleAuthenticatorUtils.createSecretKey();
		String googleAuthQRCodeData = GoogleAuthenticatorUtils.createGoogleAuthQRCodeData(secretKey,email, issuer);
		JSONObject googleAuthMap = new JSONObject();
		googleAuthMap.put("secret", secretKey);
		googleAuthMap.put("url", googleAuthQRCodeData);
		RedisUtil.setKV("google_auth_" + email, googleAuthMap.toJSONString());
		res.put("code", 0);
		res.put("data", googleAuthQRCodeData);
		res.put("secretKey", secretKey);
		return res;
	}

	public static JSONObject googleAuthVerify(JSONObject args) {
		log4j.info(new Date() + ",googleAuthVerify,args:" + args.toJSONString());
		String email = Utils.getString(args, "email", "");
		String code = Utils.getString(args, "code", "");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(code)) {
			res.put("code", 100001);
			return res;
		}
		Users user = UsersManager.getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		String googleAuthString = RedisUtil.getKV("google_auth_" + email);
		if (Utils.isStringEmpty(googleAuthString)) {
			res.put("code", 100001);
			return res;
		}
		JSONObject googleAuthMap = JSONObject.parseObject(googleAuthString);
		boolean success = GoogleAuthenticatorUtils.verify(googleAuthMap.getString("secret"), code);
		res.put("code", 0);
		res.put("status", success);
		if (success == true) {
			user.setGoogleAuthStatus("1");
			user.setGoogleAuthSecret(googleAuthMap.getString("secret"));
			user.setGoogleAuthUrl(googleAuthMap.getString("url"));
			user.setUpdateAt(new Date().getTime() / 1000);
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.update(user);
			s.getTransaction().commit();
			s.close();
		}
		return res;
	}

}
