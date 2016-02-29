package com.autonavi.collect.entity;

import java.io.Serializable;

public class ActiveTaskAroundSearchEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6075610836942430079L;
	private long clazzId;
	private int radius;
	private double x;
	private double y;
	private Long userId;
	private Long baseId;
	private Integer number;
	private Long ownerId;
	private String imageFlag;
	private Long batchId;
	private Integer imageStatus;
	
	

	public Integer getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(Integer imageStatus) {
		this.imageStatus = imageStatus;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public long getClazzId() {
		return clazzId;
	}

	public void setClazzId(long clazzId) {
		this.clazzId = clazzId;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBaseId() {
		return baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getImageFlag() {
		return imageFlag;
	}

	public void setImageFlag(String imageFlag) {
		this.imageFlag = imageFlag;
	}
	
	
	
}
