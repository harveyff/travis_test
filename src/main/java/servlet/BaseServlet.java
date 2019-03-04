package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import jwt.JsonWebTokenService;
import jwt.support.JsonWebTokenServiceImpl;
import redis.MyJedisPool;
import redis.RedisUtil;
import utils.Utils;

public class BaseServlet extends HttpServlet {
	private static JsonWebTokenService jwtService ;
	public static Logger log4j = LogManager.getLogger(BaseServlet.class);
	public static void _init() {
		jwtService = new JsonWebTokenServiceImpl();
	}
	public void returnNotValid(ServletResponse response, HttpServletResponse resp) throws IOException {
		JSONObject res = new JSONObject();
		res.put("code", 5);
		resp.setHeader("Content-type", "charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}
	}
	public static boolean isUserTokenValid(String email, String userToken) {
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(userToken)) {
			return false;
		} else {
			String user = jwtService.parseToken(userToken);
			if (Utils.isStringEmpty(user) || !user.equals(email) || !jwtService.isNotExpired(userToken)) {
				log4j.info(new Date()+",isUserTokenValid-false,email:"+email+",userToken:"+userToken);
				return false;
			} else {
				String rightUserToken = RedisUtil.getKV("userToken_" + email);
				if (Utils.isStringEmpty(rightUserToken) || !rightUserToken.equals(userToken)) {
					log4j.info(new Date()+",isUserTokenValid-false,email:"+email+",userToken:"+userToken);
					return false;
				} else {
					return true;
				}
			}
		}
	}
	public static boolean isUserTokenAdminValid(String email, String userToken) {
		if (Utils.isStringEmpty(email) || Utils.isStringEmpty(userToken)) {
			return false;
		} else {
			if(email.equals("zhengruyue@bytetrade.io")||email.equals("zhangbaoliang@bytetrade.io")||email.equals("pengpeng@bytetrade.io")||email.equals("harvey@bytetrade.io")||email.equals("fanshen@bytetrade.io")){
				String user = jwtService.parseToken(userToken);
				if (Utils.isStringEmpty(user) || !user.equals(email) || !jwtService.isNotExpired(userToken)) {
					log4j.info(new Date()+",isUserTokenAdminValid-false,email:"+email+",userToken:"+userToken);
					return false;
				} else {
					String rightUserToken = RedisUtil.getKV("userToken_" + email);
					if (Utils.isStringEmpty(rightUserToken) || !rightUserToken.equals(userToken)) {
						log4j.info(new Date()+",isUserTokenAdminValid-false,email:"+email+",userToken:"+userToken);
						return false;
					} else {
						return true;
					}
				}
				
			}else{
				return false;
			}
		}
	}
}