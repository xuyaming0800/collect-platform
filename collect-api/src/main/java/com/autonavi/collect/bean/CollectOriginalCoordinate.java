package com.autonavi.collect.bean;


public class CollectOriginalCoordinate implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 2150968145212904696L;
	private Long id;
	private Long passiveId=null;
	private String taskSampleImg;
	private Double originalX;
	private Double originalY;
	private Integer originalAdcode;
	private Integer coordinateStatus=0;
	private Long packageId=null;
	
	
	

	// Constructors

	public Long getPackageId() {
		return packageId;
	}

	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}

	public Integer getCoordinateStatus() {
		return coordinateStatus;
	}

	public void setCoordinateStatus(Integer coordinateStatus) {
		this.coordinateStatus = coordinateStatus;
	}

	/** default constructor */
	public CollectOriginalCoordinate() {
	}

	/** minimal constructor */
	public CollectOriginalCoordinate(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectOriginalCoordinate(Long id, Long passiveId, Double originalX,
			Double originalY, Integer originalAdcode) {
		this.id = id;
		this.passiveId = passiveId;
		this.originalX = originalX;
		this.originalY = originalY;
		this.originalAdcode = originalAdcode;
	}

	// Property accessors
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


	public Double getOriginalX() {
		return this.originalX;
	}

	public void setOriginalX(Double originalX) {
		this.originalX = originalX;
	}

	public Double getOriginalY() {
		return this.originalY;
	}

	public void setOriginalY(Double originalY) {
		this.originalY = originalY;
	}

	public Integer getOriginalAdcode() {
		return this.originalAdcode;
	}

	public void setOriginalAdcode(Integer originalAdcode) {
		this.originalAdcode = originalAdcode;
	}

	public String getTaskSampleImg() {
		return taskSampleImg;
	}

	public void setTaskSampleImg(String taskSampleImg) {
		this.taskSampleImg = taskSampleImg;
	}
	
	

}