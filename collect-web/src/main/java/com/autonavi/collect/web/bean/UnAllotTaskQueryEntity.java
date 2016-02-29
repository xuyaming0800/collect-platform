package com.autonavi.collect.web.bean;

public class UnAllotTaskQueryEntity {
	private Long userId;
	private Integer adcode;
	private Integer page;
	private Integer size;
	private Double x;
	private Double y;
	private Integer radis;
	private Integer queryType=0;
	private String isGPS="1";
	private String allotStatus="0";
	private Long ownerId;
	private Long collectClassId;
		
	public String getAllotStatus() {
		return allotStatus;
	}
	public void setAllotStatus(String allotStatus) {
		this.allotStatus = allotStatus;
	}
	public String getIsGPS() {
		return isGPS;
	}
	public void setIsGPS(String isGPS) {
		this.isGPS = isGPS;
	}
	public Integer getQueryType() {
		return queryType;
	}
	public void setQueryType(Integer queryType) {
		this.queryType = queryType;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getAdcode() {
		return adcode;
	}
	public void setAdcode(Integer adcode) {
		this.adcode = adcode;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
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
	public Integer getRadis() {
		return radis;
	}
	public void setRadis(Integer radis) {
		this.radis = radis;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getCollectClassId() {
		return collectClassId;
	}
	public void setCollectClassId(Long collectClassId) {
		this.collectClassId = collectClassId;
	}
	
	

}
