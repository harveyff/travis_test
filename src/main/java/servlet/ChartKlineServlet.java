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

import utils.HttpRequest;
public class ChartKlineServlet extends BaseServlet {

	private static final long serialVersionUID = -1915463532411657451L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Content-type", "application/json;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");
		//response.setContentType("application/json;charset=GBK");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, String> paramsMap=new HashMap<String, String>();
		Enumeration pNames = request.getParameterNames();
		while (pNames.hasMoreElements()) {
			String name = (String) pNames.nextElement();
			String value = request.getParameter(name);
			paramsMap.put(name, value);
		}
		paramsMap.put("source", request.getRequestURI());
		JSONObject res = JSONObject.parseObject(HttpRequest.sendPost("1",paramsMap));

		PrintWriter out = response.getWriter();

		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}

	}

}