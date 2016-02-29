package com.autonavi.collect.web.bean;

public class ShapeInfoEntity {
	private Long userId;
	private Double x;
	private Double y;
	private Long taskClazzId;
	private Long ownerId;
	private Boolean isValid;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Long getTaskClazzId() {
		return taskClazzId;
	}
	public void setTaskClazzId(Long taskClazzId) {
		this.taskClazzId = taskClazzId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Boolean getIsValid() {
		return isValid;
	}
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}
	

}
