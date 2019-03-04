package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
public class ChartSymbol_infoServlet extends BaseServlet {

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
		

		JSONObject res = new JSONObject();
		PrintWriter out = response.getWriter();
		JSONObject args = new JSONObject();
		Enumeration pNames = request.getParameterNames();
		while (pNames.hasMoreElements()) {
			String name = (String) pNames.nextElement();
			String value = request.getParameter(name);
			args.put(name, value);
		}
		
		log4j.info(new Date()+"sysmbol_info:"+args.toJSONString());
		if(args.getInteger("from")!=null&&args.getInteger("from")<1519833600){
			res.put("s", "no_data");
			res.put("nextTime", 1519833600);
		}
		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}

	}

}