package com.autonavi.collect.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectTaskSubmitReturnEntity;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.manager.InitiativeTaskBusinessManager;
import com.autonavi.collect.manager.SyncTaskBusinessMananger;
import com.autonavi.collect.service.InitiativeTaskService;
import com.autonavi.collect.service.TaskCollectUtilService;
import com.autonavi.collect.util.FileUtil;
@Service
public class InitiativeTaskServiceImpl implements InitiativeTaskService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private InitiativeTaskBusinessManager initiativeTaskBusinessManager;
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;
	@Autowired
	private TaskCollectUtilService taskCollectUtilService;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;
	
	private PropertiesConfig propertiesConfig;
	public InitiativeTaskServiceImpl()throws Exception{
		this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	@Override
	public CollectTaskBase taskSave(CollectTaskSaveEntity collectTaskSaveEntity) throws Exception {
		
		CollectTaskBase collectTaskBase=collectTaskSaveEntity.getCollectTaskBase();
		CollectTaskToken collectTaskToken=collectTaskSaveEntity.getCollectTaskToken();
		List<CollectTaskImg> imgList=collectTaskSaveEntity.getCollectTaskImgList();
		boolean lockOne=false;
		boolean lockTwo=false;
		CollectBasePackage collectBasePackage=null;
		try {
			int expire=Integer.valueOf(propertiesConfig.getProperty(CommonConstant.PROP_TASK_INSERT_MIN_SPLIT).toString());
			if(collectTaskBase.getId()!=null){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
				lockOne=true;			
			}else{
				//未保存的新任务采取n秒内智能保存一条 完成后不释放 等待自动超时
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_INSERT_KEY, collectTaskBase.getCollectUserId().toString(), BusinessExceptionEnum.DUPLICATE_ACTIVE_TASK_SAVE_ERROR, expire);
			}
			collectTaskSaveEntity.setCollectTaskBase(collectTaskBase);
			collectTaskSaveEntity = initiativeTaskBusinessManager.getTaskSaveCheck().execute(collectTaskSaveEntity);
			collectBasePackage=collectTaskSaveEntity.getCollectBasePackage();
			CollectTaskClazz taskClazz=taskClazzCacheComponent.getCollectTaskClazz(collectBasePackage.getTaskClazzId());
			if(taskClazz.getClazzPay()!=null&&collectBasePackage.getTaskPackagePay()==0.0D)
			collectBasePackage.setTaskPackagePay(taskClazz.getClazzPay());
			if(collectBasePackage.getId()!=null){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
				lockTwo=true;
			}else{
				//默认会给默认价格等信息 释放时间等信息 如果需要手设置请在这里设置
			}
			collectTaskSaveEntity.setCollectTaskImgList(imgList);
			collectTaskSaveEntity.setCollectTaskToken(collectTaskToken);
			CollectTaskBase _base=initiativeTaskBusinessManager.getTaskSave().execute(collectTaskSaveEntity);
			return _base;
			// 如果出现异常，直接抛出
		}finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString());
			}
			
		}
	}
	@Override
	public CollectTaskSubmitReturnEntity taskSubmit(CollectTaskSubmitEntity submitEntity)
			throws Exception {
		boolean lockOne=false;
		boolean lockTwo=false;
		CollectTaskBase collectTaskBase=submitEntity.getCollectTaskBase();
		CollectBasePackage collectBasePackage=null;
		try {
			int expire=Integer.valueOf(propertiesConfig.getProperty(CommonConstant.PROP_TASK_INSERT_MIN_SPLIT).toString());
			//Map<Long,CollectTaskClazz> map=new HashMap<Long,CollectTaskClazz>();
			if(collectTaskBase.getId()!=null){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 10);
				lockOne=true;			
			}else{
				//未保存的新任务采取n秒内智能保存一条 完成后不释放 等待自动超时
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_INSERT_KEY, collectTaskBase.getCollectUserId().toString(), BusinessExceptionEnum.DUPLICATE_ACTIVE_TASK_UPLOAD_ERROR, expire);
			}
			submitEntity = initiativeTaskBusinessManager.getTaskSubmitCheck().execute(submitEntity);
			collectBasePackage=submitEntity.getCollectBasePackage();
			CollectTaskClazz taskClazz=taskClazzCacheComponent.getCollectTaskClazz(collectBasePackage.getTaskClazzId());
			//map.put(taskClazz.getId(), taskClazz);
			if(taskClazz.getClazzPay()!=null&&collectBasePackage.getTaskPackagePay()==0.0D)
			collectBasePackage.setTaskPackagePay(taskClazz.getClazzPay());
			if(collectBasePackage.getId()!=null){
				redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
				lockTwo=true;
			}else{
				//默认会给默认价格等信息 释放时间等信息 如果需要手设置请在这里设置
			}
			submitEntity.setUserName(taskCollectUtilService.getUserNameCache(collectTaskBase.getCollectUserId()));
			submitEntity=initiativeTaskBusinessManager.getTaskSubmit().execute(submitEntity);
			CollectTaskSubmitReturnEntity result=new CollectTaskSubmitReturnEntity();
			result.setCollectBasePackage(submitEntity.getCollectBasePackage());
			result.setUserId(collectTaskBase.getCollectUserId());
			result.setCollectTaskBase(submitEntity.getCollectTaskBase());
			return result;
