package com.autonavi.collect.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author chunsheng.zhang
 *
 */
public class SearchTaskQueryEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6563980723970915279L;

	private double x;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	private double y;

	private Integer range = 1000;

	private Long userId;

	/**
	 * 页数
	 */
	private Integer page;

	/**
	 * 每页条数
	 */
	private Integer number;

	private String adcode;

	/**
	 * 自定义查询
	 */
	private Map customMap;

	private String indexName;

	private String dataType;
	
	private Long ownerId;
	
	private Long collectCLazzId;
	
	private Integer taskStatus;

	// public String getX() {
	// return x;
	// }
	//
	// public void setX(String x) {
	// this.x = x;
	// }
	//
	// public String getY() {
	// return y;
	// }
	//
	// public void setY(String y) {
	// this.y = y;
	// }

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

	public Map getCustomMap() {
		return customMap;
	}

	public void setCustomMap(Map customMap) {
		this.customMap = customMap;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getCollectCLazzId() {
		return collectCLazzId;
	}

	public void setCollectCLazzId(Long collectCLazzId) {
		this.collectCLazzId = collectCLazzId;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	
	

}
