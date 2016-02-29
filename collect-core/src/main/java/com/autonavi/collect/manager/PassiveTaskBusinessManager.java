package com.autonavi.collect.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;
import autonavi.online.framework.sharding.dao.DaoHelper;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectOriginalCoordinate;
import com.autonavi.collect.bean.CollectPackageTimeoutBatch;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.bean.CollectTaskImg;
import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.ALLOT_USER_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_EXTRA_OPERATION;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectOriginalCoordinateDao;
import com.autonavi.collect.dao.CollectPackageTimeoutBatchDao;
import com.autonavi.collect.dao.CollectPassivePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.dao.CollectTaskImageDao;
import com.autonavi.collect.dao.CollectTaskTokenDao;
import com.autonavi.collect.entity.CollectTaskSaveEntity;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.TaskExtraInfoEntity;
import com.autonavi.collect.entity.TaskPackageSubmitCheckEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.util.DataSourceHolder;

/**
 * @author ang.ji
 *	被动任务管理类
 *  被动任务的任务模式为1对1模式 如果支持任务包-任务一对多模式 则需要客户端上面调用任务包提交接口 提交之后会将整包
 *  提交给审核 审核需要支持整包的的导入 待实现和改造
 */
@Component
public class PassiveTaskBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private TaskSave taskSave;
	@Autowired
	private TaskSaveCheck taskSaveCheck;
	@Autowired
	private TaskSubmitCheck taskSubmitCheck;
	@Autowired
	private TaskSubmit taskSubmit;
	@Autowired
	private TaskReceiveCheck taskReceiveCheck;
	@Autowired
	private TaskReceive taskReceive;
	@Autowired
	private TaskPackageReceiveCheck taskPackageReceiveCheck;
	@Autowired
	private TaskPackageReceive taskPackageReceive;
	@Autowired
	private TaskReleaseCheck taskReleaseCheck;
	@Autowired
	private TaskRelease taskRelease;
	@Autowired
	private TaskCollectUtilBusinessManager taskCollectUtilBusinessManager;
	@Autowired
	private TaskDeleteCheck taskDeleteCheck;
	@Autowired
	private TaskDelete taskDelete;
	@Autowired
	private TaskPackageUserRelease taskPackageUserRelease;
	@Autowired
	private TaskPackageAutoRelease taskPackageAutoRelease;
	@Autowired
	private TaskPackageTimeoutProceed taskPackageTimeoutProceed;
	@Autowired
	private TaskPackageTimeoutQuery taskPackageTimeoutQuery;
	@Autowired
	private TaskPackageTimeoutClean taskPackageTimeoutClean;
		
	
	
	public TaskPackageAutoRelease getTaskPackageAutoRelease() {
		return taskPackageAutoRelease;
	}

	public TaskPackageTimeoutProceed getTaskPackageTimeoutProceed() {
		return taskPackageTimeoutProceed;
	}

	public TaskPackageTimeoutQuery getTaskPackageTimeoutQuery() {
		return taskPackageTimeoutQuery;
	}

	public TaskDeleteCheck getTaskDeleteCheck() {
		return taskDeleteCheck;
	}

	public TaskDelete getTaskDelete() {
		return taskDelete;
	}

	public TaskReleaseCheck getTaskReleaseCheck() {
		return taskReleaseCheck;
	}

	public TaskRelease getTaskRelease() {
		return taskRelease;
	}

	public TaskPackageReceive getTaskPackageReceive() {
		return taskPackageReceive;
	}

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

	public TaskReceiveCheck getTaskReceiveCheck() {
		return taskReceiveCheck;
	}

	public TaskReceive getTaskReceive() {
		return taskReceive;
	}

	public TaskPackageReceiveCheck getTaskPackageReceiveCheck() {
		return taskPackageReceiveCheck;
	}
	
	

	public TaskPackageTimeoutClean getTaskPackageTimeoutClean() {
		return taskPackageTimeoutClean;
	}



	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private CollectTaskImageDao collectTaskImageDao;
	@Autowired
	private CollectTaskTokenDao collectTaskTokenDao;
	@Autowired
	private CollectTaskAllotUserDao collectTaskAllotUserDao;	
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;	
	@Autowired
	private CollectPassivePackageDao collectPassivePackageDao;	
	@Autowired
	private CollectOriginalCoordinateDao collectOriginalCoordinateDao;	
	@Autowired
	private CollectPackageTimeoutBatchDao collectPackageTimeoutBatchDao;
	
	
	@Autowired
	private TaskPackageSubmitCheck taskPackageSubmitCheck;
	
	public TaskPackageSubmitCheck getTaskPackageSubmitCheck() {
		return taskPackageSubmitCheck;
	}
	@Autowired
	private TaskPackageSubmit taskPackageSubmit;
	
	public TaskPackageSubmit getTaskPackageSubmit() {
		return taskPackageSubmit;
	}
	

	private PropertiesConfig propertiesConfig;

	public PassiveTaskBusinessManager() throws Exception {
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	
	

	@Component
	public class TaskReceiveCheck implements CollectCore<CollectPassiveTask, CollectTaskBase> {

		@Autowired
		public TaskReceiveCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {
		}

		@Override
		public CollectPassiveTask execute(CollectTaskBase collectTaskBase) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			CollectPassiveTask collectPassiveTask_DB = (CollectPassiveTask) collectPassiveTaskDao.select(dataSourceKey, collectTaskBase.getPassiveId());
			Integer taskStatus_DB = collectPassiveTask_DB.getTaskStatus();
			//  任务状态是已分配
			if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {
				Long id = (Long) collectTaskAllotUserDao.select(collectTaskBase);
				if (id != null && id.longValue() > 0) {// 如果在任务分配用户表中存在，即表示可以领取
					return collectPassiveTask_DB;
				}
			}
			throw new BusinessException(BusinessExceptionEnum.TASK_CANNOT_RECEIVE);
		}
	}
	
	@Component
	public class TaskPackageReceiveCheck implements CollectCore<CollectPassivePackage, CollectBasePackage> {

		@Autowired
		public TaskPackageReceiveCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {
		}

		@Override
		public CollectPassivePackage execute(CollectBasePackage collectBasePackage) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			//需要锁定记录 防止释放任务的出现问题
//			collectPassivePackageDao.lockPassivePackageById(dataSourceKey, collectBasePackage.getPassivePackageId());
			CollectPassivePackage collectPassivePackage_DB = (CollectPassivePackage) collectPassivePackageDao.getCollectPassivePackageById(dataSourceKey, collectBasePackage.getPassivePackageId());
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			if(collectPassivePackage_DB==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			Integer taskStatus_DB = collectPassivePackage_DB.getTaskPackageStatus();
			if(collectPassivePackage_DB.getAllotEndTime()!=null&&currentTime>collectPassivePackage_DB.getAllotEndTime()){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			//检测业主品类
			if(!collectPassivePackage_DB.getOwnerId().equals(collectBasePackage.getOwnerId())){
				throw new BusinessException(BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
			}
		
			// 任务状态是已分配
			if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {
				Long id = (Long) collectTaskAllotUserDao.selectByBasePackage(collectBasePackage,ALLOT_USER_STATUS.VALID.getCode());
				if (id != null && id.longValue() > 0) {// 如果在任务分配用户表中存在，即表示可以领取
					return collectPassivePackage_DB;
				}else{
					throw new BusinessException(BusinessExceptionEnum.TASK_CANNOT_RECEIVE);
				}
			}else if(taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())){
				//未分配的任务
				return collectPassivePackage_DB;
			}
			
			throw new BusinessException(BusinessExceptionEnum.TASK_CANNOT_RECEIVE);
		}
	}
	
	@Component
	public class TaskPackageReceive implements CollectCore<List<CollectTaskBase>, CollectBasePackage> {

		@Autowired
		public TaskPackageReceive(PassiveTaskBusinessManager passiveTaskBusinessManager) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<CollectTaskBase> execute(CollectBasePackage collectBasePackage) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			collectBasePackage.setUpdateTime(currentTime);
			List<CollectTaskBase> baseList=new ArrayList<CollectTaskBase>();
//			int days = new Integer(propertiesConfig.getProperty(CommonConstant.PROP_TASK_SAVE_MAX_TIME).toString());
//			collectTaskBase.setTaskSaveEndTime(DateUtils.addDays(new Date(currentTime * 1000), days).getTime() / 1000);
			//判断领取任务种类
			if(collectBasePackage.getTaskPackageStatus().equals(TASK_STATUS.UNALLOT.getCode())){
				//未分配任务 需要插入
				//构建原始任务包对象
				CollectPassivePackage collectPassivePackage=(CollectPassivePackage)collectPassivePackageDao.getCollectPassivePackageById(dataSourceKey, collectBasePackage.getPassivePackageId());
				Long allotEndTime=new Date(currentTime*1000+collectPassivePackage.getAllotMaintainTime()*60*60*1000L).getTime()/1000;
				collectBasePackage.setAllotEndTime(allotEndTime);
				//构建并插入分片的任务包
				collectPassivePackage.setCreateTime(currentTime);
				collectPassivePackage.setUpdateTime(currentTime);
				collectPassivePackage.setTaskPackageStatus(TASK_STATUS.RECEIVE.getCode());
				collectPassivePackage.setAllotUserId(collectBasePackage.getAllotUserId());
				collectPassivePackage.setCollectUserId(null);
				collectPassivePackage.setAllotEndTime(allotEndTime);
				collectPassivePackage.setOwnerId(collectPassivePackage.getOwnerId());
				collectBasePackageDao.insert(collectPassivePackage);
				Long newPid=DaoHelper.getPrimaryKey();
				//检测坐标是否已经存在
				Long count=(Long)collectOriginalCoordinateDao.checkBaseCoordExist(collectBasePackage);
				if(count==null){
					logger.warn("新框架版本过低，请更新新框架版本");
					throw new BusinessException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
				}
				if(count.equals(0L)){
				  //插入坐标表
					List<CollectOriginalCoordinate> coords=(List<CollectOriginalCoordinate>)collectOriginalCoordinateDao.selectOriginalCoordinateByPid(dataSourceKey, collectBasePackage);
					if(coords!=null)
					collectOriginalCoordinateDao.insertBaseCoordinates(coords,collectBasePackage.getAllotUserId(),collectBasePackage.getOwnerId());
				  
				}
				//获取任务包下所有任务
				List<CollectPassiveTask> tasks=(List<CollectPassiveTask>)collectPassiveTaskDao.selectCollectPassiveTaskByPid(dataSourceKey, collectBasePackage.getPassivePackageId());
				if(tasks!=null){
					for(CollectPassiveTask collectPassiveDb:tasks){
						collectPassiveDb.setTaskPackageId(newPid);
					    collectPassiveDb.setCreateTime(currentTime);
					    collectPassiveDb.setUpdateTime(currentTime);
					    collectPassiveDb.setTaskStatus(TASK_STATUS.RECEIVE.getCode());
					    collectPassiveDb.setAllotEndTime(allotEndTime);
					    collectPassiveDb.setAllotUserId(collectBasePackage.getAllotUserId());
					    CollectTaskBase base=new CollectTaskBase();
					    base.setPassiveId(collectPassiveDb.getId());
					    base.setTaskClazzId(collectPassiveDb.getTaskClazzId());
					    baseList.add(base);
					}
					//插入任务包
					collectTaskBaseDao.insert(tasks);
					int counts=0;
					for(Long pkId:DaoHelper.getPrimaryKeys()){
						CollectTaskBase base=baseList.get(counts);
						base.setId(pkId);
						counts++;
					}
				}
				collectBasePackage.setId(newPid);
				for(CollectTaskBase base:baseList){
					//初始化额外信息
					taskCollectUtilBusinessManager.getInitTaskExtraInfo().execute(base);
				}
				
			}else{
				//批量更新base表
				collectTaskBaseDao.receiveTasksByPackage(collectBasePackage, TASK_STATUS.RECEIVE.getCode());
				//跟新任务包表
				collectBasePackageDao.updatePackageStatus(collectBasePackage, TASK_STATUS.RECEIVE.getCode());
				//更新其他分片
				List<Integer> ds=new DataSourceHolder().getDsKey();
				if(ds.size()>1){
					//可优化 多数据源时候才使用 只对任务包进行状态控制 任务表不做
					for(Integer i:ds){
						collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.RECEIVE.getCode(), TASK_STATUS.ALLOT.getCode());
						collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.RECEIVE.getCode(),TASK_STATUS.ALLOT.getCode());
					}
				}
				collectBasePackage.setAllotEndTime(null);
			}
			//更新被动任务包表
			collectBasePackage.setAllotEndTime(null);
			int i=(Integer)collectPassivePackageDao.updatePackageStatus(dataSourceKey,collectBasePackage, TASK_STATUS.RECEIVE.getCode());
			if(i!=1){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
			}
			//批量更新被动任务表
			collectPassiveTaskDao.receiveTasksByPackage(dataSourceKey
					, collectBasePackage
					, TASK_STATUS.RECEIVE.getCode());
			
			
			return (List<CollectTaskBase>)collectTaskBaseDao.selectTasksForAllot(collectBasePackage);
		}
	}

	@Component
	public class TaskReceive implements CollectCore<CollectTaskBase, CollectTaskBase> {

		@Autowired
		public TaskReceive(PassiveTaskBusinessManager passiveTaskBusinessManager) {
		}

		@Override
		public CollectTaskBase execute(CollectTaskBase collectTaskBase) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			collectTaskBase.setUpdateTime(currentTime);
//			int days = new Integer(propertiesConfig.getProperty(CommonConstant.PROP_TASK_SAVE_MAX_TIME).toString());
//			collectTaskBase.setTaskSaveEndTime(DateUtils.addDays(new Date(currentTime * 1000), days).getTime() / 1000);
			collectPassiveTaskDao.receiveTask(dataSourceKey
					, collectTaskBase
					, TASK_STATUS.RECEIVE.getCode());
			collectTaskBaseDao.receiveTask(collectTaskBase, TASK_STATUS.RECEIVE.getCode());
			return (CollectTaskBase)collectTaskBaseDao.selectTaskDataForAllot(collectTaskBase);
		}
	}

	@Component
	public class TaskSubmitCheck implements CollectCore<CollectTaskSubmitEntity, CollectTaskSubmitEntity> {

		@Autowired
		public TaskSubmitCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public CollectTaskSubmitEntity execute(CollectTaskSubmitEntity entity) throws BusinessException {
			CollectTaskBase collectTaskBase=entity.getCollectTaskBase();
			// 获得单一数据源的key
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			CollectPassiveTask collectPassiveTask_DB = (CollectPassiveTask) collectPassiveTaskDao.select(dataSourceKey, collectTaskBase.getPassiveId());
			if (collectPassiveTask_DB == null) {
				throw new BusinessException(BusinessExceptionEnum.TASK_NOT_EXIST);
			}
			//领取后不再做原始任务结束时间校验
//			if(collectPassiveTask_DB.getAllotEndTime()!=null&&currentTime>collectPassiveTask_DB.getAllotEndTime()){
//				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
//			}
			if(collectPassiveTask_DB.getOwnerId()==null||!collectPassiveTask_DB.getOwnerId().equals(collectTaskBase.getOwnerId())){
				throw new BusinessException(BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
			}
			if(collectPassiveTask_DB.getAllotUserId()==null||!collectTaskBase.getCollectUserId().equals(collectPassiveTask_DB.getAllotUserId())){
				throw new BusinessException(
						BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
			}
			CollectTaskBase collectTaskBase_DB=(CollectTaskBase)collectTaskBaseDao.selectTaskDataForCollect(collectTaskBase);
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			if (collectTaskBase_DB == null) {// 校验是否存在传入的任务ID
				throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
			}
			if(collectTaskBase_DB.getAllotEndTime()!=null&&currentTime>collectTaskBase_DB.getAllotEndTime()){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			if(!collectTaskBase_DB.getTaskStatus().equals(collectPassiveTask_DB.getTaskStatus())){
				throw new BusinessException(BusinessExceptionEnum.LOGIC_ERROR);
			}
			collectTaskBase.setTaskPackageId(collectTaskBase_DB.getTaskPackageId());

			Integer taskStatus_DB = collectPassiveTask_DB.getTaskStatus();
			if (taskStatus_DB == null)
				taskStatus_DB = -1;
			if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
				throw new BusinessException(BusinessExceptionEnum.TASK_MUST_RECEIVE);
			} else if (taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已领取
				Long allotUserId = collectPassiveTask_DB.getAllotUserId();
				if (allotUserId.longValue() != collectTaskBase.getCollectUserId().longValue()) {// 如果实际采集用户不等于已分配的用户，抛出异常
					throw new BusinessException(BusinessExceptionEnum.ALLOT_USER_UNEQUAL);
				}
				if (collectTaskBase.getId() == null || collectTaskBase.getId() <= 0) {
					throw new BusinessException(BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				CollectTaskBase ctb = (CollectTaskBase) collectTaskBaseDao.checkDataById(collectTaskBase);
				if (ctb == null) {// 校验是否存在传入的任务ID
					throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
				}
				entity.setCollectPassiveTask(collectPassiveTask_DB);
				return entity;
			} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
				throw new BusinessException(BusinessExceptionEnum.TASK_MUST_RECEIVE);
			} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
				throw new BusinessException(BusinessExceptionEnum.TASK_FREEZE);
			} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())) {// 已保存
				if (collectTaskBase.getId() == null || collectTaskBase.getId() <= 0) {
					throw new BusinessException(BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				Long collectUserId = collectPassiveTask_DB.getCollectUserId();
				if (collectUserId.longValue() != collectTaskBase.getCollectUserId().longValue()) {
					throw new BusinessException(BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
//				CollectTaskBase ctb = (CollectTaskBase) collectTaskBaseDao.checkDataById(collectTaskBase);
//				if (ctb == null) {// 校验是否存在传入的任务ID
//					throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
//				}
				entity.setCollectPassiveTask(collectPassiveTask_DB);
				return entity;
			} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT.getCode())) {// 已提交
				throw new BusinessException(BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
			} else if (taskStatus_DB.equals(TASK_STATUS.FINISH.getCode())) {// 完成
				throw new BusinessException(BusinessExceptionEnum.TASK_ALREADY_FINISH);
			} else if (taskStatus_DB.equals(TASK_STATUS.NOT_FOUND.getCode())) {// 完成
				throw new BusinessException(BusinessExceptionEnum.TASK_ALREADY_NOTFOUND);
			} else {
				throw new BusinessException(BusinessExceptionEnum.LOGIC_ERROR);
			}
		}
	}

	@Component
	public class TaskSaveCheck implements CollectCore<CollectTaskSaveEntity, CollectTaskSaveEntity> {

		@Autowired
		public TaskSaveCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public CollectTaskSaveEntity execute(CollectTaskSaveEntity collectTaskSaveEntity) throws BusinessException {
			// 获得单一数据源的key
			CollectTaskBase collectTaskBase=collectTaskSaveEntity.getCollectTaskBase();
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			CollectPassiveTask collectPassiveTask_DB = (CollectPassiveTask) collectPassiveTaskDao.select(dataSourceKey, collectTaskBase.getPassiveId());
			if (collectPassiveTask_DB == null) {
				throw new BusinessException(BusinessExceptionEnum.TASK_NOT_EXIST);
			}
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			//领取后不再做原始任务结束时间校验
//			if(collectPassiveTask_DB.getAllotEndTime()!=null&&currentTime>collectPassiveTask_DB.getAllotEndTime()){
//				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
//			}
			if(collectPassiveTask_DB.getOwnerId()==null||!collectPassiveTask_DB.getOwnerId().equals(collectTaskBase.getOwnerId())){
				throw new BusinessException(BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
			}
			if(collectPassiveTask_DB.getAllotUserId()==null||!collectTaskBase.getCollectUserId().equals(collectPassiveTask_DB.getAllotUserId())){
				throw new BusinessException(
						BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
			}
			CollectTaskBase collectTaskBase_DB=(CollectTaskBase)collectTaskBaseDao.selectTaskDataForCollect(collectTaskBase);
			if (collectTaskBase_DB == null) {// 校验是否存在传入的任务ID
				throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
			}
			if(collectTaskBase_DB.getOwnerId()==null||!collectTaskBase_DB.getOwnerId().equals(collectTaskBase.getOwnerId())){
				throw new BusinessException(BusinessExceptionEnum.TASK_IS_NOT_ALLOW_FOR_OWNER);
			}
//			if(collectTaskBase_DB.getTaskClazzId()!=null&&collectTaskBase.getTaskClazzId()!=null
//					&&!collectTaskBase_DB.getTaskClazzId().equals(collectTaskBase.getTaskClazzId())){
//				throw new BusinessException(BusinessExceptionEnum.TASK_CLAZZ_IS_CHANGE);
//			}
			if(collectTaskBase_DB.getAllotUserId()==null||!collectTaskBase.getCollectUserId().equals(collectTaskBase_DB.getAllotUserId())){
				throw new BusinessException(
						BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
			}
			if(collectTaskBase_DB.getAllotEndTime()!=null&&currentTime>collectTaskBase_DB.getAllotEndTime()){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			if(!collectTaskBase_DB.getTaskStatus().equals(collectPassiveTask_DB.getTaskStatus())){
				throw new BusinessException(BusinessExceptionEnum.LOGIC_ERROR);
			}
			collectTaskBase.setTaskPackageId(collectTaskBase_DB.getTaskPackageId());
			//CollectTaskSaveEntity entity = new CollectTaskSaveEntity();
			
			Integer taskStatus_DB = collectPassiveTask_DB.getTaskStatus();
			collectTaskBase.setTaskStatus(taskStatus_DB);
			collectTaskSaveEntity.setCollectTaskBase(collectTaskBase);
			if (taskStatus_DB == null)
				taskStatus_DB = -1;
			if (taskStatus_DB.equals(TASK_STATUS.UNALLOT.getCode())) {// 未分配
				throw new BusinessException(BusinessExceptionEnum.TASK_MUST_RECEIVE);
			} else if (taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {// 已领取
				Long allotUserId = collectPassiveTask_DB.getAllotUserId();
				if (allotUserId.longValue() != collectTaskBase.getCollectUserId().longValue()) {// 如果实际采集用户不等于已分配的用户，抛出异常
					throw new BusinessException(BusinessExceptionEnum.ALLOT_USER_UNEQUAL);
				}
				if (collectTaskBase.getId() == null || collectTaskBase.getId() <= 0) {
					throw new BusinessException(BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				collectTaskSaveEntity.setCollectPassiveTask(collectPassiveTask_DB);
				return collectTaskSaveEntity;
			} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
				throw new BusinessException(BusinessExceptionEnum.TASK_MUST_RECEIVE);
			} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
				throw new BusinessException(BusinessExceptionEnum.TASK_FREEZE);
			} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())) {// 已保存
				if (collectTaskBase.getId() == null || collectTaskBase.getId() <= 0) {
					throw new BusinessException(BusinessExceptionEnum.TASK_MUST_HAVE_ID);
				}
				Long collectUserId = collectPassiveTask_DB.getCollectUserId();
				if (collectUserId.longValue() != collectTaskBase.getCollectUserId().longValue()) {
					throw new BusinessException(BusinessExceptionEnum.COLLECT_USER_UNEQUAL);
				}
//				CollectTaskBase ctb = (CollectTaskBase) collectTaskBaseDao.checkDataById(collectTaskBase);
//				if (ctb == null) {// 校验是否存在传入的任务ID
//					throw new BusinessException(BusinessExceptionEnum.TASK_BASE_ID_NOT_FOUND);
//				}
				collectTaskSaveEntity.setCollectPassiveTask(collectPassiveTask_DB);
				return collectTaskSaveEntity;
			} else if (taskStatus_DB.equals(TASK_STATUS.SUBMIT.getCode())) {// 已提交
				throw new BusinessException(BusinessExceptionEnum.TASK_ALREADY_SUBMIT);
			} else if (taskStatus_DB.equals(TASK_STATUS.FINISH.getCode())) {// 完成
				throw new BusinessException(BusinessExceptionEnum.TASK_ALREADY_FINISH);
			} else {
				throw new BusinessException(BusinessExceptionEnum.LOGIC_ERROR);
			}
		}
	}

	@Component
	public class TaskSave implements CollectCore<CollectTaskBase, CollectTaskSaveEntity> {
		@Autowired
		public TaskSave(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public CollectTaskBase execute(CollectTaskSaveEntity entity) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			CollectPassiveTask collectPassiveTask_DB = entity.getCollectPassiveTask();
			CollectTaskBase collectTaskBase = entity.getCollectTaskBase();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			collectTaskBase.setTaskSaveTime(currentTime);
			collectTaskBase.setUpdateTime(currentTime);
			
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
//			int days = new Integer(propertiesConfig.getProperty(CommonConstant.PROP_TASK_SAVE_MAX_TIME).toString());
//			collectTaskBase.setTaskSaveEndTime(DateUtils.addDays(new Date(currentTime * 1000), days).getTime() / 1000);
			// if
			// (collectPassiveTask.getTaskStatus().equals(TASK_STATUS.RECEIVE.getCode()))
			// {
			// collectPassiveTaskDao.updateTask(dataSourceKey, collectTaskBase,
			// TASK_STATUS.SAVE);
			// collectTaskBaseDao.update(collectTaskBase, collectPassiveTask,
			// TASK_STATUS.SAVE);
			// } else {
			Integer i=(Integer)collectPassiveTaskDao.updateTask(dataSourceKey, collectTaskBase, collectPassiveTask_DB.getTaskStatus().intValue(),TASK_STATUS.SAVE.getCode());
			if(i!=1){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
			}
			i=(Integer)collectTaskBaseDao.update(collectTaskBase, collectPassiveTask_DB.getTaskStatus().intValue(), TASK_STATUS.SAVE.getCode());
			if(i!=1){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
			}
			CollectBasePackage collectBasePackage=new CollectBasePackage();
			collectBasePackage.setCollectUserId(entity.getCollectTaskBase().getCollectUserId());
			collectBasePackage.setUpdateTime(currentTime);
			collectBasePackage.setId(collectTaskBase.getTaskPackageId());
			collectBasePackage.setPassivePackageId(collectPassiveTask_DB.getTaskPackageId());
			collectBasePackage.setOwnerId(collectTaskBase.getOwnerId());
			if(flag){
				collectBasePackage.setTaskPackagePay(money);
				collectBasePackage.setTaskPackageStatus(collectPassiveTask_DB.getTaskStatus());
				collectBasePackageDao.updatePackageStatusForCollectNew(collectBasePackage, TASK_STATUS.SAVE.getCode());
			}else{
				collectBasePackageDao.updatePackageStatusForCollect(collectBasePackage, TASK_STATUS.SAVE.getCode());
			}
			collectPassivePackageDao.updatePackageStatusForCollect(dataSourceKey, collectBasePackage, TASK_STATUS.SAVE.getCode());
			// 更新图片信息
			Map<Long,Long> tempMap=new HashMap<Long,Long>();
			List<CollectTaskImg> tempImgList=new ArrayList<CollectTaskImg>();
			if(entity.getCollectTaskImgList().size()>0){
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
			if(entity.getCollectTaskToken()!=null){//更新token BAse_ID
				collectTaskTokenDao.updateTaskTokenBaseId(entity.getCollectTaskToken(), collectTaskBase);
			}
			/**
			 * //校验任务包状态不做 浪费性能
			collectTaskBase.setTaskStatus(TASK_STATUS.SAVE.getCode());
			CheckPackageUpdateEntity check=(CheckPackageUpdateEntity)collectTaskBaseDao.checkPackageUpdate(collectTaskBase);
			if(check!=null&&check.getTaskPackageCount().equals(check.getTaskProcessCount())){
				CollectBasePackage collectBasePackage=new CollectBasePackage();
				collectBasePackage.setCollectUserId(entity.getCollectTaskBase().getCollectUserId());
				collectBasePackage.setUpdateTime(currentTime);
				collectBasePackage.setTaskPackageStatus(check.getStatus());
				collectBasePackage.setId(check.getId());
				collectBasePackageDao.updatePackageStatusForCollect(collectBasePackage, TASK_STATUS.SAVE.getCode());
				collectBasePackage.setPassivePackageId(check.getPassivePackageId());
				collectPassivePackageDao.updatePackageStatusForCollect(dataSourceKey, collectBasePackage, TASK_STATUS.SAVE.getCode());
			}
			 */
			entity.setIsNew(false);
			//额外信息更新
			List<Long> batchIdList=taskCollectUtilBusinessManager.getUpdateTaskExtraInfo().execute(entity);
			//作废无用的图片
			if(batchIdList.size()>0){
				collectTaskImageDao.batchUpdateToUnUse(collectTaskBase, batchIdList);
			}
			
			// }
			//查询返回结果-有效时间
			return (CollectTaskBase)collectTaskBaseDao.selectTaskDataForCollect(collectTaskBase);
		}
	}

	@Component
	public class TaskSubmit implements CollectCore<Object, CollectTaskSubmitEntity> {
		@Autowired
		public TaskSubmit(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public Object execute(CollectTaskSubmitEntity entity) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			CollectPassiveTask collectPassiveTask_DB = entity.getCollectPassiveTask();
			CollectTaskBase collectTaskBase = entity.getCollectTaskBase();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			collectTaskBase.setTaskSaveTime(currentTime);
			collectTaskBase.setUpdateTime(currentTime);
			int days = new Integer(propertiesConfig.getProperty(CommonConstant.PROP_TASK_SAVE_MAX_TIME).toString());
			collectTaskBase.setTaskSaveEndTime(DateUtils.addDays(new Date(currentTime * 1000), days).getTime() / 1000);
			Integer status=TASK_STATUS.SUBMIT.getCode();
			if(entity.isLost()){
				status=TASK_STATUS.NOT_FOUND.getCode();
			}
			// if
			// (collectPassiveTask.getTaskStatus().equals(TASK_STATUS.RECEIVE.getCode()))
			// {
			// collectPassiveTaskDao.updateTask(dataSourceKey, collectTaskBase,
			// TASK_STATUS.SUBMIT);
			// // collectTaskBaseDao.insert(collectTaskBase,
			// // TASK_STATUS.SUBMIT);
			// collectTaskBaseDao.update(collectTaskBase, collectPassiveTask,
			// TASK_STATUS.SUBMIT);
			// collectTaskBase.setId(DaoHelper.getPrimaryKey());
			// } else {
			Integer i=(Integer)collectPassiveTaskDao.updateTask(dataSourceKey, collectTaskBase,collectPassiveTask_DB.getTaskStatus().intValue(), status);
			if(i!=1){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
			}
			i=(Integer)collectTaskBaseDao.update(collectTaskBase, collectPassiveTask_DB.getTaskStatus().intValue(), status);
			if(i!=1){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
			}
			// }
			// 批量保存图片信息
			if (entity.getCollectTaskImgList().size()>0) {
				collectTaskImageDao.insert(collectTaskBase, entity.getCollectTaskImgList());
			}
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
			/** 
			 //校验任务包状态 性能并发有问题不做 采用在Service中调用任务包更新方法实现
			collectTaskBase.setTaskStatus(TASK_STATUS.SUBMIT.getCode());
			CheckPackageUpdateEntity check=(CheckPackageUpdateEntity)collectTaskBaseDao.checkPackageUpdate(collectTaskBase);
			if(check!=null&&check.getTaskPackageCount().equals(check.getTaskProcessCount())){
				CollectBasePackage collectBasePackage=new CollectBasePackage();
				collectBasePackage.setCollectUserId(entity.getCollectTaskBase().getCollectUserId());
				collectBasePackage.setUpdateTime(currentTime);
				collectBasePackage.setTaskPackageStatus(check.getStatus());
				collectBasePackage.setId(check.getId());
				collectBasePackageDao.updatePackageStatusForCollect(collectBasePackage, TASK_STATUS.SUBMIT.getCode());
				collectBasePackage.setPassivePackageId(check.getPassivePackageId());
				collectPassivePackageDao.updatePackageStatusForCollect(dataSourceKey, collectBasePackage,TASK_STATUS.SUBMIT.getCode());
				//预留给审核发送消息
				entity.setCollectBasePackage(collectBasePackage);
				//封装任务信息列表
				List<ResultEntity> entitys=syncTaskBusinessMananger.getPassiveTaskSendToAudit().execute(entity);
				for(ResultEntity _en:entitys){
					syncTaskBusinessMananger.getSendCollectInfoToAuditQueue().execute(_en);
				}
			}
			 */
			//额外信息
			taskCollectUtilBusinessManager.getSubmitTaskExtraInfo().execute(entity);
			return null;
		}
	}
	
	@Component
	public class TaskReleaseCheck implements CollectCore<List<CollectPassiveTask>, Object> {
		@Autowired
		public TaskReleaseCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}
        /**
         * 释放被动任务
	     * 只支持1任务包对1任务的被动任务
	     * 如需支持1对多的关系
	     * 需要重新定义整任务包释放的方法 
	     * 同时需要审核系统按照整任务包回传
         */
		@SuppressWarnings("unchecked")
		@Override
		public List<CollectPassiveTask> execute(Object obj) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			//获取所有需要释放的被动任务
			List<CollectPassiveTask> releases=(List<CollectPassiveTask>)collectPassiveTaskDao.checkTaskRelease(dataSourceKey, currentTime);
			return releases;
		}
		
	}
	
	@Component
	public class TaskRelease implements CollectCore<Object, CollectPassiveTask> {
		@Autowired
		public TaskRelease(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}
        /**
         * 释放被动任务
	     * 只支持1任务包对1任务的被动任务
	     * 如需支持1对多的关系
	     * 需要重新定义整任务包释放的方法 
	     * 同时需要审核系统按照整任务包回传
	     * 所有审核不通过->通过 必须由客户端通过申诉申请发起 审核端不要在无申诉的情况下直接做 否则会出现用户A的某个任务被释放后 正好已经被别的用户领取 但是领取后A又被任务通过 造成复杂问题
	     * 申诉的任务 会被锁定 也就是意味着不会被释放出来，也就不会造成重新领取后又马上通过的问题
	     * 上述问题也可以通过审核端调用采集端的接口来判定一条人物是否还具有申诉权利来实现 但是目前未实现 
	     * 暂时按上述描述约定
	     * 1.申诉必须由APP端发起
	     * 2.审核不能在没有通知的情况下 主动复审未通过的任务
	     * 3.申诉和任务释放时候，会采用任务ID方式来锁行 锁行后判断人物是否可以被释放或者申诉
	     * 释放:反查任务状态（FINISH）和审核结果（不为空）
	     * 申诉:反查任务状态（FINISH）和审核结果（不为空）在申诉冻结期之内
	     * 4.申诉后直接发送消息给审核的消息队列
	     * 审核通过->不通过可以由审核端重新发起
         */
		@Override
		public Object execute(CollectPassiveTask collectPassiveTask) throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			collectPassivePackageDao.lockPassivePackageById(dataSourceKey, collectPassiveTask.getTaskPackageId());
			collectPassiveTaskDao.lockTask(dataSourceKey, collectPassiveTask.getId());
			//反查任务包状态
			CollectPassiveTask collectPassiveDb= (CollectPassiveTask)collectPassiveTaskDao.select(dataSourceKey, collectPassiveTask.getId());
			if(collectPassiveDb.getTaskStatus()!=CommonConstant.TASK_STATUS.FINISH.getCode()||collectPassiveDb.getVerifyStatus()==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_CANNOT_RELEASE);
			}
			//构建原始任务包对象
			CollectPassivePackage collectPassivePackage=(CollectPassivePackage)collectPassivePackageDao.getCollectPassivePackageById(dataSourceKey, collectPassiveTask.getTaskPackageId());
			//构建虚拟的任务包
			CollectBasePackage collectBasePackage=new CollectBasePackage();
			Integer newStatus=CommonConstant.TASK_STATUS.ALLOT.getCode();
			collectBasePackage.setPassivePackageId(collectPassivePackage.getId());
		    collectBasePackage.setAllotUserId(collectPassiveTask.getCollectUserId());
		    collectBasePackage.setCollectUserId(collectPassiveTask.getCollectUserId());
		    collectBasePackage.setOwnerId(collectPassiveTask.getOwnerId());
//		    Long endTime=new Date(currentTime*1000+collectPassivePackage.getAllotMaintainTime()*60*60*1000).getTime()/1000;
		    Long endTime=collectPassivePackage.getAllotEndTime();
		    CollectTaskBase collectTaskBase=new CollectTaskBase();
		    
		    logger.info("任务包需要重新置为未分配状态");
	    	newStatus=CommonConstant.TASK_STATUS.UNALLOT.getCode();
	    	if(endTime!=null&&currentTime>endTime){
		    	//超过分配有效时间 直接置为未分配状态
	    		logger.info("超过分配有效时间 直接置为超时状态");
	    		newStatus=CommonConstant.TASK_STATUS.TIME_OUT.getCode();
		    	collectBasePackage.setAllotEndTime(null);
		    	collectTaskBase.setAllotEndTime(null);
//		    	//直接将所有的有效分配置为无效
//		    	collectTaskAllotUserDao.updateStatus(collectBasePackage, CommonConstant.ALLOT_USER_STATUS.INVALID.getCode());
		    }
		  //判定任务包是否需要重新分配
		    //Long id=(Long)collectTaskAllotUserDao.selectFirstAllotInfoByPid(collectBasePackage);
//		    if(id.equals(0L)){
////		    	logger.info("任务包需要重新置为未分配状态");
////		    	newStatus=CommonConstant.TASK_STATUS.UNALLOT.getCode();
//		    	collectBasePackage.setAllotEndTime(null);
//		    	collectTaskBase.setAllotEndTime(null);
//		    }else{
//		    	logger.info("任务包需要重新置为已分配状态");
//		    	newStatus=CommonConstant.TASK_STATUS.ALLOT.getCode();
//		    	collectBasePackage.setAllotEndTime(endTime);
//		    	collectTaskBase.setAllotEndTime(endTime);
//		    	if(currentTime>endTime){
//			    	//超过分配有效时间 直接置为未分配状态
//		    		logger.info("超过分配有效时间 直接置为未分配状态");
//		    		newStatus=CommonConstant.TASK_STATUS.UNALLOT.getCode();
//			    	collectBasePackage.setAllotEndTime(null);
//			    	collectTaskBase.setAllotEndTime(null);
//			    	//直接将所有的有效分配置为无效
//			    	collectTaskAllotUserDao.updateStatus(collectBasePackage, CommonConstant.ALLOT_USER_STATUS.INVALID.getCode());
//			    }
//		    }
			collectBasePackage.setUpdateTime(currentTime);
			//构建虚拟的任务
			collectTaskBase.setPassiveId(collectPassiveTask.getId());
			collectTaskBase.setUpdateTime(currentTime);
//			collectTaskBase.setCollectUserId(collectPassiveTask.getCollectUserId());
			collectTaskBase.setTaskStatus(newStatus);
			//更新原始任务包状态
			collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
			//更新原始任务状态
			collectPassiveTaskDao.updateTaskFromAudit(dataSourceKey, collectTaskBase, newStatus);
			
		    //设置任务包分配
//		    if(newStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())){
//		    	//重新插入任务包
//				collectPassivePackage.setCreateTime(currentTime);
//				collectPassivePackage.setUpdateTime(currentTime);
//				collectPassivePackage.setTaskPackageStatus(newStatus);
//				collectPassivePackage.setAllotUserId(collectPassiveTask.getCollectUserId());
//				collectPassivePackage.setCollectUserId(null);
//				collectPassivePackage.setAllotEndTime(endTime);
//				collectPassivePackage.setOwnerId(collectPassivePackage.getOwnerId());
//				collectBasePackageDao.insert(collectPassivePackage);
//			    Long newPid=DaoHelper.getPrimaryKey();
//				//重新插入任务
//				  collectPassiveDb.setTaskPackageId(newPid);
//				  collectPassiveDb.setCreateTime(currentTime);
//				  collectPassiveDb.setUpdateTime(currentTime);
//				  collectPassiveDb.setTaskStatus(newStatus);
//				  collectPassiveDb.setAllotEndTime(endTime);
//				  collectTaskBaseDao.insert(collectPassiveDb);
//		    }
		    
		    //其他分片任务包释放
//			 List<Integer> ds=new DataSourceHolder().getDsKey();
//				//可优化 多数据源时候才使用
//			 if(ds.size()>1&&newStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())){
//				 for(Integer i:ds){
//						collectTaskBaseDao.updateShardsTask(i, collectBasePackage, newStatus, TASK_STATUS.RECEIVE.getCode());
//						collectBasePackageDao.updateShardsPackage(i, collectBasePackage,newStatus,TASK_STATUS.RECEIVE.getCode());
//						//可能是审核通过->不通过 审核端直接发起的 需要重新在别的分片上释放
//						collectTaskBaseDao.updateShardsTask(i, collectBasePackage, newStatus, TASK_STATUS.FINISH.getCode());
//						collectBasePackageDao.updateShardsPackage(i, collectBasePackage, newStatus,TASK_STATUS.FINISH.getCode());
//				 }
//			 }
			return null;
		}
		
		
		
	}
	
	/**
	 * 任务包提交
	 * @author xuyaming
	 *
	 */
	@Component
	public class TaskPackageSubmit implements CollectCore<CollectTaskSubmitEntity, CollectTaskSubmitEntity> {

		@Autowired
		public TaskPackageSubmit(PassiveTaskBusinessManager CollectTaskSubmitEntity) {

		}
		@Override
		public CollectTaskSubmitEntity execute(CollectTaskSubmitEntity entity)
				throws BusinessException {
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			//entity.getCollectBasePackage().setTaskPackageStatus(TASK_STATUS.RECEIVE.getCode());
			entity.getCollectBasePackage().setUpdateTime(currentTime);
			//更新被动任务包表
			collectPassivePackageDao.updatePackageStatus(dataSourceKey,entity.getCollectBasePackage(), TASK_STATUS.SUBMIT.getCode());
			//跟新任务包表
			collectBasePackageDao.updatePackageStatus(entity.getCollectBasePackage(), 
					TASK_STATUS.SUBMIT.getCode());
			entity.getCollectBasePackage().setTaskPackageStatus(TASK_STATUS.SUBMIT.getCode());
//			//发消息到审核
//			List<ResultEntity> entitys=syncTaskBusinessMananger.getPassiveTaskSendToAudit().execute(entity);
//			for(ResultEntity _en:entitys){
//				syncTaskBusinessMananger.getSendCollectInfoJsonToAuditQueue().execute(_en);
//			}
			return entity;
		}
		
	}
	/**
	 * 任务包提交检查
	 * @author xuyaming
	 *
	 */
	@Component
	public class TaskPackageSubmitCheck implements CollectCore<CollectBasePackage, CollectBasePackage> {

		@Autowired
		public TaskPackageSubmitCheck(PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}
		@Override
		public CollectBasePackage execute(CollectBasePackage collectBasePackage)
				throws BusinessException {
			// 获得单一数据源的key
			Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
			Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
			CollectBasePackage collectBasePackageDb=(CollectBasePackage)collectBasePackageDao.getCollectBasePackageById(collectBasePackage.getId(), collectBasePackage.getAllotUserId(), collectBasePackage.getOwnerId());
			if(collectBasePackageDb==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_PKG_CANNOT_SUBMIT);
			}
			if(collectBasePackageDb.getAllotEndTime()!=null&&currentTime>collectBasePackageDb.getAllotEndTime()){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			collectBasePackage.setPassivePackageId(collectBasePackageDb.getPassivePackageId());
			CollectPassivePackage collectPassivePackage_DB = (CollectPassivePackage) collectPassivePackageDao.getCollectPassivePackageById(dataSourceKey, collectBasePackageDb.getPassivePackageId());
			if(collectPassivePackage_DB==null){
				throw new BusinessException(BusinessExceptionEnum.TASK_PKG_CANNOT_SUBMIT);
			}
			if(collectPassivePackage_DB.getAllotEndTime()!=null&&currentTime>collectPassivePackage_DB.getAllotEndTime()){
				throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_TIME_OUT);
			}
			//检测包下任务状态
			TaskPackageSubmitCheckEntity entity=(TaskPackageSubmitCheckEntity)collectTaskBaseDao.checkTaskPackage(collectBasePackage);
			if(entity==null||!entity.getCountAll().equals(entity.getCountSubmit())){
				throw new BusinessException(BusinessExceptionEnum.TASK_PKG_TASK_MUST_SUBMIT);
			}
			Integer taskStatus_DB = collectPassivePackage_DB.getTaskPackageStatus();
			// 任务状态是已保存或者已经领取
			if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())||taskStatus_DB.equals(TASK_STATUS.RECEIVE.getCode())) {
				if (collectPassivePackage_DB.getAllotUserId() != null && collectBasePackage.getCollectUserId()!=null
						&&collectPassivePackage_DB.getAllotUserId().equals(collectBasePackage.getCollectUserId())) {// 如果在任务分配用户表中存在，即表示可以领取
					collectBasePackage.setTaskPackageStatus(taskStatus_DB);
					return collectBasePackage;
				}else{
					throw new BusinessException(BusinessExceptionEnum.TASK_PKG_CANNOT_SUBMIT);
				}
			}
			throw new BusinessException(BusinessExceptionEnum.TASK_PKG_CANNOT_SUBMIT);
		}
		
	}
	//任务删除检测
	@Component
	public class TaskDeleteCheck implements
			CollectCore<CollectBasePackage, CollectBasePackage> {

		@Autowired
		public TaskDeleteCheck(
				PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public CollectBasePackage execute(
				CollectBasePackage collectBasePackage)
				throws BusinessException {
			// 判断任务是否已经保存过
			if (collectBasePackage.getId() != null) {
				//需要锁定记录 防止批量超时置位时候状态出现读脏问题
				collectBasePackageDao.lockBasePackageById(collectBasePackage);
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
				if (collectBasePackageDb.getPassivePackageId() == null) {
					throw new BusinessException(
							BusinessExceptionEnum.TASK_IS_NOT_PASSIVE);
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
					return collectBasePackageDb;
				} else if (taskStatus_DB.equals(TASK_STATUS.ALLOT.getCode())) {// 已分配
					throw new BusinessException(
							BusinessExceptionEnum.TASK_MUST_SAVE);
				} else if (taskStatus_DB.equals(TASK_STATUS.FREEZE.getCode())) {// 冻结
					throw new BusinessException(
							BusinessExceptionEnum.TASK_FREEZE);
				} else if (taskStatus_DB.equals(TASK_STATUS.SAVE.getCode())) {// 已保存
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
					/**
					 * 由于任务超时后还删除的话 会影响超时任务的自动释放 因此 禁止超时任务的清理
					 * 超时任务给用户的感觉就是自动删除了 这块需要产品和用户说明 因为被动任务必须要自动清理 不能总被占着
					 * 门址采集也是采用这种方式处理
					 * 可以增加列表显示所有已经超时的任务 这样用户不会产生觉得丢任务的问题
					 */
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
				PassiveTaskBusinessManager passiveTaskBusinessManager) {
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
				collectBasePackage.setCollectUserId(collectBasePackage.getAllotUserId());
				int count=0;
				//删除任务包
				count=(Integer)collectBasePackageDao.updatePassivePackageStatus(collectBasePackage, TASK_STATUS.DELETE.getCode());
				//删除任务
				collectTaskBaseDao.update(collectBasePackage.getId(),TASK_STATUS.DELETE.getCode(),currentTime,collectBasePackage.getCollectUserId(),collectBasePackage.getOwnerId());
				//释放被动任务包
				if(count==1){
					taskPackageUserRelease.execute(collectBasePackage);
				}
				//清理mongodb
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
	@Component
	public class TaskPackageUserRelease implements CollectCore<Object, CollectBasePackage> {
		@Autowired
		public TaskPackageUserRelease(
				PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public Object execute(CollectBasePackage collectBasePackage) throws BusinessException {
			try {
				Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
				Long currentTime=collectBasePackage.getUpdateTime();
				Integer newStatus=-1;
				CollectPassivePackage collectPassivePackageDb=(CollectPassivePackage)collectPassivePackageDao.
						getCollectPassivePackageById(dataSourceKey, collectBasePackage.getPassivePackageId());
				//必须在状态同步状态下才会释放任务 任务不在存在已经分配状态 全部是未分配状态 按任务释放参照这个
				if(collectPassivePackageDb!=null
						&&(collectPassivePackageDb.getTaskPackageStatus().equals(collectBasePackage.getTaskPackageStatus())
								&&!collectBasePackage.getTaskPackageStatus().equals(TASK_STATUS.TIME_OUT.getCode()))
						&&collectPassivePackageDb.getAllotUserId().equals(collectBasePackage.getAllotUserId())){
					 Long endTime=collectPassivePackageDb.getAllotEndTime();
					 newStatus=CommonConstant.TASK_STATUS.UNALLOT.getCode();
					 if(endTime!=null&&currentTime>endTime){
					    	//超过分配有效时间 直接置为超时状态
				    		logger.info("超过分配有效时间 直接置为未分配状态");
				    		newStatus=CommonConstant.TASK_STATUS.TIME_OUT.getCode();
				    		collectBasePackage.setAllotEndTime(null);
					  }
					 collectBasePackage.setUpdateTime(currentTime);
					 //更新原始任务包状态
					 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
					//更新原始任务包下任务状态
					 collectPassiveTaskDao.updateTaskStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
				}
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.TASK_RELEASE_ERROR);
			}
		}
	}
	
	@Component
	public class TaskPackageAutoRelease implements CollectCore<Object, CollectBasePackage> {
		@Autowired
		public TaskPackageAutoRelease(
				PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public Object execute(CollectBasePackage collectBasePackage) throws BusinessException {
			try {
				Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
				Long currentTime= (Long) collectPassiveTaskDao
						.getCurrentTime(dataSourceKey);
				Integer newStatus=-1;
				CollectPassivePackage collectPassivePackageDb=(CollectPassivePackage)collectPassivePackageDao.
						getCollectPassivePackageById(dataSourceKey, collectBasePackage.getPassivePackageId());
				//必须在状态同步状态下才会释放任务 任务不在存在已经分配状态 全部是未分配状态 按任务释放参照这个
				if(collectPassivePackageDb!=null
						&&collectBasePackage.getTaskPackageStatus().equals(TASK_STATUS.TIME_OUT.getCode())
						&&collectPassivePackageDb.getAllotUserId()!=null
						&&collectBasePackage.getAllotUserId()!=null
						&&collectPassivePackageDb.getAllotUserId().equals(collectBasePackage.getAllotUserId())){
					 Long endTime=collectPassivePackageDb.getAllotEndTime();
					 newStatus=CommonConstant.TASK_STATUS.UNALLOT.getCode();
					 collectBasePackage.setAllotEndTime(null);
					 if(endTime!=null&&currentTime>endTime){
					    	//超过分配有效时间 直接置为超时状态
				    		logger.info("超过分配有效时间 直接置为超时状态");
				    		newStatus=CommonConstant.TASK_STATUS.TIME_OUT.getCode();
					  }
					 collectBasePackage.setUpdateTime(currentTime);
					 //更新原始任务包状态
					 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
					//更新原始任务包下任务状态
					 collectPassiveTaskDao.updateTaskStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
					//清理mongodb
					List<CollectTaskBase> list=(List<CollectTaskBase>)collectTaskBaseDao.
								selectByPackage(collectBasePackage);
					for(CollectTaskBase base:list){
							taskCollectUtilBusinessManager.getDeleteWholeTaskExtra().execute(base);
					}
				}
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				logger.info("释放被动任务包ID["+collectBasePackage.getPassivePackageId()+"]出现异常,请手工处理");
				throw new BusinessException(BusinessExceptionEnum.TASK_RELEASE_ERROR);
			}
		}
	}
	//超时任务处理
	@Component
	public class TaskPackageTimeoutProceed implements CollectCore<Object, Object> {
		@Autowired
		public TaskPackageTimeoutProceed(
				PassiveTaskBusinessManager passiveTaskBusinessManager) {

		}

		@Override
		public Object execute(Object obj) throws BusinessException {
			try {
				Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
				Long currentTime = (Long) collectPassiveTaskDao
						.getCurrentTime(dataSourceKey);
				Long expireTime = currentTime-60;
				List<Integer> ds=new DataSourceHolder().getDsKey();
				for(Integer dsKey:ds){
					CollectTaskBase base=new CollectTaskBase();
					CollectBasePackage basePackage=new CollectBasePackage();
					base.setUpdateTime(currentTime);
					basePackage.setUpdateTime(currentTime);
					Integer aaa=(Integer)collectBasePackageDao.updateTimeoutPackage(dsKey, basePackage, expireTime);
					if(aaa>0){
						collectTaskBaseDao.updateTimeoutPackage(dsKey, base, expireTime);
						CollectPackageTimeoutBatch batch=new CollectPackageTimeoutBatch();
						batch.setDsKey(dsKey);
						batch.setUpdateTime(currentTime);
						collectPackageTimeoutBatchDao.insert(dataSourceKey,batch);
					}
					
				}
				
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.TASK_RELEASE_ERROR);
			}
		}
	}
	
	
	
	//超时任务查询
		@Component
		public class TaskPackageTimeoutQuery implements CollectCore<Map<CollectPackageTimeoutBatch,List<CollectBasePackage>>, Object> {
			@Autowired
			public TaskPackageTimeoutQuery(
					PassiveTaskBusinessManager passiveTaskBusinessManager) {

			}

			@SuppressWarnings("unchecked")
			@Override
			public Map<CollectPackageTimeoutBatch,List<CollectBasePackage>> execute(Object obj) throws BusinessException {
				try {
					Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
					Map<CollectPackageTimeoutBatch,List<CollectBasePackage>> result=new HashMap<CollectPackageTimeoutBatch,List<CollectBasePackage>>();
					List<CollectPackageTimeoutBatch> l=(List<CollectPackageTimeoutBatch>)collectPackageTimeoutBatchDao.select(dataSourceKey);
					for(CollectPackageTimeoutBatch batch:l){
						CollectBasePackage basePackage=new CollectBasePackage();
						basePackage.setUpdateTime(batch.getUpdateTime());
						List<CollectBasePackage> list=(List<CollectBasePackage>)collectBasePackageDao.selectTimeoutPackage(batch.getDsKey(), basePackage);
						result.put(batch, list);
					}
					return result;
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new BusinessException(BusinessExceptionEnum.TASK_RELEASE_ERROR);
				}
			}
		}
		
		@Component
		public class TaskPackageTimeoutClean implements CollectCore<Object, CollectPackageTimeoutBatch> {
			@Autowired
			public TaskPackageTimeoutClean(
					PassiveTaskBusinessManager passiveTaskBusinessManager) {

			}

			@Override
			public Object execute(CollectPackageTimeoutBatch batch) throws BusinessException {
				try {
					Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
					collectPackageTimeoutBatchDao.delete(dataSourceKey, batch);
					return null;
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
					throw new BusinessException(BusinessExceptionEnum.TASK_RELEASE_ERROR);
				}
			}
		}
		
		
	
}
