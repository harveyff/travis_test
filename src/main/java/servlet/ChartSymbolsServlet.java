package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import utils.Utils;

public class ChartSymbolsServlet extends BaseServlet {

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
		// response.setContentType("application/json;charset=GBK");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		JSONObject res = new JSONObject();
		String symbol = request.getParameter("symbol");
		if (Utils.isStringEmpty(symbol)) {
			res.put("s", "error");
			res.put("errmsg", "symbols not exists");
		}else{
			res.put("name", symbol);
			res.put("exchange-traded", "");
			res.put("exchange-listed", "");
			res.put("timezone", "Asia/Shanghai");
			res.put("minmovement", 1);
			res.put("minmovement2", 1);
			res.put("session", "0930-1630");
			res.put("has_intraday", true);
			res.put("has_no_volume", false);
			res.put("description", "");
			res.put("type", "stock");
			res.put("pricescale", 1000000);
			res.put("ticker", symbol);
		}
		PrintWriter out = response.getWriter();

		try {
			out.write(res.toJSONString());
		} finally {
			out.close();
		}

	}

}