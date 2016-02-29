package com.autonavi.collect.entity;

import java.io.Serializable;
import java.util.List;

import com.autonavi.collect.bean.CollectTaskBase;
/**
 * 任务领取返回对象
 * @author xuyaming
 *
 */
public class CollectTaskReceiveReturnEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5781581011296880447L;
	private String packageName;
	private List<CollectTaskBase> collectTaskBaseList;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public List<CollectTaskBase> getCollectTaskBaseList() {
		return collectTaskBaseList;
	}
	public void setCollectTaskBaseList(List<CollectTaskBase> collectTaskBaseList) {
		this.collectTaskBaseList = collectTaskBaseList;
	}
	
	

}
