package com.autonavi.collect.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_VERIFY_STATUS;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.entity.CollectTaskReAuditEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

@Component
public class ReAuditTaskBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	
	@Autowired
	private ReAuditTaskCheck reAuditTaskCheck;
	@Autowired
	private ReAuditTask reAuditTask;
	
	
	
	public ReAuditTaskCheck getReAuditTaskCheck() {
		return reAuditTaskCheck;
	}

	public ReAuditTask getReAuditTask() {
		return reAuditTask;
	}

	@Component
	public class ReAuditTaskCheck implements
			CollectCore<CollectTaskReAuditEntity, CollectTaskReAuditEntity> {

		@Autowired
		public ReAuditTaskCheck(
				ReAuditTaskBusinessManager reAuditTaskBusinessManager) {

		}

		@Override
		public CollectTaskReAuditEntity execute(CollectTaskReAuditEntity entity)
				throws BusinessException {
			// 检测是否有ID
			if (entity.getBasePackageId() == null) {
				throw new BusinessException(
						BusinessExceptionEnum.TASK_ID_ERROR);
			}
			if (entity.getUserId() == null) {
				throw new BusinessException(
						BusinessExceptionEnum.USERID_IS_NULL);
			}
			CollectBasePackage collectBasePackageDb = (CollectBasePackage) collectBasePackageDao
					.getCollectBasePackageById(entity.getBasePackageId(), entity.getUserId()
							,entity.getOwnerId());
			if(collectBasePackageDb==null){
				throw new BusinessException(
						BusinessExceptionEnum.TASK_NOT_EXIST);
			}
			//校验业主
			if(collectBasePackageDb.getOwnerId()==null||!collectBasePackageDb.getOwnerId().equals(entity.getOwnerId())){
				throw new BusinessException(
						BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
			}
			//检测用户
			if(!collectBasePackageDb.getCollectUserId().equals(entity.getUserId())){
				throw new BusinessException(
						BusinessExceptionEnum.TASK_NOT_EXIST);
			}
			//判断状态
			if(!collectBasePackageDb.getTaskPackageStatus().equals(TASK_STATUS.FIRST_AUDIT.getCode())){
				logger.error("任务包ID=["+collectBasePackageDb.getId()+"]状态["+collectBasePackageDb.getTaskPackageStatus()+"] 错误 不是初审状态 无法申诉!");
				throw new BusinessException(
						BusinessExceptionEnum.TASK_NOT_FIRST_AUDIT);
			}
			//判断审核状态
			if(!collectBasePackageDb.getTaskPackageVerifyStatus().equals(TASK_VERIFY_STATUS.FIRST_FAIL.getCode())){
				logger.error("任务包ID=["+collectBasePackageDb.getId()+"]审核状态["+collectBasePackageDb.getTaskPackageVerifyStatus()+"] 错误 不是初审无效状态 无法申诉!");
				throw new BusinessException(
						BusinessExceptionEnum.TASK_NOT_FIRST_AUDIT_FAIL);
			}
			entity.setCurrentStatus(collectBasePackageDb.getTaskPackageStatus());
			return entity;
				
		}
	}
	
	@Component
	public class ReAuditTask implements
			CollectCore<CollectTaskReAuditEntity, CollectTaskReAuditEntity> {

		@Autowired
		public ReAuditTask(
				ReAuditTaskBusinessManager reAuditTaskBusinessManager) {

		}

		@Override
		public CollectTaskReAuditEntity execute(CollectTaskReAuditEntity entity)
				throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao
					.getCurrentTime(dataSourceKey);
			//更新任务
			collectTaskBaseDao.update(entity.getBasePackageId(), TASK_STATUS.RE_AUDIT.getCode(), currentTime, entity.getUserId(),entity.getOwnerId());
			//更新任务包
			CollectBasePackage collectBasePackage=new CollectBasePackage();
			collectBasePackage.setAllotUserId(entity.getUserId());
			collectBasePackage.setCollectUserId(entity.getUserId());
			collectBasePackage.setTaskPackageStatus(entity.getCurrentStatus());
			collectBasePackage.setId(entity.getBasePackageId());
			collectBasePackage.setUpdateTime(currentTime);
			collectBasePackage.setOwnerId(entity.getOwnerId());
			collectBasePackageDao.updatePackageStatus(collectBasePackage, TASK_STATUS.RE_AUDIT.getCode());
			return entity;
				
		}
	}


}
