package com.autonavi.collect.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTaskQueryEntity {
	private Long userId;
	private Integer taskStatus;
	private Long basePackageId;
	private Long taskPackageId;
	private Integer page;
	private Integer size;
	private Integer queryType=0;
	private Long baseId;
	private Boolean isPassive=null;
	
	private String level="";
	private Long batchId=0L;
	
	private String userName;
	
	private List<Long> basePackageIdList=new ArrayList<Long>();
	
	private Map<Long,Long> basePackageIdMap=new HashMap<Long,Long>();
	
	private Long ownerId;
	
	private Long collectClassId;
	
	
	
	public Long getCollectClassId() {
		return collectClassId;
	}
	public void setCollectClassId(Long collectClassId) {
		this.collectClassId = collectClassId;
	}
	public Long getBaseId() {
		return baseId;
	}
	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}
	public Long getBasePackageId() {
		return basePackageId;
	}
	public void setBasePackageId(Long basePackageId) {
		this.basePackageId = basePackageId;
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
	public Integer getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
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
	public Boolean getIsPassive() {
		return isPassive;
	}
	public void setIsPassive(Boolean isPassive) {
		this.isPassive = isPassive;
	}
	public List<Long> getBasePackageIdList() {
		return basePackageIdList;
	}
	public void setBasePackageIdList(List<Long> basePackageIdList) {
		this.basePackageIdList = basePackageIdList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public Long getTaskPackageId() {
		return taskPackageId;
	}
	public void setTaskPackageId(Long taskPackageId) {
		this.taskPackageId = taskPackageId;
	}
	public Map<Long, Long> getBasePackageIdMap() {
		return basePackageIdMap;
	}
	public void setBasePackageIdMap(Map<Long, Long> basePackageIdMap) {
		this.basePackageIdMap = basePackageIdMap;
	}
	
	

}
