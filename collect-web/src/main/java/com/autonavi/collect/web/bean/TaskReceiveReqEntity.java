package com.autonavi.collect.web.bean;
/**
 * 领取任务请求封装类
 * @author xuyaming
 *
 */
public class TaskReceiveReqEntity {
	private Long userId;
	private Long basePackageId;
	private Long taskPackageId;
	private Long ownerId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getBasePackageId() {
		return basePackageId;
	}
	public void setBasePackageId(Long basePackageId) {
		this.basePackageId = basePackageId;
	}
	public Long getTaskPackageId() {
		return taskPackageId;
	}
	public void setTaskPackageId(Long taskPackageId) {
		this.taskPackageId = taskPackageId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	
	
	

}
