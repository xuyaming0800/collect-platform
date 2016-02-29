package com.autonavi.collect.service;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.exception.BusinessException;

public interface CollectJobService {
	/**
	 * 所有分片超时任务包设置超时
	 * @throws BusinessException
	 */
	public void taskPackageTimeoutProceed()throws BusinessException;
	/**
	 * 查询所有分片上的超时任务包
	 * @return
	 * @throws BusinessException
	 */
	public void taskPackageTimeoutRelease()throws BusinessException;
	/**
	 * 被动任务释放(独立事务使用建议)
	 * @param collectBasePackage
	 * @throws BusinessException
	 */
	public void taskPackageAutoRelease(CollectBasePackage collectBasePackage)throws BusinessException;

}
