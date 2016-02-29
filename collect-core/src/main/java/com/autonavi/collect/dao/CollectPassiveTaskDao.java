package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.constant.CommonConstant;

//id bigint(32) pk 
//data_name varchar(100) 
//task_status int(2) 
//create_time bigint(32) 
//update_time bigint(32) 
//reward_id bigint(32) 
//allot_user_id bigint(32) 
//collect_user_id bigint(32) 
//collect_data_name varchar(100) 
//verify_data_name varchar(100) 
//poi varchar(50) 
//prename varchar(50) 
//allot_end_time bigint(32)
@Repository
public class CollectPassiveTaskDao {

	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.bean, resultType = CollectPassiveTask.class)
	public Object select(@SqlParameter("dataSourceKey") Integer dataSourceKey, @SqlParameter("id") Long id) {
		return "select id"
				+ ", data_name as dataName"
				+ ", collect_data_name as collectDataName"
				+ ", task_status as taskStatus"
				+ ", allot_end_time allotEndTime "
				+ ", allot_user_id as allotUserId"
				+ ", collect_user_id as collectUserId "
				+ ", task_package_id as taskPackageId "
				+ ", verify_status as verifyStatus "
				+ ", update_time as updateTime"
				+ ", release_Freeze_Time as releaseFreezeTime "
				+ ", owner_id as ownerId "
				+ ", task_clazz_id as taskClazzId "
				+ " from collect_passive_task where id=#{id}";
	}

	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	// 0 未分配 1已分配 2冻结 3已保存 4已提交 5完成
	public Object receiveTask(@SqlParameter("dataSourceKey") Integer dataSourceKey
			, @SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String sql = "update collect_passive_task set "
				+ "  task_status=#{newTaskStatus}"
				+ ", update_time=#{collectTaskBase.updateTime}"
				+ ", allot_user_id=#{collectTaskBase.allotUserId} "
				+ "where id=#{collectTaskBase.passiveId} and task_status=#{collectTaskBase.taskStatus}";
		return sql;
	}
	
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	// 0 未分配 1已分配 2冻结 3已保存 4已提交 5完成
	public Object receiveTasksByPackage(@SqlParameter("dataSourceKey") Integer dataSourceKey
			, @SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(collectBasePackage.getAllotEndTime()!=null){
			temp=", allot_end_time=#{collectBasePackage.allotEndTime} ";
		}
		String sql = "update collect_passive_task set "
				+ "  task_status=#{newTaskStatus}"
				+ temp
				+ ", update_time=#{collectBasePackage.updateTime}"
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ "where task_package_id=#{collectBasePackage.passivePackageId}";
		return sql;
	}
	
	
	
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	// 0 未分配 1已分配 2冻结 3已保存 4已提交 5完成
	public Object updateTask(@SqlParameter("dataSourceKey") Integer dataSourceKey
			, @SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("currentTaskStatus") Integer currentTaskStatus
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String sql = "update collect_passive_task set "
				+ "collect_data_name=#{collectTaskBase.collectDataName}"
				+ ", task_status=#{newTaskStatus}"
				+ ", update_time=#{collectTaskBase.updateTime}"
				+ ", collect_user_id=#{collectTaskBase.collectUserId} "
				+ "where id=#{collectTaskBase.passiveId}"
				+ " and task_status=#{currentTaskStatus}";
		return sql;
	}
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	// 0 未分配 1已分配 2冻结 3已保存 4已提交 5完成
	public Object lockTask(@SqlParameter("dataSourceKey") Integer dataSourceKey
			, @SqlParameter("id") Long id) {
		String sql = "update collect_passive_task set "
				+ " id=id "
				+ "where id=#{id}";
		return sql;
	}
	
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	// // 0 未分配 1已领取(待采集) 2冻结 3已保存(待提交) 4已提交(待审核) 5完成(已审核) 6已分配 7 未找到 8 超时
	public Object updateTaskFromAudit(@SqlParameter("dataSourceKey") Integer dataSourceKey
			, @SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(collectTaskBase.getVerifyDataName()!=null){
			temp=", verify_data_name=#{collectTaskBase.verifyDataName}";
		}
		String statusTemp="";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())
				||collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
				||collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
			statusTemp=", task_status=#{newTaskStatus}";
		}
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.UNALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			statusTemp=", task_status=#{newTaskStatus} ";
			temp=",allot_user_id=null,collect_user_id=null,verify_status=null,collect_data_name=null ";
		}
		String veifyStatus="";
		if(collectTaskBase.getVerifyStatus()!=null){
			veifyStatus=",verify_status=#{collectTaskBase.verifyStatus} ";
		}
		String sql = "update collect_passive_task set "
				+ " update_time=#{collectTaskBase.updateTime}"
				+ veifyStatus
				+ statusTemp
				+ temp
				+ " where id=#{collectTaskBase.passiveId} ";
//				+ " where id=#{collectTaskBase.passiveId} and allot_user_id=#{collectTaskBase.collectUserId}";
		return sql;
	}

	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object getCurrentTime(@SqlParameter("dataSourceKey") Integer dataSourceKey) {
		return "select UNIX_TIMESTAMP(now()) as currentTime";
	}
	
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectPassiveTask.class)
	public Object checkTaskRelease(@SqlParameter("dataSourceKey") Integer dataSourceKey,@SqlParameter("currentTime") Long currentTime){
		return "select t.id,t.TASK_PACKAGE_ID taskPackageId,t.collect_user_id collectUserId from collect_passive_task t where "
				+ " t.VERIFY_STATUS="+CommonConstant.TASK_VERIFY_STATUS.FAIL.getCode()+" "
						+ "and t.UPDATE_TIME<=${currentTime}-RELEASE_FREEZE_TIME*60*60 ";
	}
	
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectPassiveTask.class)
	public Object selectCollectPassiveTaskByPid(@SqlParameter("dataSourceKey") Integer dataSourceKey,@SqlParameter("packageId") Long packageId) {
		return "select id"
				+ ", data_name as dataName"
				+ ", collect_data_name as collectDataName"
				+ ", task_status as taskStatus"
				+ ", allot_user_id as allotUserId"
				+ ", collect_user_id as collectUserId "
				+ ", task_package_id as taskPackageId "
				+ ", verify_status as verifyStatus "
				+ ", update_time as updateTime"
				+ ", release_Freeze_Time as releaseFreezeTime "
				+ ", owner_id as ownerId "
				+ ", task_clazz_id taskClazzId "
				+ " from collect_passive_task where TASK_PACKAGE_ID=#{packageId}";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updateTaskStatusForAudit(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.UNALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=", collect_user_id=null "
					+ ", allot_user_id=null "
					+ ",VERIFY_STATUS=null "
			        + ",COLLECT_DATA_NAME=null ";
		}
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.FINISH.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
			temp=",VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} ";
		}
		return "update collect_passive_task "
				+ "set TASK_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ temp
				+ "where task_package_id=#{collectBasePackage.passivePackageId} ";
	}
}
