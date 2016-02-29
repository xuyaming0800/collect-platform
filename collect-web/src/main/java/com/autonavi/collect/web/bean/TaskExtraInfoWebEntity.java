package com.autonavi.collect.web.bean;

import java.util.ArrayList;
import java.util.List;

import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.entity.TaskExtraInfoEntity;

public class TaskExtraInfoWebEntity {
	List<TaskExtraInfoEntity> taskExtraInfoEntityList=new ArrayList<TaskExtraInfoEntity>();
	List<CollectTaskImg> collectTaskImgList=new ArrayList<CollectTaskImg>();
	String operation;
	String level;
	String batchId;
	public List<TaskExtraInfoEntity> getTaskExtraInfoEntityList() {
		return taskExtraInfoEntityList;
	}
	public void setTaskExtraInfoEntityList(
			List<TaskExtraInfoEntity> taskExtraInfoEntityList) {
		this.taskExtraInfoEntityList = taskExtraInfoEntityList;
	}
	public List<CollectTaskImg> getCollectTaskImgList() {
		return collectTaskImgList;
	}
	public void setCollectTaskImgList(List<CollectTaskImg> collectTaskImgList) {
		this.collectTaskImgList = collectTaskImgList;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	

}
