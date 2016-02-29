package com.autonavi.collect.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditSpecimenImage;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.mq.RabbitMQMessageHandler;
import com.autonavi.audit.mq.RabbitMQUtils;
import com.autonavi.collect.bean.BossUserTable;
import com.autonavi.collect.bean.CollectSendMessageError;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.component.CollectUserCacheComponent;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TaskClazzCacheComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.SEND_MESSAGE_ERROR_TYPE;
import com.autonavi.collect.constant.CommonConstant.USER_CACHE_TYPE;
import com.autonavi.collect.dao.BossUserDao;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectPassivePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectSendMessageErrorDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.entity.CollectTaskReAuditEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.CollectToAuditEntity;
import com.autonavi.collect.entity.OriginCoordinateEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.gd.app.entity.WaterImgMessageEntity;
@Component
public class SyncTaskBusinessMananger {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;	
	@Autowired
	private CollectPassivePackageDao collectPassivePackageDao;
	@Autowired
	private CollectTaskAllotUserDao collectTaskAllotUserDao;
	@Autowired
	private CollectSendMessageErrorDao collectSendMessageErrorDao;
	@Autowired
	private BossUserDao bossUserDao;
	@Autowired
	private SyncTaskUtilBusinessManager syncTaskUtilBusinessManager;
	@Autowired
	private TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private CollectUserCacheComponent collectUserCacheComponent;
	@Autowired
	private TaskClazzCacheComponent taskClazzCacheComponent;
	@Autowired
	private ReAuditTaskSendToAudit reAuditTaskSendToAudit;
	
	@Autowired
	private PassiveTaskSendToAudit passiveTaskSendToAudit;
	
	@Autowired
	private TaskReceiveAudit taskReceiveAudit;
	@Autowired
	private SendCollectInfoToAuditQueue sendCollectInfoToAuditQueue;
	@Autowired
	private InitiativeTaskSendToAudit initiativeTaskSendToAudit;
	@Autowired
	private SendCollectInfoJsonToAuditQueue sendCollectInfoJsonToAuditQueue;
	@Autowired
	private SendImagePathToWaterQueue sendImagePathToWaterQueue;

	

	public SendImagePathToWaterQueue getSendImagePathToWaterQueue() {
		return sendImagePathToWaterQueue;
	}


	public InitiativeTaskSendToAudit getInitiativeTaskSendToAudit() {
		return initiativeTaskSendToAudit;
	}


	public SendCollectInfoToAuditQueue getSendCollectInfoToAuditQueue() {
		return sendCollectInfoToAuditQueue;
	}


	public PassiveTaskSendToAudit getPassiveTaskSendToAudit() {
		return passiveTaskSendToAudit;
	}
	
	
	public TaskReceiveAudit getTaskReceiveAudit() {
		return taskReceiveAudit;
	}


	public SendCollectInfoJsonToAuditQueue getSendCollectInfoJsonToAuditQueue() {
		return sendCollectInfoJsonToAuditQueue;
	}
	
	


	public ReAuditTaskSendToAudit getReAuditTaskSendToAudit() {
		return reAuditTaskSendToAudit;
	}




	private PropertiesConfig propertiesConfig;

