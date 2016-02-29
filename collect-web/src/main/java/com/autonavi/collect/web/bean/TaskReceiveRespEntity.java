package com.autonavi.collect.web.bean;

public class TaskReceiveRespEntity {
	private String dataName="";
	private String taskId="";
	private String baseId="";
	private String packageName="";
	private String endTime="";
	private String basePackageId="";
	
	
	
	public String getBasePackageId() {
		return basePackageId;
	}
	public void setBasePackageId(String basePackageId) {
		this.basePackageId = basePackageId;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getBaseId() {
		return baseId;
	}
	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}
}
