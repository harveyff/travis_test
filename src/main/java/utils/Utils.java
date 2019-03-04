package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class Utils {
	public static boolean isDug = true;


	public final static int ANNOUNCE_DURATION_MINUTES = 3;

	public static double getNumDouble(double num) {
		BigDecimal b = new BigDecimal(num);
		double f1 = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	public static int getInt(JSONObject ob, String key, int de) {
		try {
			int k = Integer.parseInt((String) ob.get(key));
			return k;
		} catch (Exception e) {
			try {
				int k2 = ((Long) ob.get(key)).intValue();
				return k2;
			} catch (Exception e2) {
				try {
					int k3 = (int) ob.get(key);
					return k3;
				} catch (Exception e3) {
					// log4j.info("error getint " + key + " ");
					// e3.printStackTrace();
					return de;
				}
			}
		}

	}

	public static int getInt(JSONArray ob, int index, int de) {
		if (ob.size() <= index) {
			return de;
		}

		try {
			int k = Integer.parseInt((String) ob.get(index));
			return k;
		} catch (Exception e) {
			try {
				int k2 = ((Long) ob.get(index)).intValue();
				return k2;
			} catch (Exception e2) {
				try {
					int k3 = (int) ob.get(index);
					return k3;
				} catch (Exception e3) {
					// log4j.info("error getint " + index + " ");
					// e3.printStackTrace();
					return de;
				}
			}
		}

	}

	public static int getInt(JSONObject ob, String key) {
		return getInt(ob, key, -1);
	}

	public static double getDouble(JSONObject ob, String key) {
		try {
			double k = Double.parseDouble((String) ob.get(key));
			return k;
		} catch (Exception e) {
			try {
				double k3 = (double) ob.get(key);
				return k3;
			} catch (Exception e3) {
				// log4j.info("error getint " + key + " ");
				// e3.printStackTrace();
				return -1;
			}
		}
	}

	public static String getString(JSONObject ob, String key, String de) {
		if (ob.containsKey(key)) {
			if (isStringEmpty((String) ob.get(key))) {
				return "";
			}
			return (String) ob.get(key);
		} else {
			return de;
		}
	}

	public static int getCreatetime() {
		Long l = (long) ((new Date()).getTime() / 1000l);
		return l.intValue();
	}

	public static int getDatetime(Date date) {
		Long l = (long) (date.getTime() / 1000l);
		return l.intValue();
	}

	public static int getNextAnnounceSeconds(Date now) {

		int m = now.getMinutes();
		int s = now.getSeconds();

		if (s == 0 && m % ANNOUNCE_DURATION_MINUTES == 0) {
			return 0;
		}

		m = m + 1;
		while (m % ANNOUNCE_DURATION_MINUTES != 0) {
			m = m + 1;
		}

		int tm = m - now.getMinutes() - 1;
		int ss = (60 - s) + tm * 60;

		return ss;
	}

	public static Date getYesterday(Date now) {
		// Date now = new Date();
		// now.setHours(0);
		// now.setMinutes(0);
		// now.setSeconds(0);

		long t = now.getTime() - 1000;

		Date yesterday = new Date(t);
		yesterday.setHours(0);
		yesterday.setMinutes(0);
		yesterday.setSeconds(0);

		return yesterday;
	}

	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	public static Date transForDate(long time) {
		Date d = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			time = (long) time * 1000;
			d = format.parse(format.format(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;

	}

	public static String transForDate(Integer ms, String format) {
		if (ms == null) {
			ms = 0;
		}
		if (format == null) {
			format = "yyyy-MM-dd";
		}
		long msl = (long) ms * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String str = null;
		if (ms != null) {
			try {
				str = sdf.format(msl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public static boolean isStringEmpty(String b) {
		if (b == null || b.length() == 0) {
			return true;
		}

		return false;
	}

	/*
	 */
	public static String dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/*
	 */
	public static String stampToDate(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

	public static String timeStamp2Date(String seconds, String format) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty())
			format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds + "000")));
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static double formatDouble3(double d) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.000");
		return Double.parseDouble(df.format(d));
	}

	public static String getIpAddress(HttpServletRequest request) throws IOException {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	public static String parseENumToString(String num) {
		if (Utils.isStringEmpty(num)) {
			return "";
		} else {
			String numStr = new BigDecimal(num).toPlainString();
			if (numStr.indexOf(".") > 0) {
				numStr = numStr.replaceAll("0+?$", "").replaceAll("[.]$", "");
			}
			return numStr;
		}
	}
}
