package com.autonavi.collect.web.bean;

import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskToken;

public class TaskSaveInfoEntity {
	private CollectTaskBase collectTaskBase;
	private CollectTaskToken collectTaskToken;
	public CollectTaskBase getCollectTaskBase() {
		return collectTaskBase;
	}
	public void setCollectTaskBase(CollectTaskBase collectTaskBase) {
		this.collectTaskBase = collectTaskBase;
	}
	public CollectTaskToken getCollectTaskToken() {
		return collectTaskToken;
	}
	public void setCollectTaskToken(CollectTaskToken collectTaskToken) {
		this.collectTaskToken = collectTaskToken;
	}
	
	

}
