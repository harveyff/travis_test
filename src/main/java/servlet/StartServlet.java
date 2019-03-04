package servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import manager.AssetManager;
import manager.DepositManager;
import manager.RedisThread;
import manager.UsersManager;
import manager.WithdrawManager;
import redis.MyJedisPool;
import utils.AmazonSESUtil;
import utils.HttpRequest;


public class StartServlet extends HttpServlet {
	public static Logger log4j = LogManager.getLogger(StartServlet.class);
	private static final long serialVersionUID = 1L;
	@Override
	public void init(ServletConfig config) throws ServletException {
		log4j.info("StartServlet......");
		super.init(config);
		HttpRequest.init();
		MyJedisPool.init();
		UsersManager.init();
		BaseServlet._init();
		AssetManager.init();
		DepositManager.init();
		WithdrawManager.init();
		
		AmazonSESUtil.init();
		try {
			RedisThread redisThread = new RedisThread();
			redisThread.start();
		} catch (Exception e) {
			log4j.info(new Date() + "RedisThread catch err........................");
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	public void destroy() {

	}

}