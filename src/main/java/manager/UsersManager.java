package manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import com.alibaba.fastjson.JSONObject;

import google.authenticator.util.GoogleAuthenticatorUtils;
import gt3.sdk.GeetestLib;
import gt3.src.GeetestConfig;
import hibernate.HibernateUtil;
import hibernate.hbm.Users;
import jwt.JsonWebTokenService;
import jwt.support.JsonWebTokenServiceImpl;
import redis.RedisUtil;
import utils.DesUtil;
import utils.Utils;

public class UsersManager {
	public static Logger log4j = LogManager.getLogger(UsersManager.class);
	private static JsonWebTokenService jwtService;
	public static void init() {
		jwtService = new JsonWebTokenServiceImpl();
	}

	/**
	 * 检查是否有权发邮件
	 * 
	 * @param ip
	 *            单IP每天最多发10封邮件
	 * @return
	 */
	public static boolean canSendEmail(String ip) {
		int count = 0;
		String sendEmailCount = RedisUtil.getKV("emailCount_" + ip);
		if (Utils.isStringEmpty(sendEmailCount)) {
			return true;
		}
		count = Integer.parseInt(sendEmailCount);
		if (count >= 500) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 邮件发送成功后续事件
	 * 
	 * @param ip
	 */
	public static void SendEmailSuccess(String ip) {
		Long count = RedisUtil.inceKV("emailCount_" + ip);
		if (count == 1) {
			RedisUtil.expireKV("emailCount_" + ip, 60 * 60 * 24);
		}
	}

	public static JSONObject requestRegister(HttpServletRequest request, JSONObject args) {
		log4j.info(new Date() + ":requestRegister,args:" + args.toJSONString());
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "loginPwd", "");
		String lang = Utils.getString(args, "lang", "tr");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(pwd)) {
			res.put("code", 100001);
			return res;
		}
		if (!canSendEmail(email)) {
			res.put("code", 200001);
			return res;
		}
		int gtResult = checkGTCode(request, args);
		if (gtResult == 0) {
			res.put("code", 200000);
			res.put("msg", "err request.");
			return res;
		}
		Users user = getUserByEmail(email);
		if (user != null) {
			res.put("code", 100002);
			return res;
		}
		try {
			// 保存用户
			pwd = DesUtil.MD5(pwd);
			Users users = new Users();
			users.setEmail(email);
			users.setLoginPwd(pwd);
			users.setStatus("0");
			users.setBankBindStatus("0");
			users.setKycStatus("0");
			users.setGoogleAuthStatus("0");
			users.setBankBindStatus("0");
			users.setCreateAt(new Date().getTime() / 1000);
			Session s = null;
			s = HibernateUtil.getSessionFactory().openSession();
			s.beginTransaction();
			s.save(users);
			s.getTransaction().commit();
			s.close();
			res.put("code", 0);

			// 提交发送邮件任务
			JSONObject params = new JSONObject();
			params.put("type", "register");
			params.put("email", email);
			params.put("lang", lang);
			RedisUtil.publish(params.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("code", 0);
		return res;
	}

	/**
	 * 重新请求发送激活邮件
	 * 
	 * @param request
	 * @param args
	 * @return
	 */
	public static JSONObject rerequestRegister(HttpServletRequest request, JSONObject args) {
		String email = Utils.getString(args, "email", "");
		String lang = Utils.getString(args, "lang", "tr");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email)) {
			res.put("code", 100001);
			return res;
		}
		if (!canSendEmail(email)) {
			res.put("code", 200001);
			return res;
		}
		int gtResult = checkGTCode(request, args);
		if (gtResult == 0) {
			res.put("code", 200000);
			res.put("msg", "err request.");
			return res;
		}
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		if (user.getStatus().equals("0")) {
			// 请求发送邮件
			try {
				// 提交发送邮件任务
				JSONObject params = new JSONObject();
				params.put("type", "reregister");
				params.put("email", email);
				params.put("lang", lang);
				RedisUtil.publish(params.toJSONString());
				res.put("code", 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.put("code", 0);
		} else {
			res.put("code", 100003);
		}
		return res;
	}

	public static JSONObject requestForgot(JSONObject args) {
		log4j.info(new Date() + ":requestForgot,args:" + args.toJSONString());
		String email = Utils.getString(args, "email", "");
		String lang = Utils.getString(args, "lang", "tr");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email)) {
			res.put("code", 100001);
			return res;
		}
		if (!canSendEmail(email)) {
			res.put("code", 200001);
			return res;
		}
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		// 请求发送邮件
		try {
			// 提交发送邮件任务
			JSONObject params = new JSONObject();
			params.put("type", "forgot");
			params.put("email", email);
			params.put("lang", lang);
			RedisUtil.publish(params.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("code", 0);
		return res;
	}

	public static JSONObject requestCapitalForgot(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		String lang = Utils.getString(args, "lang", "tr");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email)) {
			res.put("code", 100001);
			return res;
		}
		if (!canSendEmail(email)) {
			res.put("code", 200001);
			return res;
		}
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		// 请求发送邮件
		try {
			// 提交发送邮件任务
			JSONObject params = new JSONObject();
			params.put("type", "forgot_capital");
			params.put("email", email);
			params.put("lang", lang);
			RedisUtil.publish(params.toJSONString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("code", 0);
		return res;
	}

	public static JSONObject register(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "capitalPwd", "");
		String code = Utils.getString(args, "code", "");

		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(pwd) || Utils.isStringEmpty(code)) {
			res.put("code", 100001);
			return res;
		}
		pwd = DesUtil.MD5(pwd);
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		if (user.getLoginPwd().equals(pwd)) {
			res.put("code", 100013);
			return res;
		}
		String esCode = RedisUtil.getKV("register_" + email);
		if (Utils.isStringEmpty(esCode) || !esCode.equals(code)) {
			res.put("code", 100009);
			return res;
		}
		// 保存用户
		user.setCapitalPwd(pwd);
		user.setStatus("1");
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.update(user);
		s.getTransaction().commit();
		s.close();
		RedisUtil.delKV("register_" + email);
		String userToken = jwtService.genToken(email, 60 * 24 * 2);
		RedisUtil.setKV("userToken_" + email, 60 * 60 * 24 * 2, userToken);
		res.put("userToken", userToken);
		res.put("code", 0);
		return res;
	}

	public static JSONObject forgot(JSONObject args) {
		log4j.info(new Date() + "forgot,args:" + args.toJSONString());
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "loginPwd", "");
		String code = Utils.getString(args, "code", "");
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(pwd) || Utils.isStringEmpty(code)) {
			res.put("code", 100001);
			return res;
		}
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		String esCode = RedisUtil.getKV("forgot_" + email);
		if (Utils.isStringEmpty(esCode) || !esCode.equals(code)) {
			res.put("code", 100009);
			return res;
		}
		// 保存用户
		user.setEmail(email);
		pwd = DesUtil.MD5(pwd);
		user.setLoginPwd(pwd);
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.update(user);
		s.getTransaction().commit();
		s.close();
		RedisUtil.delKV("forgot_" + email);
		String userToken = jwtService.genToken(email, 60 * 24 * 2);
		RedisUtil.setKV("userToken_" + email, 60 * 60 * 24 * 2, userToken);
		res.put("userToken", userToken);
		res.put("code", 0);
		return res;
	}

	public static JSONObject forgotCapital(JSONObject args) {
		log4j.info(new Date() + ",forgotCapital，args:" + args.toJSONString());
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "capitalPwd", "");
		String code = Utils.getString(args, "code", "");
		String googleAuthCode = Utils.getString(args, "googleAuthCode", "");
		pwd = DesUtil.MD5(pwd);
		JSONObject res = new JSONObject();
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(pwd) || Utils.isStringEmpty(code)) {
			res.put("code", 100001);
			return res;
		}
		Users user = getUserByEmail(email);
		if (user == null) {
			res.put("code", 100002);
			return res;
		}
		if (user.getLoginPwd().equals(pwd)) {
			res.put("code", 100013);
			return res;
		}
		boolean success = GoogleAuthenticatorUtils.verify(user.getGoogleAuthSecret(), googleAuthCode);
		if (success != true) {
			res.put("code", 100014);
			return res;
		}
		String esCode = RedisUtil.getKV("forgot_capital_" + email);
		if (Utils.isStringEmpty(esCode) || !esCode.equals(code)) {
			res.put("code", 100009);
			return res;
		}
		// 保存用户
		user.setEmail(email);
		user.setCapitalPwd(pwd);
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.update(user);
		s.getTransaction().commit();
		s.close();
		RedisUtil.delKV("forgot_capital_" + email);
		res.put("code", 0);

		return res;
	}

	public static JSONObject testAuth(JSONObject args) {
		JSONObject res = new JSONObject();
		return res;
	}
	public static JSONObject adminLogin(JSONObject args) {
		log4j.info(new Date() + ",adminLogin,args:" + args.toJSONString());
		JSONObject res = new JSONObject();
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "password", "");
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(email)) {
			res.put("code", 100001);
			return res;
		}
		String status = "0";
		Users user = getUserByEmailAndPwd(email, pwd);
		if (user == null) {
			status = "0";
			res.put("code", 100002);
			return res;
		}
		if(!email.equals("zhangbaoliang@bytetrade.io")&&!email.equals("pengpeng@bytetrade.io")&&!email.equals("harvey@bytetrade.io")&&!email.equals("fanshen@bytetrade.io")){
			res.put("code", 300200);
			return res;
		}
		status = "1";
		// 获取usertoken
		String userToken = jwtService.genToken(email, 60 * 24 * 7);
		RedisUtil.setKV("userToken_" + email, 60 * 60 * 24 * 7, userToken);
		res.put("userToken", userToken);
		res.put("userStatus", user.getStatus());
		if (Utils.isStringEmpty(user.getBttUserid())) {
			res.put("bttUserid", "");
		} else {
			res.put("bttUserid", user.getBttUserid());
		}
		res.put("kycStatus", user.getKycStatus());
		res.put("bankBindStatus", user.getBankBindStatus());
		res.put("googleAuthStatus", user.getGoogleAuthStatus());
		res.put("code", 0);
		res.put("channel", "newton");
		args.put("status", status);
		LoginLogManager.saveLog(args);
		return res;
	}
	public static JSONObject login(JSONObject args) {
		log4j.info(new Date() + ",login,args:" + args.toJSONString());
		JSONObject res = new JSONObject();
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "password", "");
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(email)) {
			res.put("code", 100001);
			return res;
		}
		String status = "0";
		Users user = getUserByEmailAndPwd(email, pwd);
		if (user == null) {
			status = "0";
			res.put("code", 100002);
			return res;
		}
		status = "1";
		// 获取usertoken
		String userToken = jwtService.genToken(email, 60 * 24 * 2);
		RedisUtil.setKV("userToken_" + email, 60 * 60 * 24 * 2, userToken);
		res.put("userToken", userToken);
		res.put("userStatus", user.getStatus());
		if (Utils.isStringEmpty(user.getBttUserid())) {
			res.put("bttUserid", "");
		} else {
			res.put("bttUserid", user.getBttUserid());
		}
		res.put("kycStatus", user.getKycStatus());
		res.put("bankBindStatus", user.getBankBindStatus());
		res.put("googleAuthStatus", user.getGoogleAuthStatus());
		res.put("code", 0);
		res.put("channel", "newton");
		args.put("status", status);
		LoginLogManager.saveLog(args);
		return res;
	}

	public static Users getUserByEmail(String email) {
		Users user = null;
		Session s = null;
		try {
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Users> crq = crb.createQuery(Users.class);
			Root<Users> root = crq.from(Users.class);
			crq.select(root);
			crq.where(crb.equal(root.get("email"), email));
			List<Users> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				user = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}

		return user;
	}

	public static Users getUserByEmailAndPwd(String email, String pwd) {
		Users user = null;
		Session s = null;
		try {
			pwd = DesUtil.MD5(pwd);
			s = HibernateUtil.getSessionFactory().openSession();
			CriteriaBuilder crb = s.getCriteriaBuilder();
			CriteriaQuery<Users> crq = crb.createQuery(Users.class);
			Root<Users> root = crq.from(Users.class);
			crq.select(root);
			List<Predicate> predicates = new ArrayList<Predicate>();
			predicates.add(crb.equal(root.get("email"), email));
			predicates.add(crb.equal(root.get("loginPwd"), pwd));
			crq.where(predicates.toArray(new Predicate[predicates.size()]));
			List<Users> list = s.createQuery(crq).getResultList();
			if (list.size() > 0) {
				user = list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}

		return user;
	}

	public static JSONObject createBttUserParams(String address, String userid) {
		JSONObject res = new JSONObject();
		JSONObject account = new JSONObject();
		JSONObject owner = new JSONObject();
		JSONObject address_auths_value = new JSONObject();
		address_auths_value.put(address, 100);
		owner.put("weight_threshold", 100);
		owner.put("account_auths", new JSONObject());
		owner.put("key_auths", new JSONObject());
		owner.put("address_auths", address_auths_value);

		JSONObject active = new JSONObject();
		active.put("weight_threshold", 100);
		active.put("account_auths", new JSONObject());
		active.put("key_auths", new JSONObject());

		JSONObject address_auths_value1 = new JSONObject();
		address_auths_value1.put(address, 100);
		owner.put("weight_threshold", 100);
		active.put("address_auths", address_auths_value1);
		account.put("id", userid);
		account.put("owner", owner);
		account.put("active", active);
		res.put("account", account.toJSONString());
		return res;
	}

	public static int checkGTCode(HttpServletRequest request, JSONObject args) {
		GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
				GeetestConfig.isnewfailback());

		String challenge = Utils.getString(args, GeetestLib.fn_geetest_challenge, "");
		String validate = Utils.getString(args, GeetestLib.fn_geetest_validate, "");
		String seccode = Utils.getString(args, GeetestLib.fn_geetest_seccode, "");

		// 从session中获取gt-server状态
		// int gt_server_status_code = (Integer)
		// request.getSession().getAttribute(gtSdk.gtServerStatusSessionKey);

		// 从session中获取userid
		// String userid = (String)request.getSession().getAttribute("userid");
		int gt_server_status_code = 1;
		String userid = "17611192019";
		// 自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("user_id", userid); // 网站用户id
		param.put("client_type", "web"); // web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
		param.put("ip_address", "127.0.0.1"); // 传输用户请求验证时所携带的IP

		int gtResult = 0;

		if (gt_server_status_code == 1) {
			// gt-server正常，向gt-server进行二次验证

			gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
			log4j.info(gtResult);
		} else {
			// gt-server非正常情况下，进行failback模式验证

			log4j.info("failback:use your own server captcha validate");
			gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
		}

		return gtResult;
	}
}
