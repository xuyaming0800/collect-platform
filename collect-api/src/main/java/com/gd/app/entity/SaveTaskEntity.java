package com.gd.app.entity;

public class SaveTaskEntity implements ServletInfoBean {
	private String taskId;
	private String tokenId;
	private String baseId;
	private String userName;
	private String dataName;
	private String coordinateX;
	private String coordinateY;
	private String adCode;
	private String deviceInfo;
	private String scoreId;
	private String errorMessage;
	private String recommend;
	
	

	
	

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(String coordinateX) {
		this.coordinateX = coordinateX;
	}

	public String getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(String coordinateY) {
		this.coordinateY = coordinateY;
	}

	public String getAdCode() {
		return adCode;
	}

	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getScoreId() {
		return scoreId;
	}

	public void setScoreId(String scoreId) {
		this.scoreId = scoreId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
