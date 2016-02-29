package com.gd.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestParseUtil {

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> argsToMap(HttpServletRequest request) {
		Map<String, String> hashMap = new HashMap<String, String>();
		String method = request.getMethod();
		if (method.equals("GET")) {
			getRec(request, hashMap);
		} else {
			postRec(request, hashMap);
		}
		return hashMap;
	}

	/**
	 * 
	 * @param request
	 * @param hashMap
	 */
	private static void getRec(HttpServletRequest request, Map<String, String> hashMap) {

		Enumeration enu = request.getParameterNames();
		String tempk = null;
		String tempv = null;
		try {
			while (enu.hasMoreElements()) {
				tempk = (String) enu.nextElement();
				tempv = (String) request.getParameter(tempk);
				tempv = strFilter(urlDecode(tempv, "UTF-8"));
				if (!tempv.equals("")) {
					hashMap.put(tempk, tempv);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request
	 * @param hashMap
	 */
	private static void postRec(HttpServletRequest request, Map<String, String> hashMap) {
		StringBuffer postContent = new StringBuffer();
		try {
			InputStream inputStream = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = reader.readLine()) != null) {
				postContent.append(line);
			}
			reader.close();
			inputStream.close();
			String params = postContent.toString();
			String[] kvs = params.split("&");
			for (String kv : kvs) {
				String[] p = kv.split("=");
				// if (p.length == 2) {
				if (p.length != 0 && p.length != 1) {
					int index = kv.indexOf("=");
					String temp = kv.substring(index + 1);
					hashMap.put(p[0], strFilter(URLDecoder.decode(temp, "UTF-8")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 去掉字符前后多余的空格
	 * 
	 * @param oldstr
	 * @return String
	 */
	private static String strFilter(String oldstr) {
		String newstr = oldstr;
		return newstr.trim();
	}

	/**
	 * 转码
	 * 
	 * @param par
	 * @param charset
	 * @return String
	 */
	private static String urlDecode(String par, String encode) {
		if (par != null && !par.equals("")) {
			try {
				byte[] temp = par.getBytes("iso-8859-1");
				par = new String(temp, encode);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return par;
	}

	public static String getAbsoluteUrl(HttpServletRequest request) {
		String absoluteUrl = "";
		try {
			String encode = request.getParameter("encode");
			if (encode == null || encode.equals("")) {
				encode = "UTF-8";
			}
			absoluteUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + request.getServletPath() + "?" + request.getQueryString();
			absoluteUrl = java.net.URLDecoder.decode(absoluteUrl, encode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return absoluteUrl;
	}

}
