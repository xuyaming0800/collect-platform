package com.autonavi.collect.entity;

public class CollectTokenCheckEntity implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5335737517755714698L;
	private String token;
	private Boolean isNew;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	
	
	
}
