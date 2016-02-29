package com.gd.app.util;

import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jiayi.zhang
 * 
 */

public class PushHttpClientUtil {

	private static Logger log = LoggerFactory.getLogger(PushHttpClientUtil.class);

	private HttpClient httpClient = new HttpClient();

	public PushHttpClientUtil() {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		httpClient.setHttpConnectionManager(connectionManager);
		HttpClientParams params = httpClient.getParams();
		params.setContentCharset("UTF-8");
	}

	public String sendPost(String url, Map<String, String> params) {
		String result = null;
		PostMethod post = new PostMethod(url);
		for (String key : params.keySet()) {
			post.addParameter(new NameValuePair(key, params.get(key) == null ? "" : params.get(key)));
		}
		try {
			int status = httpClient.executeMethod(post);
			if (status == HttpStatus.SC_OK) {
				result = post.getResponseBodyAsString();
			}
		} catch (Exception e) {
			log.error("获取远程数据失败：" + url, e);
		} finally {
			post.releaseConnection();
		}
		return result;
	}

	public String sendPost(String url) {
		String result = null;
		PostMethod post = new PostMethod(url);
		try {
			int status = httpClient.executeMethod(post);
			if (status == HttpStatus.SC_OK) {
				result = post.getResponseBodyAsString();
			}
		} catch (Exception e) {
			log.error("获取远程数据失败：" + url, e);
		} finally {
			post.releaseConnection();
		}
		return result;
	}
}
