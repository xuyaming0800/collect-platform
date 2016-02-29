package com.autonavi.collect.entity;

public class CollectUserCacheEntity<T> {
	private String userName;
	private Long userId;
	private Boolean isFromDb=false;
	private Integer cacheType;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Boolean getIsFromDb() {
		return isFromDb;
	}
	public void setIsFromDb(Boolean isFromDb) {
		this.isFromDb = isFromDb;
	}
	public Integer getCacheType() {
		return cacheType;
	}
	public void setCacheType(Integer cacheType) {
		this.cacheType = cacheType;
	}
	

}
