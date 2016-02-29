package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.CollectTaskReceiveReturnEntity;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectTaskSubmitReturnEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.manager.OtherTaskBusinessManager;
import com.autonavi.collect.manager.PassiveTaskBusinessManager;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.service.PassiveTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.util.FileUtil;

@Service
public class PassiveTaskServiceImpl implements PassiveTaskService {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private PassiveTaskBusinessManager passiveTaskBusinessManager;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private OtherTaskBusinessManager otherTaskBusinessManager;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;


	// 被动任务保存
	@Override
	public CollectTaskBase taskSave(CollectTaskSaveEntity collectTaskSaveEntity) throws Exception {
		CollectBasePackage collectBasePackage=null;
		boolean lockOne=false;
		boolean lockTwo=false;
		CollectTaskBase collectTaskBase=collectTaskSaveEntity.getCollectTaskBase();
		try {
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_SAVE_ERROR, 10);
			lockOne=true;
			collectTaskSaveEntity = passiveTaskBusinessManager.getTaskSaveCheck().execute(collectTaskSaveEntity);
			collectBasePackage=new CollectBasePackage();
			collectBasePackage.setId(collectTaskSaveEntity.getCollectPassiveTask().getTaskPackageId());
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_SAVE_ERROR, 30);
			lockTwo=true;
			return passiveTaskBusinessManager.getTaskSave().execute(collectTaskSaveEntity);
			// 如果出现异常，直接抛出
		}finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString());
			}
			
		}
	}

	

	// 被动任务领取
	// collectTaskBase参数会传递上来allotUserId, passiveId, id
	@Override
	public CollectTaskBase taskReceive(CollectTaskBase collectTaskBase) throws Exception {
		boolean lockOne=false;
		try {
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getPassiveId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
			lockOne=true;
			CollectPassiveTask collectPassiveTask_DB = passiveTaskBusinessManager.getTaskReceiveCheck().execute(collectTaskBase);
			collectTaskBase.setTaskStatus(collectPassiveTask_DB.getTaskStatus());// 数据库中当前的任务状态需要带给内存中的collectTaskBase
			return passiveTaskBusinessManager.getTaskReceive().execute(collectTaskBase);
		} finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getPassiveId().toString());
			}
		}
	}

	// 被动任务提交
	@Override
	public CollectTaskSubmitReturnEntity taskSubmit(CollectTaskSubmitEntity submitEntity) throws Exception {
		CollectBasePackage collectBasePackage=null;
		boolean lockOne=false;
		boolean lockTwo=false;
		CollectTaskBase collectTaskBase=submitEntity.getCollectTaskBase();
		
		try {
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 30);
			lockOne=true;
			submitEntity = passiveTaskBusinessManager.getTaskSubmitCheck().execute(submitEntity);
			collectBasePackage=new CollectBasePackage();
			collectBasePackage.setId(submitEntity.getCollectPassiveTask().getTaskPackageId());
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 10);
			lockTwo=true;
			submitEntity.setUserName(taskCollectUtilService.getUserNameCache(collectTaskBase.getCollectUserId()));
			passiveTaskBusinessManager.getTaskSubmit().execute(submitEntity);
			//暂时自动提交任务包 1对一前提下
			collectBasePackage.setId(submitEntity.getCollectTaskBase().getTaskPackageId());
			collectBasePackage.setCollectUserId(submitEntity.getCollectTaskBase().getCollectUserId());
			collectBasePackage.setAllotUserId(submitEntity.getCollectTaskBase().getCollectUserId());
			collectBasePackage.setOwnerId(submitEntity.getCollectTaskBase().getOwnerId());
			CollectTaskSubmitReturnEntity _entity=taskPackageSubmit(collectBasePackage,false);
			_entity.setCollectTaskBase(collectTaskBase);
			_entity.setUserId(collectTaskBase.getCollectUserId());
			return _entity;
		} finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString());
			}
		}
	}
    //被动任务包领取
	@Override
	public CollectTaskReceiveReturnEntity taskPackageReceive(
			CollectBasePackage collectBasePackage) throws Exception {
		boolean lockOne=false;
		try {
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getPassivePackageId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
			lockOne=true;
			CollectPassivePackage collectPassivePackage_DB = passiveTaskBusinessManager.getTaskPackageReceiveCheck().execute(collectBasePackage);
			collectBasePackage.setTaskPackageStatus(collectPassivePackage_DB.getTaskPackageStatus());// 数据库中当前的任务状态需要带给内存中的collectTaskBase
			List<CollectTaskBase> list=passiveTaskBusinessManager.getTaskPackageReceive().execute(collectBasePackage);
			CollectTaskReceiveReturnEntity entity=new CollectTaskReceiveReturnEntity();
			entity.setPackageName(collectPassivePackage_DB.getTaskPackageName());
			entity.setCollectTaskBaseList(list);
			return entity;
		} finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getPassivePackageId().toString());
			}
		}
	}

	@Override
	public CollectTaskSubmitReturnEntity taskPackageSubmit(CollectBasePackage collectBasePackage,
			Boolean isCheckRedis)throws Exception {
		boolean lockOne=false;
		try {
			if(isCheckRedis){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 15);
				lockOne=true;
			}
			collectBasePackage=passiveTaskBusinessManager.getTaskPackageSubmitCheck().execute(collectBasePackage);
			CollectTaskSubmitEntity entity=new CollectTaskSubmitEntity();
			entity.setCollectBasePackage(collectBasePackage);
			entity.setUserName(taskCollectUtilService.getUserNameCache(collectBasePackage.getCollectUserId()));
			entity=passiveTaskBusinessManager.getTaskPackageSubmit().execute(entity);
			CollectTaskSubmitReturnEntity _entity=new CollectTaskSubmitReturnEntity();
			_entity.setCollectBasePackage(entity.getCollectBasePackage());
			return _entity;
			
		} finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectBasePackage.getId().toString());
			}
		}
		
	}



	@Override
	public void taskPackageSyncAudit(CollectTaskSubmitReturnEntity entity)
			throws Exception {
		boolean lockOne=false;
		try{
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, entity.getCollectBasePackage().getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 15);
			lockOne=true;
			CollectTaskSubmitEntity submitEntity=new CollectTaskSubmitEntity();
			submitEntity.setCollectBasePackage(entity.getCollectBasePackage());
			submitEntity.setUserName(taskCollectUtilService.getUserNameCache(entity.getUserId()));
			List<ResultEntity> entitys=syncTaskBusinessMananger.getPassiveTaskSendToAudit().execute(submitEntity);
			for(ResultEntity _entity:entitys){
				logger.info("发送至待审核消息队列--开始 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
				logger.info(JsonBinder.buildNonNullBinder(true).toJson(_entity));
				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(_entity);
				logger.info("发送至待审核消息队列--结束 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
			}
		}finally{
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, entity.getCollectBasePackage().getId().toString());
			}
		}
		
	}



	@Override
	public void taskSyncWaterImage(List<CollectTaskImg> collectTaskImgList,
			String imgurl, CollectTaskSubmitReturnEntity entity)
			throws Exception {
		boolean lockOne=false;
		try{
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, entity.getCollectTaskBase().getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 30);
			lockOne=true;
			//发送到打水印步骤
			List<String> waterList=new ArrayList<String>();
			for(CollectTaskImg img:collectTaskImgList){
				String url=FileUtil.getCollectSourceImgUrl(img.getImgName(),imgurl);
				waterList.add(url);
			}
			taskCollectUtilService.notifyImageWather(entity.getUserId(), entity.getCollectTaskBase().getId(), waterList);
		}finally{
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, entity.getCollectTaskBase().getId().toString());
			}
		}
		
	}



	@Override
	public Integer taskDelete(CollectBasePackage collectBasePackage)
			throws Exception {
		boolean lockOne=false;
		try{
			if(collectBasePackage.getId()!=null){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
				lockOne=true;
			}
			collectBasePackage=passiveTaskBusinessManager.getTaskDeleteCheck().execute(collectBasePackage);
			return passiveTaskBusinessManager.getTaskDelete().execute(collectBasePackage);
		}finally{
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString());
			}
		}
	}

}
