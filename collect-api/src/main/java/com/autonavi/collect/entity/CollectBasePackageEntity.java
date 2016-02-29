package com.autonavi.collect.entity;



public class CollectBasePackageEntity implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4512978053050194077L;
	private Long id;
	private Long passivePackageId;
	private String taskPackageName;
	private String taskPackageDesc;
	private Integer taskPackageCount;
	private String taskPackageCate;
	private Double taskPackagePay;
	private Integer taskPackageStatus;
	private Integer taskPackageType;
	private Long createTime;
	private Long updateTime;
	private Long allotUserId;
	private Long collectUserId;
	private Long allotEndTime;
	private Integer taskPackageVerifyStatus;
	
	private String imgUrl;
	private Double x;
	private Double y;
	
	private String collectClassName;
	private String collectClassId;
	private Integer collectClassPayType;
	
	private Long submitTime;
	

	// Constructors

	/** default constructor */
	public CollectBasePackageEntity() {
	}

	/** minimal constructor */
	public CollectBasePackageEntity(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectBasePackageEntity(Long id, Long passivePackageId,
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
	
	
	public Long getId() {
		return this.id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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

	public String getCollectClassName() {
		return collectClassName;
	}

	public void setCollectClassName(String collectClassName) {
		this.collectClassName = collectClassName;
	}

	public String getCollectClassId() {
		return collectClassId;
	}

	public void setCollectClassId(String collectClassId) {
		this.collectClassId = collectClassId;
	}

	public Integer getCollectClassPayType() {
		return collectClassPayType;
	}

	public void setCollectClassPayType(Integer collectClassPayType) {
		this.collectClassPayType = collectClassPayType;
	}

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}
	
	
	

}