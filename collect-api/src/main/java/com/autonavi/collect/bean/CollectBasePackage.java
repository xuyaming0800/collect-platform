package com.autonavi.collect.bean;



public class CollectBasePackage implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4392792058362464566L;
	private Long id=null;
	private Long passivePackageId=null;
	private String taskPackageName;
	private String taskPackageDesc;
	private Integer taskPackageCount=1;
	private String taskPackageCate;
	private Double taskPackagePay=0.0D;
	private Integer taskPackageStatus=null;
	private Integer taskPackageType=null;
	private Long createTime=null;
	private Long updateTime=null;
	private Long allotUserId=null;
	private Long collectUserId=null;
	private Long allotEndTime=null;
	private Integer taskPackageVerifyStatus=null;
	private Integer verifyMaintainTime=5*24;
	private Integer allotMaintainTime=5*24;
	private Integer verifyFreezeTime=24;
	private Long taskClazzId=null;
	private Long ownerId=null;
	private Long submitTime=null;
	
	
	

	// Constructors

	/** default constructor */
	public CollectBasePackage() {
	}

	/** minimal constructor */
	public CollectBasePackage(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectBasePackage(Long id, Long passivePackageId,
			String taskPackageName, String taskPackageDesc,
			Integer taskPackageCount, Double taskPackagePay,
			Integer taskPackageStatus, Integer taskPackageType,
			Long createTime, Long updateTime, Long allotUserId,
			Long collectUserId, Long allotEndTime) {
		this.id = id;
		this.passivePackageId = passivePackageId;
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
	
	
	public Integer getVerifyMaintainTime() {
		return verifyMaintainTime;
	}

	public Integer getVerifyFreezeTime() {
		return verifyFreezeTime;
	}

	public void setVerifyFreezeTime(Integer verifyFreezeTime) {
		this.verifyFreezeTime = verifyFreezeTime;
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
	
	public Long getId() {
		return this.id;
	}

	public String getTaskPackageCate() {
		return taskPackageCate;
	}

	public void setTaskPackageCate(String taskPackageCate) {
		this.taskPackageCate = taskPackageCate;
	}

	public void setId(Long id) {
		this.id = id;
	}
    

	public Integer getTaskPackageVerifyStatus() {
		return taskPackageVerifyStatus;
	}

	public void setTaskPackageVerifyStatus(Integer taskPackageVerifyStatus) {
		this.taskPackageVerifyStatus = taskPackageVerifyStatus;
	}

	public Long getPassivePackageId() {
		return this.passivePackageId;
	}

	public void setPassivePackageId(Long passivePackageId) {
		this.passivePackageId = passivePackageId;
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

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}

	
	

}