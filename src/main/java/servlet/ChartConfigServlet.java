package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class ChartConfigServlet extends BaseServlet {

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
		res.put("supports_search", true);
		res.put("supports_group_request", false);
		res.put("supports_marks", true);
		res.put("supports_timescale_marks", true);
		res.put("supports_time", true);
		
		JSONArray exchanges = new JSONArray();
		res.put("exchanges", exchanges);
		JSONArray symbols_types = new JSONArray();
		res.put("symbols_types", symbols_types);
		res.put("supported_resolutions", new String[]{"1","5","15","30","60","240","1D","5D","1W","1M"});
		PrintWriter out = response.getWriter();

		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}

	}

}