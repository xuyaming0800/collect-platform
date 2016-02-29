package com.autonavi.collect.bean;


/**
 * CollectPassivePackage entity. @author MyEclipse Persistence Tools
 */
public class CollectPassivePackage implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 5374003114652626199L;
	private Long id;
	private String taskPackageName;
	private String taskPackageDesc;
	private Integer taskPackageCount=1;
	private Double taskPackagePay=0.0D;
	private Integer taskPackageStatus=0;
	private Integer taskPackageType=0;
	private String taskPackageCate;
	private Long createTime;
	private Long updateTime;
	private Long allotUserId=null;
	private Long collectUserId=null;
	private Long allotEndTime=null;
	private Integer verifyMaintainTime=5*24;
	private Integer allotMaintainTime=5*24;
	private Integer verifyFreezeTime=24;
	private Integer releaseFreezeTime=24;
	private Integer taskPackageVerifyStatus=null;
	private Long ownerId=null;
	private Long taskClazzId;

	// Constructors

	/** default constructor */
	public CollectPassivePackage() {
	}

	/** minimal constructor */
	public CollectPassivePackage(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectPassivePackage(Long id, String taskPackageName,
			String taskPackageDesc, Integer taskPackageCount,
			Double taskPackagePay, Integer taskPackageStatus,
			Integer taskPackageType, Long createTime, Long updateTime,
			Long allotUserId, Long collectUserId, Long allotEndTime) {
		this.id = id;
		this.taskPackageName = taskPackageName;
		this.taskPackageDesc = taskPackageDesc;
		this.taskPackageCount = taskPackageCount;
		this.taskPackagePay = taskPackagePay;
		this.taskPackageStatus = taskPackageStatus;
		this.taskPackageType = taskPackageType;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.allotUserId = allotUserId;
		this.collectUserId = collectUserId;
		this.allotEndTime = allotEndTime;
	}

	// Property accessors

	
	
	public Long getId() {
		return this.id;
	}

	public Integer getTaskPackageVerifyStatus() {
		return taskPackageVerifyStatus;
	}

	public void setTaskPackageVerifyStatus(Integer taskPackageVerifyStatus) {
		this.taskPackageVerifyStatus = taskPackageVerifyStatus;
	}

	public String getTaskPackageCate() {
		return taskPackageCate;
	}

	public void setTaskPackageCate(String taskPackageCate) {
		this.taskPackageCate = taskPackageCate;
	}

	public Integer getVerifyFreezeTime() {
		return verifyFreezeTime;
	}

	public void setVerifyFreezeTime(Integer verifyFreezeTime) {
		this.verifyFreezeTime = verifyFreezeTime;
	}

	public Integer getVerifyMaintainTime() {
		return verifyMaintainTime;
	}

	public void setVerifyMaintainTime(Integer verifyMaintainTime) {
		this.verifyMaintainTime = verifyMaintainTime;
	}

	public Integer getAllotMaintainTime() {
		return allotMaintainTime;
	}

	public void setAllotMaintainTime(Integer allotMaintainTime) {
		this.allotMaintainTime = allotMaintainTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskPackageName() {
		return this.taskPackageName;
	}

	public void setTaskPackageName(String taskPackageName) {
		this.taskPackageName = taskPackageName;
	}

	public String getTaskPackageDesc() {
		return this.taskPackageDesc;
	}

	public void setTaskPackageDesc(String taskPackageDesc) {
		this.taskPackageDesc = taskPackageDesc;
	}

	public Integer getTaskPackageCount() {
		return this.taskPackageCount;
	}

	public void setTaskPackageCount(Integer taskPackageCount) {
		this.taskPackageCount = taskPackageCount;
	}

	public Double getTaskPackagePay() {
		return this.taskPackagePay;
	}

	public void setTaskPackagePay(Double taskPackagePay) {
		this.taskPackagePay = taskPackagePay;
	}

	public Integer getTaskPackageStatus() {
		return this.taskPackageStatus;
	}

	public void setTaskPackageStatus(Integer taskPackageStatus) {
		this.taskPackageStatus = taskPackageStatus;
	}


	public Integer getTaskPackageType() {
		return this.taskPackageType;
	}

	public void setTaskPackageType(Integer taskPackageType) {
		this.taskPackageType = taskPackageType;
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


	public Long getCollectUserId() {
		return this.collectUserId;
	}

	public void setCollectUserId(Long collectUserId) {
		this.collectUserId = collectUserId;
	}


	public Long getAllotEndTime() {
		return this.allotEndTime;
	}

	public void setAllotEndTime(Long allotEndTime) {
		this.allotEndTime = allotEndTime;
	}

	public Integer getReleaseFreezeTime() {
		return releaseFreezeTime;
	}

	public void setReleaseFreezeTime(Integer releaseFreezeTime) {
		this.releaseFreezeTime = releaseFreezeTime;
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