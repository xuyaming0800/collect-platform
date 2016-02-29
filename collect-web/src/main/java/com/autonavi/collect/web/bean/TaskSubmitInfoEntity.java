package com.autonavi.collect.web.bean;

import com.autonavi.collect.bean.CollectTaskBase;
/**
 * 页面任务提交封装类
 * @author xuyaming
 *
 */
public class TaskSubmitInfoEntity {
	private CollectTaskBase collectTaskBase;
	private String isLocate = "0";
	private String isBatch = "1";
	private String charset = "utf-8";
	private String pack;
	private String md5Validate;
	private String token;
	private String isLost="1";
	
	
	

	public String getIsLost() {
		return isLost;
	}

	public void setIsLost(String isLost) {
		this.isLost = isLost;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public CollectTaskBase getCollectTaskBase() {
		return collectTaskBase;
	}

	public void setCollectTaskBase(CollectTaskBase collectTaskBase) {
		this.collectTaskBase = collectTaskBase;
	}

	public String getIsLocate() {
		return isLocate;
	}

	public void setIsLocate(String isLocate) {
		this.isLocate = isLocate;
	}

	public String getIsBatch() {
		return isBatch;
	}

	public void setIsBatch(String isBatch) {
		this.isBatch = isBatch;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getMd5Validate() {
		return md5Validate;
	}

	public void setMd5Validate(String md5Validate) {
		this.md5Validate = md5Validate;
	}

}
