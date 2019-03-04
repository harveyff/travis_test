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
			args.put("agent", request.getHeader("User-Agent"));
			res.put("text", " hello "+Utils.getIpAddress(request));
		} catch (Exception e) {
			res.put("code", 100000);
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