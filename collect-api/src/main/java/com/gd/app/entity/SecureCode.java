package com.gd.app.entity;

public class SecureCode implements ServletInfoBean{
	private String errorMessage;
	private String userName;
	private String code;
	private String flagStr;
	private String batch;
	
	
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFlagStr() {
		return flagStr;
	}
	public void setFlagStr(String flagStr) {
		this.flagStr = flagStr;
	}
	
	

}
