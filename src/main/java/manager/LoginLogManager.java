package manager;

import java.util.Date;

import org.hibernate.Session;
import com.alibaba.fastjson.JSONObject;
import hibernate.HibernateUtil;
import hibernate.hbm.LoginLog;
import utils.DesUtil;
import utils.Utils;

public class LoginLogManager {
	

	public static void saveLog(JSONObject args) {
		String email = Utils.getString(args, "email", "");
		String pwd = Utils.getString(args, "capitalPwd", "");
		String status = Utils.getString(args, "status", "");
		String ip = Utils.getString(args, "ip", "");
		LoginLog loginLog = new LoginLog();
		loginLog.setEmail(email);
		loginLog.setLoginPwd(DesUtil.MD5(pwd));
		loginLog.setIp(ip);
		loginLog.setTime((int)(new Date().getTime()/1000));
		loginLog.setStatus(status);
		Session s = null;
		s = HibernateUtil.getSessionFactory().openSession();
		s.beginTransaction();
		s.save(loginLog);
		s.getTransaction().commit();
		s.close();
	}
}