	public SyncTaskBusinessMananger() throws Exception {
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	
	@Component
	public class TaskReceiveAudit implements CollectCore<RabbitMQMessageHandler, Object>{
		@Autowired
		public TaskReceiveAudit(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@Override
		public RabbitMQMessageHandler execute(Object obj) throws BusinessException {
			try {
				RabbitMQMessageHandler handler=new RabbitMQMessageHandler(){

					@Override
					public void setMessage(Object message) {
						 try {
							ResultEntity resultEntity = (ResultEntity) message;
							 if(resultEntity.isSuccess()){
								 if(resultEntity.getInfo()!=null&&resultEntity.getInfo() instanceof CollectAudit){
									 //解析消息
									 JsonBinder binder=JsonBinder.buildNormalBinder();
									 logger.info("收到审核回传消息 "+binder.toJson(resultEntity));
									 CollectAudit collectAudit=(CollectAudit)resultEntity.getInfo();
									 Long bid=new Long(collectAudit.getId());
									 Integer verifyStatus=collectAudit.getStatus();
									 String userName=collectAudit.getUser_name();
									 String verifyDataName=collectAudit.getAudit_task_name();
									 String ownerId=collectAudit.getSystem_type().toString();
									 Long userId=null;
									 String _userId=collectUserCacheComponent.getUserIdFromCache(userName,USER_CACHE_TYPE.REDIS.getCode());
									 if(_userId!=null){
										 userId=Long.valueOf(_userId);
									 }else{
										 BossUserTable user=(BossUserTable)bossUserDao.getBossUserByName(CommonConstant.getSingleDataSourceKey(), userName);
										 if(user==null){
											 logger.error("UserName["+userName+"] 没有找到");
											 return;
										 }
										 userId=user.getId();
									 }
									
									 logger.info("收到审核回传消息 id="+bid);
									 //封装任务对象
									 CollectTaskBase base=new CollectTaskBase();
									 base.setId(bid);
									 base.setCollectUserId(userId);
									 base.setVerifyStatus(verifyStatus);
									 base.setVerifyDataName(verifyDataName);
									 base.setOwnerId(Long.valueOf(ownerId));
									 syncTaskUtilBusinessManager.taskRelease(base);
									 
									
								 }
								 
							 }else{
								 //失败情况 需要商量
								 if(resultEntity.getInfo()!=null&&resultEntity.getInfo() instanceof CollectAudit){
									 CollectAudit collectAudit=(CollectAudit)resultEntity.getInfo();
									 logger.error("CollectTaskBase ID=["+collectAudit.getId()+"] Failed,The ErrorInfo is ["+resultEntity.getDesc()+"]");
									 //记录入库
									 CollectSendMessageError error=new CollectSendMessageError();
									 error.setUserId(Long.valueOf(collectUserCacheComponent.getUserIdFromCache(collectAudit.getUser_name(),USER_CACHE_TYPE.REDIS.getCode())));
									 error.setTaskId(Long.valueOf(collectAudit.getId()));
									 error.setErrorType(SEND_MESSAGE_ERROR_TYPE.AUDIT_IN.getCode());
									 String desc=resultEntity.getDesc();
									 if(desc!=null&&desc.trim().length()>600){
										 desc=desc.trim().substring(0,599);
									 }
									 error.setErrorDesc(desc);
									 collectSendMessageErrorDao.insertError(CommonConstant.getSingleDataSourceKey(), error);
								 }
								
							 }
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							throw new BusinessRunException(BusinessExceptionEnum.TASK_IN_QUEUE_ERROR);
						}
					}
					
				};
			    return handler;	
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(BusinessExceptionEnum.TASK_IN_QUEUE_ERROR);
			}
		}
		
	}
	

	@Component
	public class PassiveTaskSendToAudit implements CollectCore<List<ResultEntity>, CollectTaskSubmitEntity> {

		@Autowired
		public PassiveTaskSendToAudit(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<ResultEntity> execute(CollectTaskSubmitEntity entity) throws BusinessException {
			if(entity.getCollectBasePackage().getId()==null){
				logger.error("CollectBasePackage.id is null");
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getCollectBasePackage().getCollectUserId()==null){
            	logger.error("CollectBasePackage.CollectUserId is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getUserName()==null){
            	logger.error("userName is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
			try {
				//获取信息
				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao.
						selectCollectInfoForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId(),entity.getCollectBasePackage().getOwnerId());
//				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao
//						.selectTaskInfoForAudit(entity.getCollectTaskBase().getId(), entity.getCollectTaskBase().getCollectUserId());
				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
						selectOriginCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId(),entity.getCollectBasePackage().getOwnerId());
//				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
//						selectCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId());
				Map<Long,CollectAudit> existCollectMap=new HashMap<Long,CollectAudit>();
				List<Double> coordinates=new ArrayList<Double>();
				List<String> imgs=new ArrayList<String>();
				//原始坐标生成
				for(OriginCoordinateEntity coord:coords){
					if(coord.getX()!=null&&!coord.getX().equals("")&&coord.getY()!=null&&!coord.getY().equals("")){
						coordinates.add(coord.getX());
						coordinates.add(coord.getY());
					}
					if(coord.getStatus().equals("0")){//计算图片数量
						imgs.addAll(getOrginImgUrls(coord.getImageName()));
					}
					
				}
				//分装坐标和图片信息
				List<CollectAuditSpecimenImage> collectAuditSpecimenImages = new ArrayList<CollectAuditSpecimenImage>();
				for(String img:imgs){
					CollectAuditSpecimenImage collectAuditSpecimenImage = new CollectAuditSpecimenImage();
					collectAuditSpecimenImage.setThumbnai_url(img);
					collectAuditSpecimenImage.setImage_url(img);
					collectAuditSpecimenImages.add(collectAuditSpecimenImage);

				}
				for(CollectToAuditEntity collect:collects){
					if(!existCollectMap.containsKey(collect.getBid())){
						//初始化信息
						CollectAudit collectAudit = new CollectAudit();
						collectAudit.setTask_amount(collect.getPay());
						collectAudit.setLocation_name(collect.getpName());
						collectAudit.setLocation_address(collect.getpDesc());
						collectAudit.setTask_freezing_time(collect.getfTime());
						collectAudit.setVerify_maintain_time(collect.getmTime());
						collectAudit.setId(collect.getBid().toString());
						collectAudit.setOriginal_task_name(collect.getbName());
						collectAudit.setCollect_task_name(collect.getbCollectName());
						collectAudit.setStatus(collect.getStatus());
						collectAudit.setSubmit_time(new Date(collect.getSubmitTime()*1000));
						collectAudit.setUser_name(entity.getUserName());
//						collectAudit.setSystem_type(new Integer(propertiesConfig.getProperty(CommonConstant.COLLECT_SYSTEM_UNIQUE_ID).toString()));
						collectAudit.setSystem_type(collect.getOwnerId().toString());
						collectAudit.setOriginalCoordinates(coordinates.toArray(new Double[0]));
						collectAudit.setSpecimenImages(collectAuditSpecimenImages);
						collectAudit.setImages(new ArrayList<CollectAuditImage>());
						existCollectMap.put(collect.getBid(), collectAudit);
					}
					CollectAudit collectAudit=existCollectMap.get(collect.getBid());
					//循环填入采集的图片
					if(collect.getStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
						CollectAuditImage collectAuditImage = new CollectAuditImage();
						collectAuditImage.setAudit_id(collect.getCid().toString());
						collectAuditImage
								.setThumbnai_url(getCollectImgUrl(collect.getImageName()));
						collectAuditImage
								.setImage_url(getCollectImgUrl(collect.getImageName()));
						if(collect.getGpsTime()!=null)
						collectAuditImage.setGps_time(new Date(collect.getGpsTime()*1000));
						collectAuditImage.setLon(collect.getX());
						collectAuditImage.setLat(collect.getY());
						if(collect.getPhotoTime()!=null)
						collectAuditImage.setPhotograph_time(new Date(collect.getPhotoTime()*1000));
						collectAuditImage.setPoint_accury(collect.getAccuracy());
						collectAuditImage.setPoint_level(collect.getLevels());
						collectAuditImage.setPosition(collect.getPosition());
						collectAuditImage.setIndex(collect.getImageIndex()==null?0:collect.getImageIndex());
						collectAudit.getImages().add(collectAuditImage);
					}else if(collect.getStatus().equals(CommonConstant.TASK_STATUS.NOT_FOUND.getCode())){
						//缺失情况 目前不支持多个暂时按照图片处理给审核
						CollectAuditImage collectAuditImage = new CollectAuditImage();
						collectAuditImage.setAudit_id(collect.getCid().toString());
						collectAuditImage.setVideo_url(getCollectImgUrl(collect.getImageName()));
						collectAuditImage.setNo_exist_reason("目标未找到");
						collectAuditImage.setVideo_time(collect.getPhotoTime()==null?new Date():new Date(collect.getPhotoTime()*1000));
						collectAuditImage.setGps_time(collect.getGpsTime()==null?new Date():new Date(collect.getGpsTime()*1000));
						collectAuditImage.setLon(collect.getX());
						collectAuditImage.setLat(collect.getY());
						collectAuditImage.setPhotograph_time(collect.getPhotoTime()==null?new Date():new Date(collect.getPhotoTime()*1000));
						collectAuditImage.setPoint_accury(collect.getAccuracy());
						collectAuditImage.setPoint_level(collect.getLevels());
						collectAuditImage.setPosition(collect.getPosition());
						collectAuditImage.setIndex(collect.getImageIndex());
						collectAudit.getImages().add(collectAuditImage);
					}else{
						logger.error("任务["+collect.getBid()+"]状态错误");
						throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
					}
					
				}
				List<ResultEntity> l=new ArrayList<ResultEntity>();
				for(Long id:existCollectMap.keySet()){
					ResultEntity _entity=new ResultEntity();
					_entity.setInfo(existCollectMap.get(id));
					_entity.setSuccess(true);
					l.add(_entity);
				}
				return l;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
		}
	}
	
	@Component
	public class InitiativeTaskSendToAudit implements CollectCore<List<ResultEntity>, CollectTaskSubmitEntity> {

		@Autowired
		public InitiativeTaskSendToAudit(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<ResultEntity> execute(CollectTaskSubmitEntity entity) throws BusinessException {
			if(entity.getCollectBasePackage().getId()==null){
				logger.error("CollectBasePackage.id is null");
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getCollectBasePackage().getCollectUserId()==null){
            	logger.error("CollectBasePackage.CollectUserId is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getUserName()==null){
            	logger.error("userName is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
			try {
				//获取信息
				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao.
						selectCollectInfoForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId(),entity.getCollectBasePackage().getOwnerId());
//				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao
//						.selectTaskInfoForAudit(entity.getCollectTaskBase().getId(), entity.getCollectTaskBase().getCollectUserId());
//				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
//						selectOriginCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId());
//				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
//						selectCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId());
				Map<Long,CollectAudit> existCollectMap=new HashMap<Long,CollectAudit>();
				List<Double> coordinates=new ArrayList<Double>();
//				List<String> imgs=new ArrayList<String>();
				//原始坐标生成
//				for(OriginCoordinateEntity coord:coords){
//					if(coord.getX()!=null&&!coord.getX().equals("")&&coord.getY()!=null&&!coord.getY().equals("")){
//						coordinates.add(coord.getX());
//						coordinates.add(coord.getY());
//					}
//					if(coord.getStatus().equals("0")){//计算图片数量
//						imgs.addAll(getOrginImgUrls(coord.getImageName()));
//					}
//					
//				}
				//分装坐标和图片信息
				List<CollectAuditSpecimenImage> collectAuditSpecimenImages = new ArrayList<CollectAuditSpecimenImage>();
//				for(String img:imgs){
//					CollectAuditSpecimenImage collectAuditSpecimenImage = new CollectAuditSpecimenImage();
//					collectAuditSpecimenImage.setThumbnai_url(img);
//					collectAuditSpecimenImage.setImage_url(img);
//					collectAuditSpecimenImages.add(collectAuditSpecimenImage);
//
//				}
				for(CollectToAuditEntity collect:collects){
					if(!existCollectMap.containsKey(collect.getBid())){
						//初始化信息
						CollectAudit collectAudit = new CollectAudit();
						collectAudit.setTask_amount(collect.getPay());
						collectAudit.setLocation_name(collect.getpName());
						collectAudit.setLocation_address(collect.getpDesc());
						collectAudit.setTask_freezing_time(collect.getfTime());
						collectAudit.setVerify_maintain_time(collect.getmTime());
						collectAudit.setId(collect.getBid().toString());
						collectAudit.setOriginal_task_name(collect.getbName());
						collectAudit.setCollect_task_name(collect.getbCollectName());      
						collectAudit.setStatus(collect.getStatus());
						collectAudit.setSubmit_time(new Date(collect.getSubmitTime()*1000));
						collectAudit.setUser_name(entity.getUserName());
						collectAudit.setSystem_type(collect.getOwnerId().toString());
						collectAudit.setOriginalCoordinates(coordinates.toArray(new Double[0]));
						collectAudit.setSpecimenImages(collectAuditSpecimenImages);
						collectAudit.setImages(new ArrayList<CollectAuditImage>());
						CollectTaskClazz clazz=taskClazzCacheComponent.getCollectTaskClazz(collect.getTaskClazzId());
						logger.info("同步采集类型["+clazz.getClazzName()+"]");
						if(clazz!=null){
							collectAudit.setTask_class_name(clazz.getClazzName());
							collectAudit.setTask_class_img_count(clazz.getClazzImgCount());
							collectAudit.setTask_class_far_img_count(clazz.getClazzFarImgCount());
							collectAudit.setTask_class_near_img_count(clazz.getClazzNearImgCount());
						}
						existCollectMap.put(collect.getBid(), collectAudit);
					}
				    CollectAudit collectAudit=existCollectMap.get(collect.getBid());
					//循环填入采集的图片
					if(collect.getStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
						CollectAuditImage collectAuditImage = new CollectAuditImage();
						collectAuditImage.setAudit_id(collect.getCid().toString());
						collectAuditImage
								.setThumbnai_url(getCollectImgUrl(collect.getImageName()));
						collectAuditImage
								.setImage_url(getCollectImgUrl(collect.getImageName()));
						if(collect.getGpsTime()!=null)
						collectAuditImage.setGps_time(new Date(collect.getGpsTime()*1000));
						collectAuditImage.setLon(collect.getX());
						collectAuditImage.setLat(collect.getY());
						if(collect.getPhotoTime()!=null)
						collectAuditImage.setPhotograph_time(new Date(collect.getPhotoTime()*1000));
						collectAuditImage.setPoint_accury(collect.getAccuracy());
						collectAuditImage.setPoint_level(collect.getLevels());
						collectAuditImage.setPosition(collect.getPosition());
						collectAuditImage.setIndex(collect.getImageIndex());
						collectAudit.getImages().add(collectAuditImage);
					}else{
						logger.error("任务["+collect.getBid()+"]状态错误");
						throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
					}
					
				}
				List<ResultEntity> l=new ArrayList<ResultEntity>();
				for(Long id:existCollectMap.keySet()){
					ResultEntity _entity=new ResultEntity();
					_entity.setInfo(existCollectMap.get(id));
					_entity.setSuccess(true);
					l.add(_entity);
				}
				return l;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
		}
	}
	
	@Component
	public class ReAuditTaskSendToAudit implements CollectCore<List<ResultEntity>, CollectTaskReAuditEntity> {

		@Autowired
		public ReAuditTaskSendToAudit(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<ResultEntity> execute(CollectTaskReAuditEntity entity) throws BusinessException {
			if(entity.getBasePackageId()==null){
				logger.error("CollectBasePackage.id is null");
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getBasePackageId()==null){
            	logger.error("CollectBasePackage.CollectUserId is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
            if(entity.getUserName()==null){
            	logger.error("userName is null");
            	throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
			try {
				//获取信息
				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao.
						selectCollectInfoForReAudit(entity.getBasePackageId(), entity.getUserId(),entity.getOwnerId());
//				List<CollectToAuditEntity> collects=(List<CollectToAuditEntity>)collectBasePackageDao
//						.selectTaskInfoForAudit(entity.getCollectTaskBase().getId(), entity.getCollectTaskBase().getCollectUserId());
//				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
//						selectOriginCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId());
//				List<OriginCoordinateEntity> coords=(List<OriginCoordinateEntity>)collectBasePackageDao.
//						selectCoordinateForAudit(entity.getCollectBasePackage().getId(), entity.getCollectBasePackage().getCollectUserId());
				Map<Long,CollectAudit> existCollectMap=new HashMap<Long,CollectAudit>();
				List<Double> coordinates=new ArrayList<Double>();
//				List<String> imgs=new ArrayList<String>();
				//原始坐标生成
//				for(OriginCoordinateEntity coord:coords){
//					if(coord.getX()!=null&&!coord.getX().equals("")&&coord.getY()!=null&&!coord.getY().equals("")){
//						coordinates.add(coord.getX());
//						coordinates.add(coord.getY());
//					}
//					if(coord.getStatus().equals("0")){//计算图片数量
//						imgs.addAll(getOrginImgUrls(coord.getImageName()));
//					}
//					
//				}
				//分装坐标和图片信息
				List<CollectAuditSpecimenImage> collectAuditSpecimenImages = new ArrayList<CollectAuditSpecimenImage>();
//				for(String img:imgs){
//					CollectAuditSpecimenImage collectAuditSpecimenImage = new CollectAuditSpecimenImage();
//					collectAuditSpecimenImage.setThumbnai_url(img);
//					collectAuditSpecimenImage.setImage_url(img);
//					collectAuditSpecimenImages.add(collectAuditSpecimenImage);
//
//				}
				for(CollectToAuditEntity collect:collects){
					if(!existCollectMap.containsKey(collect.getBid())){
						//初始化信息
						CollectAudit collectAudit = new CollectAudit();
						collectAudit.setTask_amount(collect.getPay());
						collectAudit.setLocation_name(collect.getpName());
						collectAudit.setLocation_address(collect.getpDesc());
						collectAudit.setTask_freezing_time(collect.getfTime());
						collectAudit.setVerify_maintain_time(collect.getmTime());
						collectAudit.setId(collect.getBid().toString());
						collectAudit.setOriginal_task_name(collect.getbName());
						collectAudit.setCollect_task_name(collect.getbCollectName());      
						collectAudit.setStatus(collect.getStatus());
						collectAudit.setSubmit_time(new Date(collect.getSubmitTime()*1000));
						collectAudit.setUser_name(entity.getUserName());
						collectAudit.setSystem_type(collect.getOwnerId().toString());
						collectAudit.setOriginalCoordinates(coordinates.toArray(new Double[0]));
						collectAudit.setSpecimenImages(collectAuditSpecimenImages);
						collectAudit.setImages(new ArrayList<CollectAuditImage>());
						CollectTaskClazz clazz=taskClazzCacheComponent.getCollectTaskClazz(collect.getTaskClazzId());
						logger.info("同步采集类型["+clazz.getClazzName()+"]");
						if(clazz!=null){
							collectAudit.setTask_class_name(clazz.getClazzName());
							collectAudit.setTask_class_img_count(clazz.getClazzImgCount());
							collectAudit.setTask_class_far_img_count(clazz.getClazzFarImgCount());
							collectAudit.setTask_class_near_img_count(clazz.getClazzNearImgCount());
						}
						existCollectMap.put(collect.getBid(), collectAudit);
					}
				    CollectAudit collectAudit=existCollectMap.get(collect.getBid());
					//循环填入采集的图片
					if(collect.getStatus().equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
						CollectAuditImage collectAuditImage = new CollectAuditImage();
						collectAuditImage.setAudit_id(collect.getCid().toString());
						collectAuditImage
								.setThumbnai_url(getCollectImgUrl(collect.getImageName()));
						collectAuditImage
								.setImage_url(getCollectImgUrl(collect.getImageName()));
						if(collect.getGpsTime()!=null)
						collectAuditImage.setGps_time(new Date(collect.getGpsTime()*1000));
						collectAuditImage.setLon(collect.getX());
						collectAuditImage.setLat(collect.getY());
						if(collect.getPhotoTime()!=null)
						collectAuditImage.setPhotograph_time(new Date(collect.getPhotoTime()*1000));
						collectAuditImage.setPoint_accury(collect.getAccuracy());
						collectAuditImage.setPoint_level(collect.getLevels());
						collectAuditImage.setPosition(collect.getPosition());
						collectAuditImage.setIndex(collect.getImageIndex());
						collectAudit.getImages().add(collectAuditImage);
					}else{
						logger.error("任务["+collect.getBid()+"]状态错误");
						throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
					}
					
				}
				List<ResultEntity> l=new ArrayList<ResultEntity>();
				for(Long id:existCollectMap.keySet()){
					ResultEntity _entity=new ResultEntity();
					_entity.setInfo(existCollectMap.get(id));
					_entity.setSuccess(true);
					l.add(_entity);
				}
				return l;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(BusinessExceptionEnum.TASK_OUT_QUEUE_ERROR);
			}
		}
	}
	
	private List<String> getOrginImgUrls(String url){
		if (url == null) {
			return new ArrayList<String>();
		}
		String[] spiltOne = url.split("\\.");
		String[] splitTwo = spiltOne[0].split("_");
		List<String> list = new ArrayList<String>();
		String commonUrl = this.propertiesConfig.getProperty(CommonConstant.ORGIN_IMG_URL).toString();
		StringBuffer sb = new StringBuffer("");
		StringBuffer sb_1 = new StringBuffer("");
		for (int i = 0; i < splitTwo.length - 1; i++) {
			sb_1.append(splitTwo[i]);
			sb_1.append("_");
			if (i < splitTwo.length - 2) {
				sb.append(File.separator);
				sb.append(splitTwo[i]);
			}
		}
		commonUrl = commonUrl + sb.toString() + "/" + sb_1.toString();
		for (int i = 0; i < new Integer(splitTwo[splitTwo.length - 1]); i++) {
			list.add(commonUrl + i + "." + spiltOne[1]);
		}
		return list;
	}
	private String getCollectImgUrl(String imageName){
		if(imageName==null||imageName.equals("")){
			return null;
		}
		String commonUrl = this.propertiesConfig.getProperty(CommonConstant.COLLECT_IMG_URL).toString();
		String[] spiltOne = imageName.split("\\.");
		String[] splitTwo = spiltOne[0].split("_");
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < splitTwo.length - 1; i++) {
			sb.append(File.separator);
			sb.append(splitTwo[i]);
		}
		return commonUrl+sb.toString()+File.separator+imageName;
	}
	/**
	 * 发送消息到审核
	 * @param entity
	 * @throws BusinessException
	 */
	@Component
	public class SendCollectInfoToAuditQueue implements CollectCore<Object,ResultEntity>{
		@Autowired
		public SendCollectInfoToAuditQueue(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@Override
		public Object execute(ResultEntity entity) throws BusinessException {
			//准备队列信息
			try {
				String host=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_HOST).toString();
				Integer port=new Integer(propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_PORT).toString());
				String queueName=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_NAME).toString();
				Long id=new Long(queueName.hashCode());
				RabbitMQUtils.send(String.valueOf(id), host, port, null, null, queueName, entity, true);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				//发送消息失败后不再抛出异常，直接记录到数据库 不影响客户采集
				CollectSendMessageError error=new CollectSendMessageError();
				CollectAudit audit=(CollectAudit)entity.getInfo();
				error.setUserId(Long.valueOf(collectUserCacheComponent.getUserIdFromCache(audit.getUser_name(),USER_CACHE_TYPE.REDIS.getCode())));
				error.setTaskId(Long.valueOf(audit.getId()));
				error.setErrorType(SEND_MESSAGE_ERROR_TYPE.AUDIT_OUT.getCode());
				collectSendMessageErrorDao.insertError(CommonConstant.getSingleDataSourceKey(), error);
			}
			return null;
		}
		
	}
	
	/**
	 * 发送消息到审核
	 * @param entity
	 * @throws BusinessException
	 */
	@Component
	public class SendCollectInfoJsonToAuditQueue implements CollectCore<Object,ResultEntity>{
		@Autowired
		public SendCollectInfoJsonToAuditQueue(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@Override
		public Object execute(ResultEntity entity) throws BusinessException {
			//准备队列信息
			try {
//				JsonBinder.buildNormalBinder(false).toJson(entity);
				String host=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_HOST).toString();
				Integer port=new Integer(propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_PORT).toString());
				String queueName=propertiesConfig.getProperty(CommonConstant.COLLECT_SYNC_QUEUE_OUT_NAME).toString();
				Long id=new Long(queueName.hashCode());
				RabbitMQUtils.send(String.valueOf(id), host, port, null, null, queueName, entity, true);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				//发送消息失败后不再抛出异常，直接记录到数据库 不影响客户采集
				CollectSendMessageError error=new CollectSendMessageError();
				CollectAudit audit=(CollectAudit)entity.getInfo();
				error.setUserId(Long.valueOf(collectUserCacheComponent.getUserIdFromCache(audit.getUser_name(),USER_CACHE_TYPE.REDIS.getCode())));
				error.setTaskId(Long.valueOf(audit.getId()));
				error.setErrorType(SEND_MESSAGE_ERROR_TYPE.AUDIT_OUT.getCode());
				collectSendMessageErrorDao.insertError(CommonConstant.getSingleDataSourceKey(), error);
			}
			return null;
		}
		
	}
	
	@Component
	public class SendImagePathToWaterQueue implements CollectCore<Object,WaterImgMessageEntity>{
		@Autowired
		public SendImagePathToWaterQueue(SyncTaskBusinessMananger syncTaskBusinessMananger) {
		}

		@Override
		public Object execute(WaterImgMessageEntity entity) throws BusinessException {
			//准备队列信息
			try {
				if(entity.getImgUrls().size()>0){
					String host=propertiesConfig.getProperty(CommonConstant.WATER_SYNC_QUEUE_OUT_HOST).toString();
					Integer port=new Integer(propertiesConfig.getProperty(CommonConstant.WATER_SYNC_QUEUE_OUT_PORT).toString());
					String queueName=propertiesConfig.getProperty(CommonConstant.WATER_SYNC_QUEUE_OUT_NAME).toString();
					Long id=new Long(queueName.hashCode());
					RabbitMQUtils.send(String.valueOf(id), host, port, null, null, queueName, JsonBinder.buildNonNullBinder(false).toJson(entity).getBytes(), true);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				//发送消息失败后不再抛出异常，直接记录到数据库 不影响客户采集
				CollectSendMessageError error=new CollectSendMessageError();
				error.setUserId(Long.valueOf(entity.getUserId()));
				error.setTaskId(Long.valueOf(entity.getTaskId()));
				error.setErrorType(SEND_MESSAGE_ERROR_TYPE.WATER_OUT.getCode());
				collectSendMessageErrorDao.insertError(CommonConstant.getSingleDataSourceKey(), error);
			}
			return null;
		}
		
	}
	
	
}
