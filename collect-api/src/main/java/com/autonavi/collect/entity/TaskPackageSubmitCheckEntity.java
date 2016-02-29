package com.autonavi.collect.entity;

public class TaskPackageSubmitCheckEntity {
	private Long taskPackageId;
	private Integer countSubmit;
	private Integer countAll;

	public Long getTaskPackageId() {
		return taskPackageId;
	}

	public void setTaskPackageId(Long taskPackageId) {
		this.taskPackageId = taskPackageId;
	}

	public Integer getCountSubmit() {
		return countSubmit;
	}

	public void setCountSubmit(Integer countSubmit) {
		this.countSubmit = countSubmit;
	}

	public Integer getCountAll() {
		return countAll;
	}

	public void setCountAll(Integer countAll) {
		this.countAll = countAll;
	}

}
