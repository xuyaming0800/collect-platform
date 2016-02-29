package com.autonavi.collect.entity;

public class TaskExtraInfoEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6211938280826062066L;
	private String operation;
	private String level;
	private Object value;
	private String batchId;
	private String collectClassId;
	private String collectClassName;
	private String status;
	private String money;
	private Boolean moneyChange;
	private String versionNo;
	private String customMoney;
	private String userMoney;
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
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getCollectClassId() {
		return collectClassId;
	}
	public void setCollectClassId(String collectClassId) {
		this.collectClassId = collectClassId;
	}
	public String getCollectClassName() {
		return collectClassName;
	}
	public void setCollectClassName(String collectClassName) {
		this.collectClassName = collectClassName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public Boolean getMoneyChange() {
		return moneyChange;
	}
	public void setMoneyChange(Boolean moneyChange) {
		this.moneyChange = moneyChange;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getCustomMoney() {
		return customMoney;
	}
	public void setCustomMoney(String customMoney) {
		this.customMoney = customMoney;
	}
	public String getUserMoney() {
		return userMoney;
	}
	public void setUserMoney(String userMoney) {
		this.userMoney = userMoney;
	}
	
	
	
	

}
