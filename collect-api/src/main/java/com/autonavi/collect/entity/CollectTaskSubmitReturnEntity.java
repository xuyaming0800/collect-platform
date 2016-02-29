package com.autonavi.collect.entity;

import java.io.Serializable;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;

public class CollectTaskSubmitReturnEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8768464418287047244L;
	private Long userId;
	private CollectBasePackage collectBasePackage;
	private CollectTaskBase collectTaskBase;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
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
	

}
