package com.autonavi.collect.entity;

import java.io.Serializable;

/**
 * 申诉封装
 * @author xuyaming
 *
 */
public class CollectTaskReAuditEntity implements Serializable {
	private static final long serialVersionUID = -974532000541659620L;
	private Long basePackageId;
	private Long userId;
	
	private String userName;
	private Integer currentStatus;
	
	private Long ownerId;
	private Boolean isLocked=false;
	public Long getBasePackageId() {
		return basePackageId;
	}
	public void setBasePackageId(Long basePackageId) {
		this.basePackageId = basePackageId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(Integer currentStatus) {
		this.currentStatus = currentStatus;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	

}
