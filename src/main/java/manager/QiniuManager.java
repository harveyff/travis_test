package manager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.util.Auth;

import utils.Utils;

public class QiniuManager {
	public static String accessKey = "5kbCTcz4FprrkZD9vt0CadvuImVcw8pEeNSSbndH";
	public static String secretKey = "HvBdzSLR65-B9zcSnP68VfvbXJ5wZ7FOlklRE1hS";
	public static String bucket = "newton";
	public static String domainOfBucket = "http://newton.baoxianshenqi.cn";
	public static String commonDomainOfBucket = "https://static1.baoxianshenqi.cn";
	public static String commonBucket = "static";

	public static JSONObject upToken(JSONObject args) {
		String fileName = Utils.getString(args, "fileName", "");
		JSONObject res = new JSONObject();
		res.put("code", 0);
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket, fileName);
		res.put("upToken", upToken);
		return res;
	}

	public static JSONObject commonQiniuUptoken(JSONObject args) {
		String fileName = Utils.getString(args, "fileName", "");
		JSONObject res = new JSONObject();
		res.put("code", 0);
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(commonBucket, fileName);
		res.put("upToken", upToken);
		return res;
	}

	public static JSONObject commonDownUrl(JSONObject args) {
		JSONObject res = new JSONObject();
		res.put("code", 0);
		String fileName = Utils.getString(args, "fileName", "");
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String finalUrl = String.format("%s/%s", commonDomainOfBucket, encodedFileName);
		res.put("url", finalUrl);
		return res;
	}

	public static JSONObject downUrl(JSONObject args) {
		JSONObject res = new JSONObject();
		res.put("code", 0);
		String fileName = Utils.getString(args, "fileName", "");
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			res.put("code", 100000);
			e.printStackTrace();
		}
		String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
		Auth auth = Auth.create(accessKey, secretKey);
		long expireInSeconds = 60;// 30s，可以自定义链接过期时间
		String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
		res.put("url", finalUrl);
		return res;
	}

}
