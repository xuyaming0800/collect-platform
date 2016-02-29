package com.autonavi.collect.entity;

import java.io.Serializable;

public class ActiveTaskAroundSearchResultEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5728443079319855229L;
	private long clazzId;
	private long userId;
	private int verifyStatus;
	private long releaseFreezeTime;
	private long updateTime;
	private String collectDataName;
	private String location;
	private int taskStatus;
	private Long taskId;
	private Integer imageStatus=0;
	
	

	public Integer getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(Integer imageStatus) {
		this.imageStatus = imageStatus;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public long getClazzId() {
		return clazzId;
	}

	public void setClazzId(long clazzId) {
		this.clazzId = clazzId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public long getReleaseFreezeTime() {
		return releaseFreezeTime;
	}

	public void setReleaseFreezeTime(long releaseFreezeTime) {
		this.releaseFreezeTime = releaseFreezeTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getCollectDataName() {
		return collectDataName;
	}

	public void setCollectDataName(String collectDataName) {
		this.collectDataName = collectDataName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}
}
