package com.gd.app.aop;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

import com.gd.app.entity.Appeal;
import com.gd.app.util.DateUtil;
import com.gd.app.util.PushHttpClientUtil;

public class PushAop {
	
	private static Logger log = Logger.getLogger(PushAop.class);

	private String pushUrl;
	private String notifyContent;
	private PushHttpClientUtil httpClientUtil;

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public String getNotifyContent() {
		return notifyContent;
	}

	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}

	public PushHttpClientUtil getHttpClientUtil() {
		return httpClientUtil;
	}

	public void setHttpClientUtil(PushHttpClientUtil httpClientUtil) {
		this.httpClientUtil = httpClientUtil;
	}

	public void pushAfterReturning(JoinPoint joinPoint) throws Exception {
		String time = DateUtil.getCurrentDateTime();
		StringBuffer validateBuffer = new StringBuffer();
		validateBuffer.append("msgpush03").append(":");
		validateBuffer.append("msgpush012").append(":");
		validateBuffer.append(time);
		String token = string2MD5(validateBuffer.toString());
		Map<String, String> params = new HashMap<String, String>();
		params.put("time", time);
		params.put("token", token);
		params.put("content", this.notifyContent);
		params.put("title", "申诉处理结果");
		log.info("content:" + this.notifyContent);
		params.put("actionType", "10");
		log.info("actionType:10");
		params.put("bizType", "3");
		log.info("bizType:3");
		params.put("bizType", "3");
		Object param = joinPoint.getArgs()[0];
		if (param instanceof Appeal) {
			Appeal appeal = (Appeal) param;
			params.put("users", appeal.getUserName());
			log.info("users:" + params.get("users"));
		}
		String result = httpClientUtil.sendPost(pushUrl, params);
		log.info("推送响应：" + result);
	}

	public String string2MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();

	}
}
