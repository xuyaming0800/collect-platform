package com.autonavi.collect.entity;

public class CheckPackageUpdateEntity {
	private Long taskPackageCount;
	private Long taskProcessCount;
	private Long id;
	private Long passivePackageId;
	private Integer status;
	
	
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPassivePackageId() {
		return passivePackageId;
	}
	public void setPassivePackageId(Long passivePackageId) {
		this.passivePackageId = passivePackageId;
	}
	public Long getTaskPackageCount() {
		return taskPackageCount;
	}
	public void setTaskPackageCount(Long taskPackageCount) {
		this.taskPackageCount = taskPackageCount;
	}
	public Long getTaskProcessCount() {
		return taskProcessCount;
	}
	public void setTaskProcessCount(Long taskProcessCount) {
		this.taskProcessCount = taskProcessCount;
	}
	
	

}
