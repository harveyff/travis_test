package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import utils.Utils;

public class BaseServlet extends HttpServlet {
	public static void _init() {
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
	
}