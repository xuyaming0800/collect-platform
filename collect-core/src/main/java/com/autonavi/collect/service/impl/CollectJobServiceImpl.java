package com.autonavi.collect.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPackageTimeoutBatch;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.service.CollectJobService;
@Service("collectJobService")
public class CollectJobServiceImpl implements CollectJobService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private PassiveTaskBusinessManager passiveTaskBusinessManager;
	@Resource
	private CollectJobService collectJobService;

	@Override
	public void taskPackageTimeoutProceed() throws BusinessException {
		try {
			logger.info("任务超时处理");
			passiveTaskBusinessManager.getTaskPackageTimeoutProceed().execute(null);
		}catch (BusinessException e){
			throw e;
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}

	}

	@Override
	public void taskPackageTimeoutRelease()
			throws BusinessException {
		try {
			logger.info("查询已经初步处理超时任务");
			Map<CollectPackageTimeoutBatch,List<CollectBasePackage>> map=passiveTaskBusinessManager.getTaskPackageTimeoutQuery().execute(null);
			for(CollectPackageTimeoutBatch batch:map.keySet()){
				List<CollectBasePackage> list=map.get(batch);
				for(CollectBasePackage collectBasePackage:list){
					//独立事务
					if(collectBasePackage.getPassivePackageId()!=null){
					  collectJobService.taskPackageAutoRelease(collectBasePackage);
					}
				}
				passiveTaskBusinessManager.getTaskPackageTimeoutClean().execute(batch);
			}
		}catch (BusinessException e){
			throw e;
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public void taskPackageAutoRelease(CollectBasePackage collectBasePackage)
			throws BusinessException {
		try {
			logger.info("任务超时释放任务处理package_id="+collectBasePackage.getPassivePackageId());
			passiveTaskBusinessManager.getTaskPackageAutoRelease().execute(collectBasePackage);
		}catch (BusinessException e){
			throw e;
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}

	}

}
