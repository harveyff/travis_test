package manager;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utils.HttpRequest;
import utils.Utils;

public class MyManager {
	public static Logger log4j = LogManager.getLogger(RedisThread.class);
	public static JSONObject marketsPrice(Map<String, String> params) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("channel", "newton");
		if (!Utils.isStringEmpty(params.get("symbol"))) {
			paramsMap.put("symbol", params.get("symbol"));
		}
		paramsMap.put("cmd", "marketsPrice");
		String resultStr = HttpRequest.sendPost("0", paramsMap);
		JSONObject data = JSONObject.parseObject(resultStr);
		JSONObject res = new JSONObject();
		res.put("code", 0);
		JSONArray result = new JSONArray();
		if (data == null) {
			res.put("code", 100000);
			return res;
		}
		JSONArray arrays = data.getJSONArray("result");
		if (arrays != null) {
			for (int i = 0; i < arrays.size(); ++i) {
					result.add(arrays.get(i));
			}
		}
		res.put("lastPrice", data.getJSONArray("lastPrice"));
		res.put("result", result);
		return res;
	}

	public static JSONObject init(Map<String, String> paramsMap) {
		Long beginTime=new Date().getTime()/1000;
		paramsMap.put("channel", "newton");
		JSONObject res = JSONObject.parseObject(HttpRequest.sendPost("0", paramsMap));
		if (res == null) {
			res = new JSONObject();
			res.put("code", 100000);
		}
		Long endTime=new Date().getTime()/1000;
		log4j.info("use "+(endTime-beginTime));
		return res;
	}
}
