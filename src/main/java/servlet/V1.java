package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONObject;

import hibernate.hbm.Users;
import manager.AssetManager;
import manager.DepositManager;
import manager.GoogleAuthManager;
import manager.KycManager;
import manager.LoveChainManager;
import manager.MyManager;
import manager.QiniuManager;
import manager.UsersManager;
import manager.WithdrawManager;
import utils.HttpRequest;
import utils.Utils;

public class V1 extends BaseServlet {

	private static final long serialVersionUID = -1915463532411657451L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Allow-Origin", "*");
		JSONObject args = new JSONObject();
		Enumeration pNames = request.getParameterNames();
		Map<String, String> paramsMap = new HashMap<String, String>();
		while (pNames.hasMoreElements()) {
			String name = (String) pNames.nextElement();
			String value = request.getParameter(name);
			args.put(name, value);
			paramsMap.put(name, value);
		}
		args.put("source", request.getRequestURI());
		String userToken = args.getString("userToken");
		String email = args.getString("email");
		JSONObject res = new JSONObject();
		try {
			String command = Utils.getString(args, "cmd", "");
			args.put("ip", Utils.getIpAddress(request));
			args.put("agent", request.getHeader("User-Agent"));
			if (command.equals("requestRegister")) {
				res = UsersManager.requestRegister(request, args);
			} else if (command.equals("rerequestRegister")) {
				res = UsersManager.rerequestRegister(request, args);
			} else if (command.equals("requestForgot")) {
				res = UsersManager.requestForgot(args);
			} else if (command.equals("requestCapitalForgot")) {
				if (isUserTokenValid(email, userToken)) {
					res = UsersManager.requestCapitalForgot(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("register")) {
				log4j.info(new Date() + ",register,args:" + args.toJSONString());
				res = UsersManager.register(args);
			} else if (command.equals("forgot")) {
				res = UsersManager.forgot(args);
			} else if (command.equals("forgotCapital")) {
				if (isUserTokenValid(email, userToken)) {
					res = UsersManager.forgotCapital(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("login")) {
				res = UsersManager.login(args);
			} else if (command.equals("8cb3dc0fa1c6994d_adminLogin")) {
				res = UsersManager.adminLogin(args);
			} else if (command.equals("marketsPrice")) {
				res = MyManager.marketsPrice(paramsMap);
			} else if (command.equals("init")) {
				res = MyManager.init(paramsMap);
			} else if (command.equals("listAccounts")) {
				if (isUserTokenValid(email, userToken)) {
					res = AssetManager.addressList(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("putTransaction")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.putTransaction(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("listDeposits")) {
				if (isUserTokenValid(email, userToken)) {
					res = DepositManager.list(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("listWithdraws")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.list(args);
				}
			} else if (command.equals("listAdminWithdraws")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.list(args);
				}
			} else if (command.equals("ListBusiness")) {
				if (isUserTokenValid(email, userToken)) {
					Users user = UsersManager.getUserByEmail(email);
					String userid = "";
					if (user != null) {
						userid = user.getBttUserid();
					}
					paramsMap.put("userid", userid);
					res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
				}
			} else if (command.equals("qiniuUptoken")) {
				if (isUserTokenValid(email, userToken)) {
					res = QiniuManager.upToken(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("commonQiniuUptoken")) {
				res = QiniuManager.commonQiniuUptoken(args);
			} else if (command.equals("commonQiniuDownUrl")) {
				res = QiniuManager.commonDownUrl(args);
			} else if (command.equals("qiniuDownUrl")) {
				if (isUserTokenValid(email, userToken)) {
					res = QiniuManager.downUrl(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("openOrders")) {
				if (isUserTokenValid(email, userToken)) {
					Users user = UsersManager.getUserByEmail(email);
					String userid = "";
					if (user != null) {
						userid = user.getBttUserid();
					}
					paramsMap.put("userid", userid);
					res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("finishedOrders")) {
				if (isUserTokenValid(email, userToken)) {
					Users user = UsersManager.getUserByEmail(email);
					String userid = "";
					if (user != null) {
						userid = user.getBttUserid();
					}
					paramsMap.put("userid", userid);
					res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("userDeals")) {
				if (isUserTokenValid(email, userToken)) {
					Users user = UsersManager.getUserByEmail(email);
					String userid = "";
					if (user != null) {
						userid = user.getBttUserid();
					}
					paramsMap.put("userid", userid);
					res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
				} else {
					res.put("code", 200200);
				}
			}
			// 充值地址管理
			else if (command.equals("listDepositsAddress")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.listDepositsAddress(args);
				} else {
					res.put("code", 200200);
				}
			}
			// 提现申请
			else if (command.equals("withdraw")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.withdrawApply(args);
				} else {
					res.put("code", 200200);
				}
			}
			// 提现取消
			else if (command.equals("withdrawCancel")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.withdrawCancel(args);
				} else {
					res.put("code", 200200);
				}
			}
			// 提现地址列表
			else if (command.equals("listWithdrawAddress")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.listWithdrawsAddress(args);
				} else {
					res.put("code", 200200);
				}
			}
			// 提现银行卡绑定/解绑
			else if (command.equals("bankCardBind")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.bankCardBind(args);
				} else {
					res.put("code", 200200);
				}
			}
			// 提现数字货币绑定/解绑
			else if (command.equals("tokenAddressBind")) {
				if (isUserTokenValid(email, userToken)) {
					res = WithdrawManager.tokenAddressBind(args);
				} else {
					res.put("code", 200200);
				}
			}
			// KYC相关接口
			else if (command.equals("kycStatus")) {
				if (isUserTokenValid(email, userToken)) {
					res = KycManager.kycStatus(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("kycSend")) {
				log4j.info(args);
				if (isUserTokenValid(email, userToken)) {
					res = KycManager.kycSend(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("kycList")) {
				if (isUserTokenAdminValid(email, userToken)) {
					res = KycManager.kycList(args);
				} else {
					res.put("code", 300200);
				}
			} else if (command.equals("kycApprove")) {
				if (isUserTokenAdminValid(email, userToken)) {
					res = KycManager.kycApprove(args);
				} else {
					res.put("code", 300200);
				}
			} else if (command.equals("depositNotify")) {
				String ip=args.getString("ip");
				log4j.info("depositNotify:" +ip);
				if(ip.equals("18.195.51.148")||ip.equals("184.73.124.140")){
				res = DepositManager.depositNotify(args);
				}else{
					log4j.warn("depositNotify err ip,args:"+args.toJSONString());
				}
			} else if (command.equals("googleAuthCreate")) {
				if (isUserTokenValid(email, userToken)) {
					res = GoogleAuthManager.googleAuthCreate(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("googleAuthVerify")) {
				if (isUserTokenValid(email, userToken)) {
					res = GoogleAuthManager.googleAuthVerify(args);
				} else {
					res.put("code", 200200);
				}
			} else if (command.equals("withdrawApplyList")) {
				if (isUserTokenAdminValid(email, userToken)) {
					res = WithdrawManager.withdrawApplyAdminList(args);
				} else {
					res.put("code", 300200);
				}
			} else if (command.equals("withdrawApprove")) {
				if (isUserTokenAdminValid(email, userToken)) {
					res = WithdrawManager.withdrawApprove(args);
				} else {
					res.put("code", 300200);
				}
			} else if (command.equals("loveChainSend")) {
				res = LoveChainManager.loveChainSend(args);
			} else if (command.equals("loveChainCreatePoster")) {
				res = LoveChainManager.loveChainCreatePoster(args);
			} else if (command.equals("tdayCreatePoster")) {
				res = LoveChainManager.tdayCreatePoster(args);
			} else {
				log4j.info(args);
				res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
			}
		} catch (Exception e) {
			res.put("code", 100000);
			log4j.info("............catch err:"+e.getMessage());
			e.printStackTrace();
			res.put("text", e.getMessage());
		}
		PrintWriter out = response.getWriter();
		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}

	}

}