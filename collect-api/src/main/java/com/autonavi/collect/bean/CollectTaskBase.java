package com.autonavi.collect.bean;

public class CollectTaskBase implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 5175396983261905903L;
	private Long id;
	private Long passiveId;
	private String dataName;
	private String taskName;
	private String collectDataName;
	private String verifyDataName;
	private Integer taskType;
	private Integer taskStatus;
	private Integer verifyStatus;
	private Long createTime;
	private Long updateTime;
	private Long allotUserId;
	private Long allotEndTime;
	private Long collectUserId;
	private Long taskSubmitTime;
	private Long taskSaveTime;
	private Long taskSaveEndTime;
	private String deviceInfo;

	private Long taskPackageId;

	private Integer releaseFreezeTime=0;
	private Long taskClazzId;
	private Long ownerId;

	public Integer getReleaseFreezeTime() {
		return releaseFreezeTime;
	}

	public void setReleaseFreezeTime(Integer releaseFreezeTime) {
		this.releaseFreezeTime = releaseFreezeTime;
	}

	// Constructors

	public Long getTaskPackageId() {
		return taskPackageId;
	}

	public void setTaskPackageId(Long taskPackageId) {
		this.taskPackageId = taskPackageId;
	}

	/** default constructor */
	public CollectTaskBase() {
	}

	/** minimal constructor */
	public CollectTaskBase(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectTaskBase(Long id, Long passiveId, String taskName,
			String collectDataName, String verifyDataName, Integer taskType,
			Integer taskStatus, Integer verifyStatus, Long createTime,
			Long updateTime, Long allotUserId, Long allotEndTime,
			Long collectUserId, Long taskSubmitTime, Long taskSaveTime) {
		this.id = id;
		this.passiveId = passiveId;
		this.taskName = taskName;
		this.collectDataName = collectDataName;
		this.verifyDataName = verifyDataName;
		this.taskType = taskType;
		this.taskStatus = taskStatus;
		this.verifyStatus = verifyStatus;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.allotUserId = allotUserId;
		this.allotEndTime = allotEndTime;
		this.collectUserId = collectUserId;
		this.taskSubmitTime = taskSubmitTime;
		this.taskSaveTime = taskSaveTime;
	}

	public Long getTaskSaveEndTime() {
		return taskSaveEndTime;
	}

	public void setTaskSaveEndTime(Long taskSaveEndTime) {
		this.taskSaveEndTime = taskSaveEndTime;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPassiveId() {
		return passiveId;
	}

	public void setPassiveId(Long passiveId) {
		this.passiveId = passiveId;
	}

	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getCollectDataName() {
		return this.collectDataName;
	}

	public void setCollectDataName(String collectDataName) {
		this.collectDataName = collectDataName;
	}

	public String getVerifyDataName() {
		return this.verifyDataName;
	}

	public void setVerifyDataName(String verifyDataName) {
		this.verifyDataName = verifyDataName;
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

	public Integer getVerifyStatus() {
		return this.verifyStatus;
	}

	public void setVerifyStatus(Integer verifyStatus) {
		this.verifyStatus = verifyStatus;
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
		return this.allotUserId;
	}

	public void setAllotUserId(Long allotUserId) {
		this.allotUserId = allotUserId;
	}

	public Long getAllotEndTime() {
		return this.allotEndTime;
	}

	public void setAllotEndTime(Long allotEndTime) {
		this.allotEndTime = allotEndTime;
	}

	public Long getCollectUserId() {
		return this.collectUserId;
	}

	public void setCollectUserId(Long collectUserId) {
		this.collectUserId = collectUserId;
	}

	public Long getTaskSubmitTime() {
		return this.taskSubmitTime;
	}

	public void setTaskSubmitTime(Long taskSubmitTime) {
		this.taskSubmitTime = taskSubmitTime;
	}

	public Long getTaskSaveTime() {
		return this.taskSaveTime;
	}

	public void setTaskSaveTime(Long taskSaveTime) {
		this.taskSaveTime = taskSaveTime;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
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
	

}