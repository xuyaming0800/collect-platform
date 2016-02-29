package com.autonavi.collect.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.CollectTaskReAuditEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.manager.ReAuditTaskBusinessManager;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.service.CollectTaskReAuditService;
@Service
public class CollectTaskReAuditServiceImpl implements CollectTaskReAuditService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private ReAuditTaskBusinessManager reAuditTaskBusinessManager;
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;

	@Override
	public void reAuditTaskPackage(CollectTaskReAuditEntity entity) throws Exception {
		boolean lockOne=false;
		try{
			//锁定任务
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, 
					entity.getBasePackageId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
			lockOne=true;
			entity.setIsLocked(true);
			entity=reAuditTaskBusinessManager.getReAuditTaskCheck().execute(entity);
			reAuditTaskBusinessManager.getReAuditTask().execute(entity);
			this.reAuditTaskPackageSync(entity);
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}finally{
			if(lockOne){
				//释放任务锁
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, 
						entity.getBasePackageId().toString());
			}
		}

	}

	@Override
	public void reAuditTaskPackageSync(CollectTaskReAuditEntity entity) throws Exception {
		boolean lockOne=false;
		try{
			if(!entity.getIsLocked()){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, 
						entity.getBasePackageId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
				lockOne=true;
			}
			
			List<ResultEntity> entitys=syncTaskBusinessMananger.getReAuditTaskSendToAudit().execute(entity);
			for(ResultEntity _entity:entitys){
				logger.info("发送至待审核消息队列--申诉--开始 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+entity.getBasePackageId());
				logger.info(JsonBinder.buildNonNullBinder(false).toJson(_entity));
				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(_entity);
				logger.info("发送至待审核消息队列--申诉--结束 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+entity.getBasePackageId());
			}
			
			
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}finally{
			if(lockOne){
				//释放任务锁
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, 
						entity.getBasePackageId().toString());
			}
			
		}
		
	}

}
