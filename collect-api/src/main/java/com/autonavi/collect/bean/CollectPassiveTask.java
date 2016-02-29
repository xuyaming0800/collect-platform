package com.autonavi.collect.bean;

public class CollectPassiveTask implements java.io.Serializable, Cloneable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -2391819284218648278L;
	private Long id;
	private String dataName="需采集的内容(默认)";
	private String collectDataName="";
	private Integer taskType=0;
	private Integer taskStatus=0;
	private Long createTime;
	private Long updateTime;
	private Long allotUserId=null;
	private Long collectUserId=null;
	private Long allotEndTime=null;
	
	private String poi=null;
	private String prename=null;
	
	private Long taskPackageId;
	private Integer releaseFreezeTime=12;
	private Integer verifyStatus=null;
	
	private Long ownerId=null;
	private Long taskClazzId=null;
	
	
	
	

	public Integer getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public Integer getReleaseFreezeTime() {
		return releaseFreezeTime;
	}

	public void setReleaseFreezeTime(Integer releaseFreezeTime) {
		this.releaseFreezeTime = releaseFreezeTime;
	}

	public Long getTaskPackageId() {
		return taskPackageId;
	}

	public void setTaskPackageId(Long taskPackageId) {
		this.taskPackageId = taskPackageId;
	}

	public Long getAllotEndTime() {
		return allotEndTime;
	}

	public void setAllotEndTime(Long allotEndTime) {
		this.allotEndTime = allotEndTime;
	}

	public String getDataName() {
		return dataName;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	private String verifyDataName;

	public String getVerifyDataName() {
		return verifyDataName;
	}

	public void setVerifyDataName(String verifyDataName) {
		this.verifyDataName = verifyDataName;
	}


	public String getCollectDataName() {
		return collectDataName;
	}

	public void setCollectDataName(String collectDataName) {
		this.collectDataName = collectDataName;
	}



	// Constructors

	/** default constructor */
	public CollectPassiveTask() {
	}

	/** minimal constructor */
	public CollectPassiveTask(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectPassiveTask(Long id, String taskName, Integer taskType, Integer taskStatus, Long createTime, Long updateTime,  Long allotUserId,
			Long collectUserId) {
		this.id = id;
		this.dataName = taskName;
		this.taskType = taskType;
		this.taskStatus = taskStatus;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.allotUserId = allotUserId;
		this.collectUserId = collectUserId;
	}

	// Property accessors
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTaskType() {
		return this.taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getTaskStatus() {
		return this.taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}


	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}


	public Long getAllotUserId() {
		return allotUserId;
	}

	public void setAllotUserId(Long allotUserId) {
		this.allotUserId = allotUserId;
	}

	public Long getCollectUserId() {
		return collectUserId;
	}

	public void setCollectUserId(Long collectUserId) {
		this.collectUserId = collectUserId;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getTaskClazzId() {
		return taskClazzId;
	}

	public void setTaskClazzId(Long taskClazzId) {
		this.taskClazzId = taskClazzId;
	}
	
	

}