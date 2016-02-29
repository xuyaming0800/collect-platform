package com.autonavi.collect.service;

import com.autonavi.collect.exception.BusinessException;

public interface CollectTaskClazzService {
	public String getCollectTaskInitiativeClazzTree(String uuid)throws BusinessException;
	public String getCollectTaskPassiveClazzTree(String uuid)throws BusinessException;
}
