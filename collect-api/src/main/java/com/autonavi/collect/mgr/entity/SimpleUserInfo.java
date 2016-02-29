package com.autonavi.collect.mgr.entity;

import java.io.Serializable;

public class SimpleUserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8604405825326672685L;
	private Long id;
	private String userName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
