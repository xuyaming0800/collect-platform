package com.autonavi.collect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_EXTRA_OPERATION;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_TYPE;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.dao.CollectTaskImageDao;
import com.autonavi.collect.dao.CollectTaskTokenDao;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.TaskExtraInfoEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

@Component
public class InitiativeTaskBusinessManager {
	
	private PropertiesConfig propertiesConfig;
	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private CollectTaskImageDao collectTaskImageDao;
	@Autowired
	private CollectTaskTokenDao collectTaskTokenDao;
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	@Autowired
	private TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;

	@Autowired
	private TaskSave taskSave;
	@Autowired
	private TaskSaveCheck taskSaveCheck;
	@Autowired
	private TaskSubmitCheck taskSubmitCheck;
	@Autowired
	private TaskSubmit taskSubmit;
	
	@Autowired
	private TaskDelete taskDelete;
	@Autowired
	private TaskDeleteCheck taskDeleteCheck;

	public TaskSave getTaskSave() {
		return taskSave;
	}

	public TaskSaveCheck getTaskSaveCheck() {
		return taskSaveCheck;
	}
	
	

	public TaskSubmitCheck getTaskSubmitCheck() {
		return taskSubmitCheck;
	}

	public TaskSubmit getTaskSubmit() {
		return taskSubmit;
	}
	
	

	public TaskDelete getTaskDelete() {
		return taskDelete;
	}

	public TaskDeleteCheck getTaskDeleteCheck() {
		return taskDeleteCheck;
	}

	public InitiativeTaskBusinessManager() throws Exception {
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
	}

