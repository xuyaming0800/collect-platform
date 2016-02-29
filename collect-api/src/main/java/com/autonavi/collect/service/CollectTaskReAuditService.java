package com.autonavi.collect.service;

import com.autonavi.collect.entity.CollectTaskReAuditEntity;

public interface CollectTaskReAuditService {
	/**
	 * 申诉任务
	 * @param id
	 * @throws Exception
	 */
	public void reAuditTaskPackage(CollectTaskReAuditEntity entity)throws Exception;
	/**
	 * 申诉任务到消息队列
	 * @param id
	 * @throws Exception
	 */
	public void reAuditTaskPackageSync(CollectTaskReAuditEntity entity)throws Exception;

}