//			//发送到打水印步骤
//			List<String> waterList=new ArrayList<String>();
//			for(CollectTaskImg img:collectTaskImgList){
//				String url=FileUtil.getCollectSourceImgUrl(img.getImgName(),imgUrl);
//				waterList.add(url);
//			}
//			logger.info("发送至待审核消息队列开始 pid="+submitEntity.getCollectBasePackage().getId());
//			List<ResultEntity> entitys=syncTaskBusinessMananger.getInitiativeTaskSendToAudit().execute(submitEntity);
//			for(ResultEntity entity:entitys){
//				logger.info("发送至待审核消息队列--开始 id="+((CollectAudit)entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
//				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(entity);
//				logger.info("发送至待审核消息队列--结束 id="+((CollectAudit)entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
//			}
			
			
		} finally {
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, collectTaskBase.getId().toString());
			}
			if(lockTwo){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString());
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
			collectBasePackage=initiativeTaskBusinessManager.getTaskDeleteCheck().execute(collectBasePackage);
			return initiativeTaskBusinessManager.getTaskDelete().execute(collectBasePackage);
		}finally{
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, collectBasePackage.getId().toString());
			}
		}
	}
	@Override
	public void taskPackageSyncAudit(CollectTaskSubmitReturnEntity entity)throws Exception {
		boolean lockOne=false;
		try{
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, entity.getCollectBasePackage().getId().toString(), BusinessExceptionEnum.REPEAT_PASSIVE_TASK_RECEIVE_ERROR, 10);
			lockOne=true;
			CollectTaskSubmitEntity submitEntity=new CollectTaskSubmitEntity();
			submitEntity.setCollectBasePackage(entity.getCollectBasePackage());
			submitEntity.setUserName(taskCollectUtilService.getUserNameCache(entity.getUserId()));
			List<ResultEntity> entitys=syncTaskBusinessMananger.getInitiativeTaskSendToAudit().execute(submitEntity);
			for(ResultEntity _entity:entitys){
				logger.info("发送至待审核消息队列--开始 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
				logger.info(JsonBinder.buildNonNullBinder(true).toJson(_entity));
				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(_entity);
				logger.info("发送至待审核消息队列--结束 id="+((CollectAudit)_entity.getInfo()).getId()+" pid="+submitEntity.getCollectBasePackage().getId());
			}
		}finally{
			if(lockOne){
				redisUtilComponent.releaseIdByRedis(CommonConstant.REDIS_LOCK_PACKAGE_KEY, entity.getCollectBasePackage().getId().toString());
			}
		}
		
	}
	@Override
	public void taskSyncWaterImage(List<CollectTaskImg> collectTaskImgList,
			String imgurl,CollectTaskSubmitReturnEntity entity)throws Exception {
		boolean lockOne=false;
		try{
			redisUtilComponent.lockIdByRedis(CommonConstant.REDIS_LOCK_TASK_KEY, entity.getCollectTaskBase().getId().toString(), BusinessExceptionEnum.DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR, 10);
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

}
