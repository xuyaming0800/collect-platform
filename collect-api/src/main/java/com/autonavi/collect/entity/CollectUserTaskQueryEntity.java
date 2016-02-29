package com.autonavi.collect.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;

/**
 * 用户查询自己的任务封装类
 * 
 * @author xuyaming
 *
 */
public class CollectUserTaskQueryEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 278811314012014980L;
	private CollectTaskBase collectTaskBase;
	private CollectTaskBaseEntity collectTaskBaseEntity;
	private CollectBasePackage collectBasePackage;
	private Integer start;
	private Integer limit;
	private Boolean isPassive;
	private String collectClassId;
	private String collectClassCount;
	private Map<String,String> collectClassCounts=new HashMap<String,String>();
	//返回值部分
	private List<CollectTaskBaseEntity> collectTaskBaseList;
	private List<CollectBasePackageEntity> collectBasePackageList;
	private List<CollectTaskImg> collectTaskImgList;
	private Map<Long,CollectTaskClazz> collectTaskClazzMap=new HashMap<Long,CollectTaskClazz>();
	private Long total;
	
	
	private Long verifyPassCount;
	
	private Long verifyFailCount;
	
	private Map<Long,String> clazzTaskClazzJsonMap=new HashMap<Long,String>();
	
	private String level;
	private Long batchId;
	
	
	

	public List<CollectTaskImg> getCollectTaskImgList() {
		return collectTaskImgList;
	}

	public void setCollectTaskImgList(List<CollectTaskImg> collectTaskImgList) {
		this.collectTaskImgList = collectTaskImgList;
	}

	public CollectBasePackage getCollectBasePackage() {
		return collectBasePackage;
	}

	public void setCollectBasePackage(CollectBasePackage collectBasePackage) {
		this.collectBasePackage = collectBasePackage;
	}

	public List<CollectBasePackageEntity> getCollectBasePackageList() {
		return collectBasePackageList;
	}

	public void setCollectBasePackageList(
			List<CollectBasePackageEntity> collectBasePackageList) {
		this.collectBasePackageList = collectBasePackageList;
	}

	public List<CollectTaskBaseEntity> getCollectTaskBaseList() {
		return collectTaskBaseList;
	}

	public void setCollectTaskBaseList(List<CollectTaskBaseEntity> collectTaskBaseList) {
		this.collectTaskBaseList = collectTaskBaseList;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public CollectTaskBase getCollectTaskBase() {
		return collectTaskBase;
	}

	public void setCollectTaskBase(CollectTaskBase collectTaskBase) {
		this.collectTaskBase = collectTaskBase;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Map<Long, CollectTaskClazz> getCollectTaskClazzMap() {
		return collectTaskClazzMap;
	}

	public void setCollectTaskClazzMap(
			Map<Long, CollectTaskClazz> collectTaskClazzMap) {
		this.collectTaskClazzMap = collectTaskClazzMap;
	}

	public Long getVerifyPassCount() {
		return verifyPassCount;
	}

	public void setVerifyPassCount(Long verifyPassCount) {
		this.verifyPassCount = verifyPassCount;
	}

	public Long getVerifyFailCount() {
		return verifyFailCount;
	}

	public void setVerifyFailCount(Long verifyFailCount) {
		this.verifyFailCount = verifyFailCount;
	}

	public Boolean getIsPassive() {
		return isPassive;
	}

	public void setIsPassive(Boolean isPassive) {
		this.isPassive = isPassive;
	}

	public Map<Long, String> getClazzTaskClazzJsonMap() {
		return clazzTaskClazzJsonMap;
	}

	public void setClazzTaskClazzJsonMap(Map<Long, String> clazzTaskClazzJsonMap) {
		this.clazzTaskClazzJsonMap = clazzTaskClazzJsonMap;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public CollectTaskBaseEntity getCollectTaskBaseEntity() {
		return collectTaskBaseEntity;
	}

	public void setCollectTaskBaseEntity(CollectTaskBaseEntity collectTaskBaseEntity) {
		this.collectTaskBaseEntity = collectTaskBaseEntity;
	}

	public String getCollectClassId() {
		return collectClassId;
	}

	public void setCollectClassId(String collectClassId) {
		this.collectClassId = collectClassId;
	}

	public String getCollectClassCount() {
		return collectClassCount;
	}

	public void setCollectClassCount(String collectClassCount) {
		this.collectClassCount = collectClassCount;
	}

	public Map<String, String> getCollectClassCounts() {
		return collectClassCounts;
	}

	public void setCollectClassCounts(Map<String, String> collectClassCounts) {
		this.collectClassCounts = collectClassCounts;
	}
	
	
	

	
	

}
