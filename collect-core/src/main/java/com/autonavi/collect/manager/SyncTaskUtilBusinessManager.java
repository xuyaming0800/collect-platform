package com.autonavi.collect.manager;

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
import autonavi.online.framework.util.bean.PropertyUtils;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.component.MongoDBUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_ATTR_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_IMG_STATUS;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectPassivePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.dao.CollectTaskImageDao;
import com.autonavi.collect.entity.CheckPackageUpdateEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.util.DataSourceHolder;
@Component
public class SyncTaskUtilBusinessManager {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Autowired
	private CollectTaskImageDao collectTaskImageDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;	
	@Autowired
	private CollectPassivePackageDao collectPassivePackageDao;
	@Autowired
	private CollectTaskAllotUserDao collectTaskAllotUserDao;
	@Autowired
	private MongoDBUtilComponent mongoDBUtilComponent;
	private PropertiesConfig propertiesConfig;
	
	public SyncTaskUtilBusinessManager()throws Exception{
		if (propertiesConfig == null)
			this.propertiesConfig = PropertiesConfigUtil
					.getPropertiesConfigInstance();
	}
	/**
	 * 释放或者完成任务
	 * 只支持1对1
	 * @param obj
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void taskRelease(CollectTaskBase obj)throws BusinessException{
		 try {
			    logger.info("审核回传处理开始 id=["+obj.getId()+"]");
			    //获取所有的步骤 attrList约定只取最外层
			    String tableName=propertiesConfig.getProperty(CommonConstant.COLLECT_TASK_EXTRA_TABLE).toString();
				Map<String,Object> queryMap=new HashMap<String,Object>();
				queryMap.put("baseId", obj.getId().toString());
				List<Object> queryResult=mongoDBUtilComponent.selectObjectMultiProjection(tableName, queryMap, new String[]{"money","attrList"}, false);
				String pay=null;
				List<Map<String,Object>> attrList=null;
				if(queryResult.size()>0){
					Object money=queryResult.get(0);
					pay=PropertyUtils.getValue(money,"money").toString();
					Object _obj=PropertyUtils.getValue(money, "attrList");
					attrList=(List<Map<String,Object>>)_obj;
				}
				List<Long> useBatchIdList=new ArrayList<Long>(); 
				List<Long> unUseBatchIdList=new ArrayList<Long>(); 
				for(Map<String,Object> m:attrList){
					String status=m.get("status").toString();
					String batchId=m.get("batchId").toString();
					if(status.equals(TASK_ATTR_STATUS.PASS.getCode())){
					   //通过
						useBatchIdList.add(Long.valueOf(batchId));   
					}else{
					   //不通过
						unUseBatchIdList.add(Long.valueOf(batchId));   
					}
					
				}
				if(useBatchIdList.size()>0){
					collectTaskImageDao.batchUpdateToVerifyStatus(obj, useBatchIdList,TASK_IMG_STATUS.FAIL.getCode(),TASK_IMG_STATUS.PASS.getCode());
				}
                if(unUseBatchIdList.size()>0){
                	collectTaskImageDao.batchUpdateToVerifyStatus(obj, unUseBatchIdList,TASK_IMG_STATUS.PASS.getCode(),TASK_IMG_STATUS.FAIL.getCode());
				}
			    Integer newStatus=null;
			    Integer newFailVerifyStatus=null;
			    Integer newPassVerifyStatus=null;
			    if(obj.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.PASS.getCode())||obj.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.FAIL.getCode())){
					 logger.info("同步终审结果");
					 newStatus=TASK_STATUS.FINISH.getCode();
					 newFailVerifyStatus=CommonConstant.TASK_VERIFY_STATUS.FAIL.getCode();
					 newPassVerifyStatus=CommonConstant.TASK_VERIFY_STATUS.PASS.getCode();
				 }
			    else if(obj.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.FIRST_PASS.getCode())||obj.getVerifyStatus().equals(CommonConstant.TASK_VERIFY_STATUS.FIRST_FAIL.getCode())){
					 logger.info("同步初审结果");
					 newStatus=TASK_STATUS.FIRST_AUDIT.getCode();
					 newFailVerifyStatus=CommonConstant.TASK_VERIFY_STATUS.FIRST_FAIL.getCode();
					 newPassVerifyStatus=CommonConstant.TASK_VERIFY_STATUS.FIRST_PASS.getCode();
					
				 }else{
					 logger.error("任务ID=["+obj.getId()+"]审核回传状态错误，回传状态["+obj.getVerifyStatus()+"]");
					 throw new BusinessException(BusinessExceptionEnum.TASK_AUDIT_STATUS_ERROR);
				 }
				Integer dataSourceKey = CommonConstant.getSingleDataSourceKey();
				//查询任务信息 已审核为准
				 CollectTaskBase base=(CollectTaskBase)collectTaskBaseDao.selectTaskDataForCollect(obj);
				 if(base==null){
					 logger.warn("TASK ["+obj.getId()+"]NOT FOUND!");
					 return;
				 }
				 if(base.getPassiveId()!=null){
					 collectPassiveTaskDao.lockTask(dataSourceKey, base.getPassiveId());
				 }
				 Long currentTime = (Long) collectPassiveTaskDao.getCurrentTime(dataSourceKey);
				 //审核状态置位
				 base.setCollectUserId(obj.getCollectUserId());
				 base.setVerifyStatus(obj.getVerifyStatus());
				 base.setVerifyDataName(obj.getVerifyDataName());
				 base.setUpdateTime(currentTime);
				 if(!base.getTaskStatus().equals(TASK_STATUS.SUBMIT.getCode())&&!base.getTaskStatus().equals(TASK_STATUS.FIRST_AUDIT.getCode())
						 &&!base.getTaskStatus().equals(TASK_STATUS.NOT_FOUND.getCode())&&!base.getTaskStatus().equals(TASK_STATUS.RE_AUDIT.getCode())){
					 logger.error("任务ID=["+obj.getId()+"]状态错误，状态为["+base.getTaskStatus()+"]");
					 throw new BusinessException(BusinessExceptionEnum.TASK_OR_PKG_STATUS_ERROR);
				 }
				 Integer tempStatus=newStatus;
				 if(base.getTaskStatus().equals(TASK_STATUS.NOT_FOUND.getCode())){
					 newStatus=TASK_STATUS.NOT_FOUND.getCode();
				 }
				 //更新原始任务表 任务表
				 collectTaskBaseDao.updateFromAudit(base, newStatus);
				 if(base.getPassiveId()!=null){
					 collectPassiveTaskDao.updateTaskFromAudit(dataSourceKey, base,newStatus);
				 }
				 newStatus=tempStatus;
				 base.setTaskStatus(newStatus);
				 //反查任务包状态
				 CheckPackageUpdateEntity check=(CheckPackageUpdateEntity)collectTaskBaseDao.checkPackageUpdate(base);
				 if(check!=null&&check.getTaskPackageCount().equals(check.getTaskProcessCount())){
					 //需要更新任务包（原始的和分配）的状态或者重新分配任务包
					 //获取任务包下的所有任务 检查状态
					 int notFound=0;
					 boolean isFail=false;
					 List<CollectTaskBase> l= (List<CollectTaskBase>)collectTaskBaseDao.selectByPackage(base);
					 for(CollectTaskBase b:l){
						 if(b.getTaskStatus().equals(CommonConstant.TASK_STATUS.NOT_FOUND.getCode())){
							 notFound++;
						 }
						 if(b.getVerifyStatus().equals(newFailVerifyStatus)){
							 isFail=true;
							 break;
						 }
					 }
					 CollectBasePackage collectBasePackage=(CollectBasePackage)collectBasePackageDao.getCollectBasePackageById(check.getId(), base.getCollectUserId(),base.getOwnerId());
					 //冻结情况 增加一张临时冻结表 如果冻结 那么久不再释放任务判断冻结状态 待实现
					 boolean isFreeze=false;
					 //实现冻结判定 待实现
					 collectBasePackage.setUpdateTime(currentTime);
					 Long endTime=new Date(currentTime*1000+collectBasePackage.getAllotMaintainTime()*60*60*1000).getTime()/1000;
					 collectBasePackage.setAllotEndTime(endTime);
					 if(pay!=null){
						 collectBasePackage.setTaskPackagePay(Double.valueOf(pay));
					 }else{
						 collectBasePackage.setTaskPackagePay(null);
					 }
					 
					 base.setAllotEndTime(endTime);
					 if(isFail){
						 if(!isFreeze){
							//整个包认为没有通过 释放任务
							 collectBasePackage.setTaskPackageVerifyStatus(newFailVerifyStatus);
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, newStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
							 }
							 /** 释放部分放入JOB做
							 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, CommonConstant.TASK_STATUS.ALLOT.getCode());
							 collectPassiveTaskDao.updateTaskFromAudit(dataSourceKey, base, CommonConstant.TASK_STATUS.ALLOT.getCode());
							
							 //重新插入任务包
							 collectBasePackage.setCreateTime(currentTime);
							 collectBasePackage.setUpdateTime(currentTime);
							 collectBasePackage.setTaskPackageStatus(CommonConstant.TASK_STATUS.ALLOT.getCode());
							 collectBasePackageDao.insert(collectBasePackage);
							 Long newPid=DaoHelper.getPrimaryKey();
							 //设置任务包分配
							 collectTaskAllotUserDao.updatePackageId(collectBasePackage, newPid);
							 //重新插入任务
							 for(CollectTaskBase b:l){
								 b.setAllotEndTime(endTime);
								 b.setCreateTime(currentTime);
								 b.setUpdateTime(currentTime);
								 b.setTaskPackageId(newPid);
								 b.setTaskStatus(CommonConstant.TASK_STATUS.ALLOT.getCode());
								 collectTaskBaseDao.insert(b);
							 }
							 //其他分片任务包释放
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>1){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.ALLOT.getCode(), TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.ALLOT.getCode(),TASK_STATUS.RECEIVE.getCode());
								 }
							 }
							  */
							 
							 
							 
						 }else{
							//整个包认为没有通过 冻结任务
							 collectBasePackage.setTaskPackageVerifyStatus(newFailVerifyStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, CommonConstant.TASK_STATUS.FREEZE.getCode()); 
							 }
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, newStatus);
							//其他分片任务包释放
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>1&&newStatus.equals(TASK_STATUS.FINISH.getCode())){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(), TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(),TASK_STATUS.RECEIVE.getCode());
								 }
							 }
						 }
						 
						 
					 }else if(notFound>0){
						 if(!isFreeze){
							 //存在未找到的 任务包设置为未找到状态 目前一对一
							 collectBasePackage.setTaskPackageVerifyStatus(newPassVerifyStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, CommonConstant.TASK_STATUS.NOT_FOUND.getCode());
							 }
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, CommonConstant.TASK_STATUS.NOT_FOUND.getCode());
							//其他分片任务包释放
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>1&&newStatus.equals(TASK_STATUS.FINISH.getCode())){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.NOT_FOUND.getCode(), TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.NOT_FOUND.getCode(),TASK_STATUS.RECEIVE.getCode());
								 }
							 }
						 }else{
							 collectBasePackage.setTaskPackageVerifyStatus(newPassVerifyStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, CommonConstant.TASK_STATUS.FREEZE.getCode());
							 }
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, CommonConstant.TASK_STATUS.NOT_FOUND.getCode());
							//其他分片任务包释放
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>0&&newStatus.equals(TASK_STATUS.FINISH.getCode())){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(), TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(),TASK_STATUS.RECEIVE.getCode());
										
								 }
							 }
						 }
						
						
					 }else{
						 if(!isFreeze){
							 //任务包设置为通过
							 collectBasePackage.setTaskPackageVerifyStatus(newPassVerifyStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, newStatus);
							 }
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, newStatus);
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>1&&newStatus.equals(TASK_STATUS.FINISH.getCode())){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, newStatus, TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, newStatus,TASK_STATUS.RECEIVE.getCode());
								 }
							 }
						 }else{
							 //任务包设置为通过
							 collectBasePackage.setTaskPackageVerifyStatus(newPassVerifyStatus);
							 if(base.getPassiveId()!=null){
								 collectPassivePackageDao.updatePackageStatusForAudit(dataSourceKey, collectBasePackage, CommonConstant.TASK_STATUS.FREEZE.getCode());
							 }
							 collectBasePackageDao.updatePackageStatusForAudit(collectBasePackage, newStatus);
							 List<Integer> ds=new DataSourceHolder().getDsKey();
								//可优化 多数据源时候才使用
							 if(ds.size()>1&&newStatus.equals(TASK_STATUS.FINISH.getCode())){
								 for(Integer i:ds){
										collectTaskBaseDao.updateShardsTask(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(), TASK_STATUS.RECEIVE.getCode());
										collectBasePackageDao.updateShardsPackage(i, collectBasePackage, TASK_STATUS.FREEZE.getCode(),TASK_STATUS.RECEIVE.getCode());
								 }
							 }
						 }
						
						 
					 }
				 }
				 logger.info("审核回传处理结束 id=["+obj.getId()+"]");
				 
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				throw new BusinessException(BusinessExceptionEnum.TASK_IN_QUEUE_ERROR);
			}
	}

}