	// 主动任务保存检测
	@Component
	public class TaskSaveCheck implements
			CollectCore<CollectTaskSaveEntity, CollectTaskSaveEntity> {

		@Autowired
		public TaskSaveCheck(
				InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

		}

		@Override
		public CollectTaskSaveEntity execute(
				CollectTaskSaveEntity collectTaskSaveEntity)
				throws BusinessException {
			// 获得单一数据源的key
			CollectTaskBase collectTaskBase = collectTaskSaveEntity
					.getCollectTaskBase();
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao
					.getCurrentTime(dataSourceKey);
			// 检测是否为主动任务
			if (collectTaskBase.getPassiveId() != null) {
				throw new BusinessException(
						BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
			}
			// 判断任务是否已经保存过
			if (collectTaskBase.getId() != null) {
				// 查询任务信息
				CollectTaskBase collectTaskbaseDb = (CollectTaskBase) collectTaskBaseDao
						.selectTaskDataForCollect(collectTaskBase);
				// 检测是否存在
				if (collectTaskbaseDb == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_NOT_EXIST);
				}
				// 检测任务是否是当前用户的
				if (collectTaskbaseDb.getCollectUserId() == null
						|| !collectTaskbaseDb.getCollectUserId().equals(
								collectTaskBase.getCollectUserId())) {
					throw new BusinessException(
							BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
				//检测是否是当前的业主品类
				if (collectTaskbaseDb.getOwnerId() == null
						|| !collectTaskbaseDb.getOwnerId().equals(
								collectTaskBase.getOwnerId())) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
				}
				if (collectTaskbaseDb.getPassiveId() != null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
				}
				//监测任务类型
				if (collectTaskbaseDb.getTaskClazzId() != null&&collectTaskBase.getTaskClazzId()!=null
						&& !collectTaskbaseDb.getTaskClazzId().equals(
								collectTaskBase.getTaskClazzId())) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_CLAZZ_IS_CHANGE);
				}
				// 任务是否超期
				if (collectTaskbaseDb.getAllotEndTime() == null
						|| currentTime > collectTaskbaseDb.getAllotEndTime()) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
				}
				// 检测任务包是否存在
				if (collectTaskbaseDb.getTaskPackageId() == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				CollectBasePackage collectBasePackageDb = (CollectBasePackage) collectBasePackageDao
						.getCollectBasePackageById(
								collectTaskbaseDb.getTaskPackageId(),
								collectTaskbaseDb.getCollectUserId(),
								collectTaskbaseDb.getOwnerId());
				if (collectBasePackageDb == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				// 检测任务是否是当前用户的
				if (collectBasePackageDb.getCollectUserId() == null
						|| !collectBasePackageDb.getCollectUserId().equals(
								collectTaskBase.getCollectUserId())) {
					throw new BusinessException(
							BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
				//检测用户品类
				if (collectBasePackageDb.getOwnerId() == null
						|| !collectBasePackageDb.getOwnerId().equals(
								collectTaskBase.getOwnerId())) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
				}
				if (collectBasePackageDb.getPassivePackageId() != null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
				}
				// 任务是否超期
				if (collectBasePackageDb.getAllotEndTime() == null
						|| currentTime > collectBasePackageDb.getAllotEndTime()) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
				}
				// 检测任务包状态
				Integer taskStatus_DB = collectBasePackageDb
						.getTaskPackageStatus();

				if (taskStatus_DB == null)
					taskStatus_DB = -1;
				if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
					throw new BusinessException(
							BusinessExceptionEnum.TASK_FREEZE);
				} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())||taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已保存 //领取
					collectTaskBase.setTaskPackageId(collectTaskbaseDb
							.getTaskPackageId());
					// 检测任务状态
					taskStatus_DB = collectTaskbaseDb.getTaskStatus();
					if (taskStatus_DB == null)
						taskStatus_DB = -1;
					if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB
							.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE
							.getCode())) {// 冻结
						throw new BusinessException(
								BusinessExceptionEnum.TASK_FREEZE);
					} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())||taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已保存 //领取
						CollectBasePackage basePackage = new CollectBasePackage();
						basePackage.setTaskPackageName(collectTaskBase
								.getCollectDataName());
						basePackage.setId(collectTaskbaseDb.getTaskPackageId());
						basePackage.setAllotUserId(collectTaskBase
								.getCollectUserId());
						basePackage.setCollectUserId(collectTaskBase
								.getCollectUserId());
						basePackage.setTaskClazzId(collectTaskBase
								.getTaskClazzId());
						basePackage.setOwnerId(collectBasePackageDb.getOwnerId());
						collectTaskSaveEntity.setNewTaskClazzId(collectTaskBase.getTaskClazzId());
						collectTaskSaveEntity.setCurrentTaskClazzId(collectBasePackageDb.getTaskClazzId());
						collectTaskSaveEntity
								.setCollectBasePackage(basePackage);
						collectTaskBase.setTaskStatus(taskStatus_DB);
						return collectTaskSaveEntity;
					} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT
							.getCode())) {// 已提交
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
					} else if (taskStatus_DB.equals(TASK_STATUS.FINISH
							.getCode())) {// 完成
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_FINISH);
					} else {
						throw new BusinessException(
								BusinessExceptionEnum.LOGIC_ERROR);
					}
				} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT.getCode())) {// 已提交
					throw new BusinessException(
							BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
				} else if (taskStatus_DB.equals(TASK_STATUS.FINISH.getCode())) {// 完成
					throw new BusinessException(
							BusinessExceptionEnum.TASK_ALREADY_FINISH);
				} else {
					throw new BusinessException(
							BusinessExceptionEnum.LOGIC_ERROR);
				}
			} else {
				
				collectTaskBase.setReleaseFreezeTime(new Integer(
						propertiesConfig.getProperty(
								CommonConstant.INITIATIVE_RELEASE_FREEZE_TIME)
								.toString()));
				collectTaskBase.setAllotUserId(collectTaskBase
						.getCollectUserId());
				CollectBasePackage basePackage = new CollectBasePackage();
				basePackage.setTaskPackageName(collectTaskBase
						.getCollectDataName());
				basePackage.setAllotUserId(collectTaskBase.getCollectUserId());
				basePackage
						.setCollectUserId(collectTaskBase.getCollectUserId());
				basePackage.setTaskClazzId(collectTaskBase.getTaskClazzId());
			
				basePackage.setTaskPackageStatus(TASK_STATUS.SAVE.getCode());
				basePackage.setAllotMaintainTime(new Integer(propertiesConfig
						.getProperty(CommonConstant.INITIATIVE_SAVE_TIME)
						.toString()));
				basePackage.setVerifyMaintainTime(new Integer(propertiesConfig
						.getProperty(CommonConstant.INITIATIVE_VERIFY_TIME)
						.toString()));
				basePackage.setVerifyFreezeTime(new Integer(propertiesConfig
						.getProperty(
								CommonConstant.INITIATIVE_VERIFY_FREEZE_TIME)
						.toString()));
				basePackage.setOwnerId(collectTaskBase.getOwnerId());
				collectTaskSaveEntity.setCollectBasePackage(basePackage);
				return collectTaskSaveEntity;
			}
		}
	}

	// 主动任务保存
	@Component
	public class TaskSave implements
			CollectCore<CollectTaskBase, CollectTaskSaveEntity> {
		@Autowired
		public TaskSave(
				InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

		}

		@Override
		public CollectTaskBase execute(CollectTaskSaveEntity entity)
				throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			boolean isNew=false;
			CollectTaskBase collectTaskBase = entity.getCollectTaskBase();
			CollectBasePackage basePackage = entity.getCollectBasePackage();
			Long currentTime = (Long) collectPassiveTaskDao
					.getCurrentTime(dataSourceKey);
			collectTaskBase.setTaskSaveTime(currentTime);
			collectTaskBase.setUpdateTime(currentTime);
			basePackage.setUpdateTime(currentTime);
			basePackage.setTaskPackageStatus(TASK_STATUS.SAVE.getCode());
			Double money=0.0D;
			boolean flag=false;
			List<TaskExtraInfoEntity> extraList=entity.getTaskExtraInfoEntityList();
			for(TaskExtraInfoEntity _ent:extraList){
				if(_ent.getMoneyChange()){
					flag=true;
					if(_ent.getOperation().equals(TASK_EXTRA_OPERATION.UPDATE.getCode()+"")){
						money=money+Double.valueOf(_ent.getMoney());
					}else{
						money=money-Double.valueOf(_ent.getMoney());
					}
				}
			}
			// 判定任务是重新保存还是新建保存
			if (collectTaskBase.getId() != null) {
				// 更新
				Integer i = (Integer) collectTaskBaseDao.update(
						collectTaskBase, collectTaskBase.getTaskStatus()
								.intValue(), TASK_STATUS.SAVE.getCode());
				if (i != 1) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
				}
				// 更新任务包名字
				if(flag){
				  //通用模式
			     basePackage.setTaskPackagePay(money);	
				 collectBasePackageDao.updatePackageNameNew(basePackage);
				}else{
				  //未采用H5模式	或者金额没发生变化的时候
				 collectBasePackageDao.updatePackageName(basePackage);
				}
			} else {
				// 同时创建一个任务包 默认模式为已经领取
				basePackage.setTaskPackageStatus(TASK_STATUS.RECEIVE.getCode());
				collectTaskBase.setTaskStatus(TASK_STATUS.RECEIVE.getCode());
				//暂时兼容旧版的非H5拍拍
				Long ownerId=collectTaskBase.getOwnerId();
				if(ownerId==1||ownerId==2){
					basePackage.setTaskPackageStatus(TASK_STATUS.SAVE.getCode());
					collectTaskBase.setTaskStatus(TASK_STATUS.SAVE.getCode());
				}
				basePackage.setCreateTime(currentTime);
				basePackage.setUpdateTime(currentTime);
				basePackage.setAllotEndTime(basePackage.getAllotMaintainTime()
						* 60 * 60L + currentTime);
				basePackage.setOwnerId(entity.getCollectTaskBase().getOwnerId());
				if(flag){
					 basePackage.setTaskPackagePay(money);	
				}
				collectBasePackageDao.insert(basePackage);
				// 插入
				collectTaskBase.setTaskPackageId(DaoHelper.getPrimaryKey());
				collectTaskBase.setAllotEndTime(basePackage
						.getAllotMaintainTime() * 60 * 60L+ currentTime);
				collectTaskBase.setCreateTime(currentTime);
			
				collectTaskBase.setTaskType(TASK_TYPE.INITIATIVE.getCode());
				collectTaskBaseDao.insert(collectTaskBase);
				collectTaskBase.setId(DaoHelper.getPrimaryKey());
				isNew=true;
				
			}
			// 更新图片信息
			Map<Long,Long> tempMap=new HashMap<Long,Long>();
			List<CollectTaskImg> tempImgList=new ArrayList<CollectTaskImg>();
			if(entity.getCollectTaskImgList()!=null&&entity.getCollectTaskImgList().size()>0){
				collectTaskImageDao.insert(collectTaskBase,
						entity.getCollectTaskImgList());
				for(CollectTaskImg img:entity.getCollectTaskImgList()){
					Long batchId=0L;
					if(img.getImageBatchId()!=null){
						batchId=img.getImageBatchId();
					}
					if(!tempMap.containsKey(batchId)){
						tempMap.put(batchId, img.getTempBatchId());
					}
				}
			}
			for(Long batchId:tempMap.keySet()){
				CollectTaskImg img=new CollectTaskImg();
				if(batchId.equals(0L)){
					img.setImageBatchId(null);	
				}else{
					img.setImageBatchId(batchId);
				}
				img.setTempBatchId(tempMap.get(batchId));
				tempImgList.add(img);
			}
			if(tempImgList.size()>0){
				collectTaskImageDao.batchUpdateToUnUseByTemp(collectTaskBase, tempImgList);
			}
			tempImgList.clear();
			tempMap.clear();
			
			
			if (entity.getCollectTaskToken() != null) {// 更新token BAse_ID
				collectTaskTokenDao.updateTaskTokenBaseId(
						entity.getCollectTaskToken(), collectTaskBase);
			}
			entity.setIsNew(isNew);
			//额外信息更新
			List<Long> batchIdList=taskCollectUtilBusinessManager.getUpdateTaskExtraInfo().execute(entity);
			//作废无用的图片
			if(batchIdList.size()>0){
				collectTaskImageDao.batchUpdateToUnUse(collectTaskBase, batchIdList);
			}
			// 查询返回结果-有效时间
			return (CollectTaskBase) collectTaskBaseDao
					.selectTaskDataForCollect(collectTaskBase);
		}
	}
    //主动任务提交校验
	@Component
	public class TaskSubmitCheck implements
			CollectCore<CollectTaskSubmitEntity, CollectTaskSubmitEntity> {

		@Autowired
		public TaskSubmitCheck(
				InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

		}

		@Override
		public CollectTaskSubmitEntity execute(CollectTaskSubmitEntity collectTaskSubmitEntity)
				throws BusinessException {
			// 获得单一数据源的key
			CollectTaskBase collectTaskBase = collectTaskSubmitEntity
					.getCollectTaskBase();
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao
					.getCurrentTime(dataSourceKey);
			// 检测是否为主动任务
			if (collectTaskBase.getPassiveId() != null) {
				throw new BusinessException(
						BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
			}
			// 判断任务是否已保存过
			if (collectTaskBase.getId() != null) {
				// 查询任务信息
				CollectTaskBase collectTaskbaseDb = (CollectTaskBase) collectTaskBaseDao
						.selectTaskDataForCollect(collectTaskBase);
				// 检测是否存在
				if (collectTaskbaseDb == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_NOT_EXIST);
				}
				// 检测任务是否是当前用户的
				if (collectTaskbaseDb.getCollectUserId() == null
						|| !collectTaskbaseDb.getCollectUserId().equals(
								collectTaskBase.getCollectUserId())) {
					throw new BusinessException(
							BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
				//检测是否是当前的业主品类
				if (collectTaskbaseDb.getOwnerId() == null
						|| !collectTaskbaseDb.getOwnerId().equals(
								collectTaskBase.getOwnerId())) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
				}
				if (collectTaskbaseDb.getPassiveId() != null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
				}
				// 任务是否超期
				if (collectTaskbaseDb.getAllotEndTime() == null
						|| currentTime > collectTaskbaseDb.getAllotEndTime()) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
				}
				// 检测任务包是否存在
				if (collectTaskbaseDb.getTaskPackageId() == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				CollectBasePackage collectBasePackageDb = (CollectBasePackage) collectBasePackageDao
						.getCollectBasePackageById(
								collectTaskbaseDb.getTaskPackageId(),
								collectTaskbaseDb.getCollectUserId(),
								collectTaskbaseDb.getOwnerId());
				if (collectBasePackageDb == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				// 检测任务是否是当前用户的
				if (collectBasePackageDb.getCollectUserId() == null
						|| !collectBasePackageDb.getCollectUserId().equals(
								collectTaskBase.getCollectUserId())) {
					throw new BusinessException(
							BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
				//检测用户品类
				if (collectBasePackageDb.getOwnerId() == null
						|| !collectBasePackageDb.getOwnerId().equals(
								collectTaskBase.getOwnerId())) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
				}
				if (collectBasePackageDb.getPassivePackageId() != null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
				}
				if (collectBasePackageDb.getPassivePackageId() != null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
				}
				// 任务是否超期
				if (collectBasePackageDb.getAllotEndTime() == null
						|| currentTime > collectBasePackageDb.getAllotEndTime()) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
				}
				// 检测任务包状态
				Integer taskStatus_DB = collectBasePackageDb
						.getTaskPackageStatus();

				if (taskStatus_DB == null)
					taskStatus_DB = -1;
				if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已领取
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
					throw new BusinessException(
							BusinessExceptionEnum.TASK_FREEZE);
				} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())) {// 已保存
					collectTaskBase.setTaskPackageId(collectTaskbaseDb
							.getTaskPackageId());
					// 检测任务状态
					taskStatus_DB = collectTaskbaseDb.getTaskStatus();
					if (taskStatus_DB == null)
						taskStatus_DB = -1;
					if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB.equals(TASK_STATUS.RECEIVE
							.getCode())) {// 已领取
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB
							.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE
							.getCode())) {// 冻结
						throw new BusinessException(
								BusinessExceptionEnum.TASK_FREEZE);
					} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())) {// 已保存
						CollectBasePackage basePackage = new CollectBasePackage();
						basePackage.setTaskPackageName(collectTaskBase
								.getCollectDataName());
						basePackage.setId(collectTaskbaseDb.getTaskPackageId());
						basePackage.setAllotUserId(collectTaskBase
								.getCollectUserId());
						basePackage.setCollectUserId(collectTaskBase
								.getCollectUserId());
						basePackage.setTaskClazzId(collectTaskBase
								.getTaskClazzId());
						basePackage.setTaskPackageStatus(collectBasePackageDb.getTaskPackageStatus());
						basePackage.setOwnerId(collectBasePackageDb.getOwnerId());
						collectTaskSubmitEntity.setCurrentTaskClazzId(collectBasePackageDb.getTaskClazzId());
						collectTaskSubmitEntity.setNewTaskClazzId(collectTaskBase.getTaskClazzId());
						collectTaskSubmitEntity
								.setCollectBasePackage(basePackage);
						collectTaskBase.setTaskStatus(TASK_STATUS.SAVE
								.getCode());
						return collectTaskSubmitEntity;
					} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT
							.getCode())) {// 已提交
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
					} else if (taskStatus_DB.equals(TASK_STATUS.FINISH
							.getCode())) {// 完成
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_FINISH);
					} else {
						throw new BusinessException(
								BusinessExceptionEnum.LOGIC_ERROR);
					}
				} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT.getCode())) {// 已提交
					throw new BusinessException(
							BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
				} else if (taskStatus_DB.equals(TASK_STATUS.FINISH.getCode())) {// 完成
					throw new BusinessException(
							BusinessExceptionEnum.TASK_ALREADY_FINISH);
				} else {
					throw new BusinessException(
							BusinessExceptionEnum.LOGIC_ERROR);
				}
			} else {
				collectTaskBase.setReleaseFreezeTime(new Integer(
						propertiesConfig.getProperty(
								CommonConstant.INITIATIVE_RELEASE_FREEZE_TIME)
								.toString()));
				collectTaskBase.setAllotUserId(collectTaskBase
						.getCollectUserId());
				CollectBasePackage basePackage = new CollectBasePackage();
				basePackage.setTaskPackageName(collectTaskBase
						.getCollectDataName());
				basePackage.setAllotUserId(collectTaskBase.getCollectUserId());
				basePackage
						.setCollectUserId(collectTaskBase.getCollectUserId());
				basePackage.setTaskClazzId(collectTaskBase.getTaskClazzId());
				basePackage.setTaskPackageStatus(TASK_STATUS.SUBMIT.getCode());
				basePackage.setAllotMaintainTime(new Integer(propertiesConfig
						.getProperty(CommonConstant.INITIATIVE_SAVE_TIME)
						.toString()));
				basePackage.setVerifyMaintainTime(new Integer(propertiesConfig
						.getProperty(CommonConstant.INITIATIVE_VERIFY_TIME)
						.toString()));
				basePackage.setVerifyFreezeTime(new Integer(propertiesConfig
						.getProperty(
								CommonConstant.INITIATIVE_VERIFY_FREEZE_TIME)
						.toString()));
				basePackage.setOwnerId(collectTaskBase.getOwnerId());
				collectTaskSubmitEntity.setCollectBasePackage(basePackage);
				return collectTaskSubmitEntity;
			}
		}
	}
	// 主动任务保存 约定1对1
		@Component
		public class TaskSubmit implements
				CollectCore<CollectTaskSubmitEntity, CollectTaskSubmitEntity> {
			@Autowired
			public TaskSubmit(
					InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

			}

			@Override
			public CollectTaskSubmitEntity execute(CollectTaskSubmitEntity entity)
					throws BusinessException {
				Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
				CollectTaskBase collectTaskBase = entity.getCollectTaskBase();
				CollectBasePackage basePackage = entity.getCollectBasePackage();
				Long currentTime = (Long) collectPassiveTaskDao
						.getCurrentTime(dataSourceKey);
				collectTaskBase.setTaskSaveTime(currentTime);
				collectTaskBase.setTaskSubmitTime(currentTime);
				collectTaskBase.setUpdateTime(currentTime);
				basePackage.setUpdateTime(currentTime);
				boolean flag=false;
				List<CollectTaskImg> imgList=entity.getCollectTaskImgList();
				for(CollectTaskImg img:imgList){
					if(img.getLevel()!=null&&img.getImageBatchId()!=null&&img.getImageH5Id()!=null){
						flag=true;
						break;
					}
				}
				// 判定任务是重新保存还是新建保存
				if (collectTaskBase.getId() != null) {
					// 更新
					Integer i = (Integer) collectTaskBaseDao.update(
							collectTaskBase, collectTaskBase.getTaskStatus()
									.intValue(), TASK_STATUS.SUBMIT.getCode());
					if (i != 1) {
						throw new BusinessException(
								BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
					}
					//任务包更新+约定1对1
					if(flag){
						collectBasePackageDao.updateInitiativePackageStatusNew(entity.getCollectBasePackage(), TASK_STATUS.SUBMIT.getCode());
					}else{
						collectBasePackageDao.updateInitiativePackageStatus(entity.getCollectBasePackage(), TASK_STATUS.SUBMIT.getCode());
					}

				} else {
					// 同时创建一个任务包
					basePackage.setCreateTime(currentTime);
					basePackage.setUpdateTime(currentTime);
					basePackage.setSubmitTime(currentTime);
					basePackage.setAllotEndTime(basePackage.getAllotMaintainTime()
							* 60 * 60 + currentTime);
					basePackage.setOwnerId(entity.getCollectTaskBase().getOwnerId());
					collectBasePackageDao.insert(basePackage);
					basePackage.setId(DaoHelper.getPrimaryKey());
					// 插入
					collectTaskBase.setTaskPackageId(DaoHelper.getPrimaryKey());
					collectTaskBase.setAllotEndTime(basePackage
							.getAllotMaintainTime() * 60 * 60 + currentTime);
					collectTaskBase.setCreateTime(currentTime);
					collectTaskBase.setTaskStatus(TASK_STATUS.SUBMIT.getCode());
					collectTaskBase.setTaskType(TASK_TYPE.INITIATIVE.getCode());
					collectTaskBaseDao.insert(collectTaskBase);
					collectTaskBase.setId(DaoHelper.getPrimaryKey());
					entity.setCollectBasePackage(basePackage);
				}
				// 更新图片信息
				collectTaskImageDao.insert(collectTaskBase,
						entity.getCollectTaskImgList());
				// 更新Token状态
				if (entity.getToken() != null) {
					CollectTaskToken token = new CollectTaskToken();
					token.setToken(entity.getToken());
					token.setUserId(collectTaskBase.getCollectUserId());
					token.setTokenStatus(CommonConstant.TOKEN_STATUS_COMPLTETE);
					token.setOwnerId(collectTaskBase.getOwnerId());
					collectTaskTokenDao.updateTaskTokenBaseId(token,collectTaskBase);
					collectTaskTokenDao.updateTaskTokenStatus(token);
				}
				entity.setCollectTaskBase((CollectTaskBase) collectTaskBaseDao
						.selectTaskDataForCollect(collectTaskBase));
				//额外信息
				taskCollectUtilBusinessManager.getSubmitTaskExtraInfo().execute(entity);
				// 查询返回结果-有效时间
				return entity;
			}
		}
		// 主动任务删除检测
		@Component
		public class TaskDeleteCheck implements
				CollectCore<CollectBasePackage, CollectBasePackage> {

			@Autowired
			public TaskDeleteCheck(
					InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

			}

			@Override
			public CollectBasePackage execute(
					CollectBasePackage collectBasePackage)
					throws BusinessException {
				// 判断任务是否已经保存过
				if (collectBasePackage.getId() != null) {
					//需要锁定记录 防止批量超时置位时候出现问题
//					collectBasePackageDao.lockBasePackageById(collectBasePackage);
					// 查询任务信息
					CollectBasePackage collectBasePackageDb = (CollectBasePackage) collectBasePackageDao
							.getCollectBasePackageById(collectBasePackage.getId(), collectBasePackage.getAllotUserId()
									,collectBasePackage.getOwnerId());
					// 检测是否存在
					if (collectBasePackageDb == null) {
						throw new BusinessException(
								BusinessExceptionEnum.TASK_NOT_EXIST);
					}
					//业主校验
					if (collectBasePackageDb.getOwnerId()==null||!collectBasePackageDb.getOwnerId().equals(collectBasePackage.getOwnerId())){
						throw new BusinessException(
								BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
					}
					// 检测任务是否是当前用户的
					if (collectBasePackageDb.getAllotUserId() == null
							|| !collectBasePackageDb.getAllotUserId().equals(
									collectBasePackage.getAllotUserId())) {
						throw new BusinessException(
								BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
					}
					if (collectBasePackageDb.getPassivePackageId() != null) {
						throw new BusinessException(
								BusinessExceptionEnum.TASK_IS_NOT_INITIATIVE);
					}
					// 检测任务包状态
					Integer taskStatus_DB = collectBasePackageDb
							.getTaskPackageStatus();

					if (taskStatus_DB == null)
						taskStatus_DB = -1;
					if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
						throw new BusinessException(
								BusinessExceptionEnum.TASK_MUST_SAVE);
					} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
						throw new BusinessException(
								BusinessExceptionEnum.TASK_FREEZE);
					} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())||taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已保存
						return collectBasePackageDb;
					} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT.getCode())) {// 已提交
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
					} else if (taskStatus_DB.equals(TASK_STATUS.FINISH.getCode())) {// 完成
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_FINISH);
					} else if (taskStatus_DB.equals(TASK_STATUS.FIRST_AUDIT.getCode())) {// 完成
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_FIRST_AUDIT);
					} else if (taskStatus_DB.equals(TASK_STATUS.TIME_OUT.getCode())) {// 已超时
						throw new BusinessException(
								BusinessExceptionEnum.TASK_ALREADY_TIME_OUT);
					} else {
						throw new BusinessException(
								BusinessExceptionEnum.LOGIC_ERROR);
					}
				} else {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_NOT_EXIST);
				}
			}
		}
		@Component
		public class TaskDelete implements
				CollectCore<Integer, CollectBasePackage> {

			@Autowired
			public TaskDelete(
					InitiativeTaskBusinessManager initiativeTaskBusinessManager) {

			}

			@SuppressWarnings("unchecked")
			@Override
			public Integer execute(
					CollectBasePackage collectBasePackage)
					throws BusinessException {
				try {
					// 获得单一数据源的key
					Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
					Long currentTime = (Long) collectPassiveTaskDao
							.getCurrentTime(dataSourceKey);
					collectBasePackage.setUpdateTime(currentTime);
					//删除任务包
					collectBasePackageDao.updateInitiativePackageStatus(collectBasePackage, TASK_STATUS.DELETE.getCode());
					//删除任务
					collectTaskBaseDao.update(collectBasePackage.getId(),TASK_STATUS.DELETE.getCode(),currentTime,collectBasePackage.getCollectUserId(),collectBasePackage.getOwnerId());
					//清理mongodb
					collectBasePackage.setCollectUserId(collectBasePackage.getAllotUserId());
					List<CollectTaskBase> list=(List<CollectTaskBase>)collectTaskBaseDao.
							selectByPackage(collectBasePackage);
					for(CollectTaskBase base:list){
						base.setCollectUserId(base.getAllotUserId());
						collectTaskImageDao.batchUpdateToUnUse(base);
					}
					for(CollectTaskBase base:list){
						taskCollectUtilBusinessManager.getDeleteWholeTaskExtra().execute(base);
					}
					return 1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BusinessException(BusinessExceptionEnum.TASK_DELETE_ERROR);
				}
			}
		}
}
