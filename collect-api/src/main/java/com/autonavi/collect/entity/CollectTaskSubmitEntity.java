package com.autonavi.collect.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;

/**
 * 提交任务封装类
 * 
 * @author xuyaming
 * 
 */
public class CollectTaskSubmitEntity  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6215740353169317448L;
	private CollectPassiveTask CollectPassiveTask;
	private CollectTaskBase CollectTaskBase;
	private List<CollectTaskImg> CollectTaskImgList;
	private Map<Long,CollectTaskClazz> collectTaskClazzMap;
	private Map<String,String> batchIdLevelMap;
	private String token;
	private boolean isLost;
	private String userName;
	
	private Long newTaskClazzId;
	private Long currentTaskClazzId;
	
	private List<TaskExtraInfoEntity> taskExtraInfoEntityList=new ArrayList<TaskExtraInfoEntity>();
	
	
	
	
	
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

	private CollectBasePackage collectBasePackage;

	public CollectBasePackage getCollectBasePackage() {
		return collectBasePackage;
	}

	public void setCollectBasePackage(CollectBasePackage collectBasePackage) {
		this.collectBasePackage = collectBasePackage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isLost() {
		return isLost;
	}

	public void setLost(boolean isLost) {
		this.isLost = isLost;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public CollectPassiveTask getCollectPassiveTask() {
		return CollectPassiveTask;
	}

	public void setCollectPassiveTask(CollectPassiveTask collectPassiveTask) {
		CollectPassiveTask = collectPassiveTask;
	}

	public CollectTaskBase getCollectTaskBase() {
		return CollectTaskBase;
	}

	public void setCollectTaskBase(CollectTaskBase collectTaskBase) {
		CollectTaskBase = collectTaskBase;
	}

	public List<CollectTaskImg> getCollectTaskImgList() {
		return CollectTaskImgList;
	}

	public void setCollectTaskImgList(List<CollectTaskImg> collectTaskImgList) {
		CollectTaskImgList = collectTaskImgList;
	}

	public Map<Long, CollectTaskClazz> getCollectTaskClazzMap() {
		return collectTaskClazzMap;
	}

	public void setCollectTaskClazzMap(
			Map<Long, CollectTaskClazz> collectTaskClazzMap) {
		this.collectTaskClazzMap = collectTaskClazzMap;
	}

	public Map<String, String> getBatchIdLevelMap() {
		return batchIdLevelMap;
	}

	public void setBatchIdLevelMap(Map<String, String> batchIdLevelMap) {
		this.batchIdLevelMap = batchIdLevelMap;
	}

	public List<TaskExtraInfoEntity> getTaskExtraInfoEntityList() {
		return taskExtraInfoEntityList;
	}

	public void setTaskExtraInfoEntityList(
			List<TaskExtraInfoEntity> taskExtraInfoEntityList) {
		this.taskExtraInfoEntityList = taskExtraInfoEntityList;
	}

	

	

	
	
	

}
