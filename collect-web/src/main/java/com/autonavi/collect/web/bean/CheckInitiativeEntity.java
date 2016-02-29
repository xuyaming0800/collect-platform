package com.autonavi.collect.web.bean;

public class CheckInitiativeEntity {
	private String collectClassId;
	private String x;
	private String y;
	private Boolean checkResult;
	private Long userId;
	private Integer checkType;
	private String baseId;
	private Long ownerId;
	private Long batchId;
	private String imageFlag;
	private Integer radius;
	
	
	
	
	
	public Integer getRadius() {
		return radius;
	}
	public void setRadius(Integer radius) {
		this.radius = radius;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public String getImageFlag() {
		return imageFlag;
	}
	public void setImageFlag(String imageFlag) {
		this.imageFlag = imageFlag;
	}
	public Integer getCheckType() {
		return checkType;
	}
	public void setCheckType(Integer checkType) {
		this.checkType = checkType;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getCollectClassId() {
		return collectClassId;
	}
	public void setCollectClassId(String collectClassId) {
		this.collectClassId = collectClassId;
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
	public Boolean getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(Boolean checkResult) {
		this.checkResult = checkResult;
	}
	public String getBaseId() {
		return baseId;
	}
	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	

}
