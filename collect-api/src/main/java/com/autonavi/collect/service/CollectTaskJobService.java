package com.autonavi.collect.service;

import com.autonavi.collect.bean.CollectPassiveTask;

public interface CollectTaskJobService {
	/**
	 * 释放被动任务
	 * 只支持1任务包对1任务的被动任务
	 * 如需支持1对多的关系
	 * 需要重新定义整任务包释放的方法 
	 * 同时需要审核系统按照整任务包回传
	 */
	public void releasePassiveTask(CollectPassiveTask collectPassiveTask)throws Exception;

}
