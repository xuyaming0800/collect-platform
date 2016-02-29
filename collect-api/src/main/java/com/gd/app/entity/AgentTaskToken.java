package com.gd.app.entity;

import java.util.Date;

public class AgentTaskToken implements ServletInfoBean  {
	private static final long serialVersionUID = -4514046679121071359L;
	private String id;
	private String tokenId;
	private String userName;
	private String imgMd5;
	private String x;
	private String y;
	private String header;
	private Date createTime;
	private Date updateTime;
	private String token;
	private Long baseId;
	private String errorMessage;
	private String imgId;
	private int status;
	private String ip;
	
	
	
	
	
	

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getImgId() {
		return imgId;
	}

	public void setImgId(String imgId) {
		this.imgId = imgId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getImgMd5() {
		return imgMd5;
	}

	public void setImgMd5(String imgMd5) {
		this.imgMd5 = imgMd5;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getBaseId() {
		return baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

}
