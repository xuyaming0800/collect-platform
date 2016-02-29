package com.autonavi.collect.entity;

import java.util.ArrayList;
import java.util.List;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.bean.CollectTaskToken;

/**
 * 保存任务封装类
 * @author xuyaming
 *
 */
public class CollectTaskSaveEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2927350042096864935L;
	private CollectTaskBase collectTaskBase;
	private CollectPassiveTask collectPassiveTask;
	private CollectTaskToken collectTaskToken;
	private CollectBasePackage collectBasePackage;
	private List<CollectTaskImg> collectTaskImgList=new ArrayList<CollectTaskImg>();
	private List<TaskExtraInfoEntity> taskExtraInfoEntityList=new ArrayList<TaskExtraInfoEntity>();
	private Long newTaskClazzId;
	private Long currentTaskClazzId;
	private Boolean isNew;
	
	
	public CollectBasePackage getCollectBasePackage() {
		return collectBasePackage;
	}
	public void setCollectBasePackage(CollectBasePackage collectBasePackage) {
		this.collectBasePackage = collectBasePackage;
	}
	public CollectTaskBase getCollectTaskBase() {
		return collectTaskBase;
	}
	public void setCollectTaskBase(CollectTaskBase collectTaskBase) {
		this.collectTaskBase = collectTaskBase;
	}
	public CollectPassiveTask getCollectPassiveTask() {
		return collectPassiveTask;
	}
	public void setCollectPassiveTask(CollectPassiveTask collectPassiveTask) {
		this.collectPassiveTask = collectPassiveTask;
	}
	public CollectTaskToken getCollectTaskToken() {
		return collectTaskToken;
	}
	public void setCollectTaskToken(CollectTaskToken collectTaskToken) {
		this.collectTaskToken = collectTaskToken;
	}
	public List<CollectTaskImg> getCollectTaskImgList() {
		return collectTaskImgList;
	}
	public void setCollectTaskImgList(List<CollectTaskImg> collectTaskImgList) {
		this.collectTaskImgList = collectTaskImgList;
	}
	public Long getNewTaskClazzId() {
		return newTaskClazzId;
	}
	public void setNewTaskClazzId(Long newTaskClazzId) {
		this.newTaskClazzId = newTaskClazzId;
	}
	public Long getCurrentTaskClazzId() {
		return currentTaskClazzId;
	}
	public void setCurrentTaskClazzId(Long currentTaskClazzId) {
		this.currentTaskClazzId = currentTaskClazzId;
	}
	public List<TaskExtraInfoEntity> getTaskExtraInfoEntityList() {
		return taskExtraInfoEntityList;
	}
	public void setTaskExtraInfoEntityList(
			List<TaskExtraInfoEntity> taskExtraInfoEntityList) {
		this.taskExtraInfoEntityList = taskExtraInfoEntityList;
	}
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	

}
