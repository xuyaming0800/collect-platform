package com.autonavi.collect.bean;

public class CollectAllotBaseUser implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3246502010655482178L;
	private Long id;
	private Long baseId;
	private Long allotUserId;
	private Long packageId;
	
	private Integer status;
   
	
	
	public Long getPackageId() {
		return packageId;
	}
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBaseId() {
		return baseId;
	}
	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}
	public Long getAllotUserId() {
		return allotUserId;
	}
	public void setAllotUserId(Long allotUserId) {
		this.allotUserId = allotUserId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

}
