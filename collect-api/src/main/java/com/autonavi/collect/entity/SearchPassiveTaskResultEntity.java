package com.autonavi.collect.entity;

import java.util.List;

public class SearchPassiveTaskResultEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 908029622903549029L;
	
	
	private List<SearchPassiveTaskEntity> collectPassiveTaskEntity;
	
	private Long count;
	


	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<SearchPassiveTaskEntity> getCollectPassiveTaskEntity() {
		return collectPassiveTaskEntity;
	}

	public void setCollectPassiveTaskEntity(
			List<SearchPassiveTaskEntity> collectPassiveTaskEntity) {
		this.collectPassiveTaskEntity = collectPassiveTaskEntity;
	}
	
}
