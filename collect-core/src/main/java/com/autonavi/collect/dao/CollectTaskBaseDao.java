package com.autonavi.collect.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select.Paging;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassiveTask;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.entity.CheckPackageUpdateEntity;
import com.autonavi.collect.entity.TaskPackageSubmitCheckEntity;

//id bigint(32) pk 
//passive_id bigint(32) 
//data_name varchar(100) 
//collect_data_name varchar(100) 
//verify_data_name varchar(100) 
//task_type int(2) 
//task_status int(2) 
//verify_status int(2) 
//task_sample_img varchar(50) 
//create_time bigint(32) 
//update_time bigint(32) 
//reward_id bigint(32) 
//allot_user_id bigint(32) 
//allot_end_time bigint(32) 
//collect_user_id bigint(32) 
//task_save_time bigint(32) 
//task_submit_time bigint(32) 
//device_info varchar(100) 
//task_save_end_time bigint(32)
@Component
public class CollectTaskBaseDao {

	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskBase.class)
	public Object checkDataById(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return "select id .user_name userName from collect_task_base where id=#{collectTaskBase.id}";
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.allotUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskBase.class)
	public Object selectTaskDataForAllot(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return "select id,passive_id passiveId,data_name dataName,allot_end_time allotEndTime,owner_id ownerId from collect_task_base where id=#{collectTaskBase.id}";
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	public Object selectTasksForAllot(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		return "select id,allot_user_id allotUserId,collect_user_id collectUserId,passive_id passiveId,task_package_id taskPackageId,data_name dataName,owner_id ownerId,"
				+ "allot_end_time allotEndTime,task_status taskStatus from collect_task_base where task_package_id=#{collectBasePackage.id}";
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CollectTaskBase.class)
	public Object selectTaskDataForCollect(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return "select id,allot_user_id allotUserId,collect_user_id collectUserId,passive_id passiveId,task_package_id taskPackageId,owner_id ownerId,"
				+ "data_name dataName,collect_data_name collectDataName,allot_end_time allotEndTime,task_save_time taskSaveTime,task_submit_time taskSubmitTime,task_status taskStatus,verify_status verifyStatus from collect_task_base where id=#{collectTaskBase.id}";
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = TaskPackageSubmitCheckEntity.class)
	public Object checkTaskPackage(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		String sql=" select countAll,countSubmit from "
				+ " (SELECT count(0) countSubmit,TASK_PACKAGE_ID FROM collect_task_base t where t.TASK_PACKAGE_ID=#{collectBasePackage.id} and t.owner_id=#{collectBasePackage.ownerId}"
				+ " and (task_status="+CommonConstant.TASK_STATUS.SUBMIT.getCode()+" or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+"))a "
				+ " inner join (SELECT count(0) countAll,TASK_PACKAGE_ID FROM collect_task_base t where t.TASK_PACKAGE_ID=#{collectBasePackage.id} and t.owner_id=#{collectBasePackage.ownerId})b "
				+ " on a.TASK_PACKAGE_ID=b.TASK_PACKAGE_ID ";
		return sql;
	}

//	@Author("ang.ji")
//	@Shard(indexColumn = "collectTaskBase.collectUserId", indexName = "USER_OWNER_ID_INDEX")
//	@Insert
//	public Object insert(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase, TASK_STATUS newStatus) {
//		String sql = "insert into collect_task_base (id," 
//				+ "passive_id," 
//				+ "data_name," 
//				+ "collect_data_name," 
//				+ "verify_data_name," 
//				+ "task_type," 
//				+ "task_status," 
//				+ "verify_status,"
//				+ "task_sample_img," 
//				+ "create_time," 
//				+ "update_time," 
//				+ "reward_id," 
//				+ "allot_user_id," 
//				+ "allot_end_time," 
//				+ "collect_user_id," + "task_save_time," 
//				+ "task_submit_time, device_info, task_save_end_time) " 
//				+ "values (#{AOF.snowflake}, " + collectTaskBase.getPassiveId() + ", '', '" + collectTaskBase.getCollectDataName()
//				+ "', null, 0, " + newStatus.getCode() + ", null, null, " 
//				+ collectTaskBase.getTaskSaveTime() + ", " + collectTaskBase.getTaskSaveTime() + ", null, null, null, "
//				+ collectTaskBase.getCollectUserId() + ", " + collectTaskBase.getTaskSaveTime() 
//				+ ", null, '" + collectTaskBase.getDeviceInfo() + "', " + collectTaskBase.getTaskSaveEndTime() + ")";
//		logger.info(sql);
//		return sql;
//	}

	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object update(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase, 
			@SqlParameter("currentTaskStatus") Integer currentTaskStatus
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String add="";
		String add1="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.NOT_FOUND.getCode())){
			add=", task_submit_time=#{collectTaskBase.taskSaveTime}";
		}
//		if(collectTaskBase.getTaskClazzId()!=null){
//			add1=", task_clazz_id=#{collectTaskBase.taskClazzId} ";
//		}
		String sql = "update collect_task_base set device_info=#{collectTaskBase.deviceInfo}"
				+ ", task_status=#{newTaskStatus}"
				+ ", collect_data_name=#{collectTaskBase.collectDataName}"
				+ ", update_time=#{collectTaskBase.updateTime}"
				+ ", collect_user_id=#{collectTaskBase.collectUserId}"
				+ ", task_save_time=#{collectTaskBase.taskSaveTime}"
				+ add
				+ ", device_info=#{collectTaskBase.deviceInfo}"
				+ add1
				+ " where id=#{collectTaskBase.id}" 
				+ " and (collect_user_id=#{collectTaskBase.collectUserId} or allot_user_id=#{collectTaskBase.collectUserId})"
				+ " and task_status=#{currentTaskStatus} ";		
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectUserId,ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object update(@SqlParameter("taskPackageId") Long taskPackageId, 
			 @SqlParameter("newTaskStatus") Integer newTaskStatus, 
			 @SqlParameter("currentTime") Long currentTime,
			 @SqlParameter("collectUserId") Long collectUserId,
			 @SqlParameter("ownerId") Long ownerId) {
		String sql = "update collect_task_base set "
				+ " task_status=#{newTaskStatus}"
				+ ", update_time=#{currentTime}"
				+ " where task_package_id=#{taskPackageId} " ;
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateFromAudit(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(collectTaskBase.getVerifyDataName()!=null){
			temp=", verify_data_name=#{collectTaskBase.verifyDataName}";
		}
		String statusTemp="";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())
				||collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
				||collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
			statusTemp=",task_status=#{newTaskStatus}";
		}
		String sql = "update collect_task_base set "
				+ " verify_status=#{collectTaskBase.verifyStatus}"
				+ statusTemp
				+ temp
				+ ", update_time=#{collectTaskBase.updateTime}"
				+ " where id=#{collectTaskBase.id}" 
				+ " and collect_user_id=#{collectTaskBase.collectUserId}";	
		return sql;
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.allotUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object receiveTask(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String sql = "update collect_task_base set "
				+ "task_status=#{newTaskStatus}"
				+ ", update_time=#{collectTaskBase.updateTime}"
				+ ", allot_user_id=#{collectTaskBase.allotUserId}"
				+ " where id=#{collectTaskBase.id}";
		return sql;
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object receiveTasksByPackage(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage
			, @SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String sql = "update collect_task_base set "
				+ "task_status=#{newTaskStatus}"
				+ ", update_time=#{collectBasePackage.updateTime}"
				+ ", allot_user_id=#{collectBasePackage.allotUserId}"
				+ " where task_package_id=#{collectBasePackage.id}";
		return sql;
	}
	@Author("ang.ji")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updateShardsTask(@SqlParameter("dataSourceKey") Integer dataSourceKey,@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage
			, @SqlParameter("newTaskStatus") Integer newTaskStatus,@SqlParameter("oldTaskStatus") Integer oldTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())){
			temp=",allot_end_time=#{collectBasePackage.allotEndTime}";
		}
		String sql = "update collect_task_base set "
				+ "task_status=#{newTaskStatus}"
				+ ", update_time=#{collectBasePackage.updateTime}"
				+ temp
				+ " where task_package_id in (select id from Collect_Base_Package where passive_package_id=#{collectBasePackage.passivePackageId} and "
				+ " task_package_status=#{oldTaskStatus} "
				+ " ) and task_status=#{oldTaskStatus} ";
		return sql;
	}

	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,paging = @Paging(skip = "start", size = "limit"),collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	public Object select(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String sql= "select id," 
				+ "passive_id passiveId," 
				+ "data_name dataName," 
				+ "collect_data_name collectDataName," 
				+ "verify_data_name verifyDataName," 
				+ "task_type taskType," 
				+ "task_status taskStatus," 
				+ "verify_status verifyStatus,"
				+ "create_time createTime," 
				+ "update_time updateTime," 
				+ "allot_user_id allotUserId," 
				+ "allot_end_time allotEndTime,owner_id ownerId," 
				+ "collect_user_id collectUserId," + "task_save_time taskSaveTime," 
				+ "task_submit_time taskSubmitTime, device_info deviceInfo, task_save_end_time taskSaveEndTime from collect_task_base "
				+ "where owner_id=#{collectTaskBase.ownerId} ";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			sql=sql+" and allot_user_id=#{collectTaskBase.collectUserId} and (task_status=#{collectTaskBase.taskStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and collect_user_id is null))";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			sql=sql+" and collect_user_id=#{collectTaskBase.collectUserId} and  (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			sql=sql+"and collect_user_id=#{collectTaskBase.collectUserId} and task_status=#{collectTaskBase.taskStatus}";
		}else{
			sql=sql+" and (collect_user_id=#{collectTaskBase.collectUserId} or allot_user_id=#{collectTaskBase.collectUserId}) and task_status=#{collectTaskBase.taskStatus}";
		}
		return sql;
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	public Object selectByPackage(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String temp="";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=" and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and verify_status is null";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			temp=" and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and verify_status is not null ";
		}else{
			temp=" and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")  ";
		}
		String sql= "select id," 
				+ "passive_id passiveId," 
				+ "data_name dataName," 
				+ "collect_data_name collectDataName," 
				+ "verify_data_name verifyDataName," 
				+ "task_type taskType," 
				+ "task_status taskStatus," 
				+ "verify_status verifyStatus,"
				+ "create_time createTime," 
				+ "update_time updateTime," 
				+ "allot_user_id allotUserId," 
				+ "allot_end_time allotEndTime,owner_id ownerId," 
				+ "collect_user_id collectUserId," + "task_save_time taskSaveTime," 
				+ "task_submit_time taskSubmitTime, device_info deviceInfo, task_save_end_time taskSaveEndTime,task_clazz_id taskClazzId from collect_task_base "
				+ "where task_package_id=#{collectTaskBase.taskPackageId} "+ temp +" ";
		sql=sql+" and (collect_user_id=#{collectTaskBase.collectUserId} or allot_user_id=#{collectTaskBase.collectUserId}) limit ${start},${limit}";
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,paging = @Paging(skip = "start", size = "limit"),collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	@Deprecated
	public Object selectByPackageStatus(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String sql= "select id," 
				+ "passive_id passiveId," 
				+ "data_name dataName," 
				+ "collect_data_name collectDataName," 
				+ "verify_data_name verifyDataName," 
				+ "task_type taskType," 
				+ "task_status taskStatus," 
				+ "verify_status verifyStatus,"
				+ "create_time createTime," 
				+ "update_time updateTime," 
				+ "allot_user_id allotUserId," 
				+ "allot_end_time allotEndTime," 
				+ "collect_user_id collectUserId," + "task_save_time taskSaveTime," 
				+ "task_submit_time taskSubmitTime, device_info deviceInfo, task_save_end_time taskSaveEndTime from collect_task_base "
				+ "where task_package_id=#{collectTaskBase.taskPackageId} ";
		sql=sql+" and (collect_user_id=#{collectTaskBase.collectUserId} or allot_user_id=#{collectTaskBase.collectUserId})";
		String temp="";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=" and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and verify_status is null";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			temp=" and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and verify_status is not null ";
		}else{
			temp=" and task_status=#{collectTaskBase.taskStatus} ";
		}
		return sql+temp;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	public Object selectByPackage(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		String sql= "select id," 
				+ "task_package_id taskPackageId,"
				+ "passive_id passiveId," 
				+ "data_name dataName," 
				+ "collect_data_name collectDataName," 
				+ "verify_data_name verifyDataName," 
				+ "task_type taskType," 
				+ "task_status taskStatus," 
				+ "verify_status verifyStatus,"
				+ "create_time createTime," 
				+ "update_time updateTime," 
				+ "allot_user_id allotUserId,owner_id ownerId," 
				+ "allot_end_time allotEndTime," 
				+ "collect_user_id collectUserId," + "task_save_time taskSaveTime," 
				+ "task_submit_time taskSubmitTime, device_info deviceInfo, task_save_end_time taskSaveEndTime from collect_task_base "
				+ "where task_package_id=#{collectTaskBase.taskPackageId} ";
		sql=sql+" and (collect_user_id=#{collectTaskBase.collectUserId} or allot_user_id=#{collectTaskBase.collectUserId}) and owner_id=#{collectTaskBase.ownerId}";
		return sql;
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectTaskBase.class)
	public Object selectByPackage(@SqlParameter("collectBasePackage") CollectBasePackage collectkBasePackage) {
		String sql= "select id," 
				+ "task_package_id taskPackageId,"
				+ "passive_id passiveId," 
				+ "data_name dataName," 
				+ "collect_data_name collectDataName," 
				+ "verify_data_name verifyDataName," 
				+ "task_type taskType," 
				+ "task_status taskStatus," 
				+ "verify_status verifyStatus,"
				+ "create_time createTime," 
				+ "update_time updateTime," 
				+ "allot_user_id allotUserId,owner_id ownerId," 
				+ "allot_end_time allotEndTime," 
				+ "collect_user_id collectUserId," + "task_save_time taskSaveTime," 
				+ "task_submit_time taskSubmitTime, device_info deviceInfo, task_save_end_time taskSaveEndTime from collect_task_base "
				+ "where task_package_id=#{collectBasePackage.id} ";
		sql=sql+" and (collect_user_id=#{collectBasePackage.collectUserId} or allot_user_id=#{collectBasePackage.collectUserId}) and owner_id=#{collectBasePackage.ownerId}";
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		String sql= "insert into  collect_task_base (id," 
				+ "task_package_id,"
				+ "passive_id," 
				+ "data_name," 
				+ "task_type," 
				+ "task_status," 
				+ "create_time," 
				+ "update_time," 
				+ "allot_end_time," 
				+ "COLLECT_DATA_NAME,"
				+ "allot_user_id,"
				+ "COLLECT_USER_ID,"
				+ "DEVICE_INFO,"
				+ "RELEASE_FREEZE_TIME,"
				+ "TASK_SAVE_TIME,"
				+ "TASK_CLAZZ_ID,"
				+ "TASK_SUBMIT_TIME,"
				+ "OWNER_ID)"
				+ " values(#{"+ ReservedWord.snowflake+"},#{collectTaskBase.taskPackageId},#{collectTaskBase.passiveId},"
				+ " #{collectTaskBase.dataName},#{collectTaskBase.taskType},#{collectTaskBase.taskStatus},"
				+ " #{collectTaskBase.createTime},#{collectTaskBase.updateTime},#{collectTaskBase.allotEndTime},"
				+ " #{collectTaskBase.collectDataName},#{collectTaskBase.allotUserId},#{collectTaskBase.collectUserId},"
				+ " #{collectTaskBase.deviceInfo},#{collectTaskBase.releaseFreezeTime},#{collectTaskBase.taskSaveTime},"
				+ " #{collectTaskBase.taskClazzId},#{collectTaskBase.taskSubmitTime},#{collectTaskBase.ownerId})";
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectPassiveTask.collectUserId,collectPassiveTask.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(@SqlParameter("collectPassiveTask") CollectPassiveTask collectPassiveTask) {
		String sql= "insert into  collect_task_base (id," 
				+ "task_package_id,"
				+ "passive_id," 
				+ "data_name," 
				+ "task_type," 
				+ "task_status," 
				+ "create_time," 
				+ "update_time,"
				+ "RELEASE_FREEZE_TIME,"
				+ "allot_end_time,OWNER_ID)" 
				+ " values(#{"+ ReservedWord.snowflake+"},#{collectPassiveTask.taskPackageId},#{collectPassiveTask.id},"
				+ " #{collectPassiveTask.dataName},0,#{collectPassiveTask.taskStatus},"
				+ " #{collectPassiveTask.createTime},#{collectPassiveTask.updateTime},"
				+ " #{collectPassiveTask.releaseFreezeTime},"
				+ " #{collectPassiveTask.allotEndTime},#{collectPassiveTask.ownerId})";
		return sql;
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectPassiveTask."+ReservedWord.index+".allotUserId,collectPassiveTask."+ReservedWord.index+".ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(@SqlParameter("collectPassiveTask") List<CollectPassiveTask> list) {
		String sql= "insert into  collect_task_base (id," 
				+ "task_package_id,"
				+ "passive_id," 
				+ "data_name," 
				+ "task_type," 
				+ "task_status," 
				+ "create_time," 
				+ "update_time,"
				+ "allot_user_id,"
				+ "RELEASE_FREEZE_TIME,"
				+ "allot_end_time,OWNER_ID,TASK_CLAZZ_ID)" 
				+ " values(#{"+ ReservedWord.snowflake+"},#{collectPassiveTask."+ReservedWord.index+".taskPackageId},#{collectPassiveTask."+ReservedWord.index+".id},"
				+ " #{collectPassiveTask."+ReservedWord.index+".dataName},0,#{collectPassiveTask."+ReservedWord.index+".taskStatus},"
				+ " #{collectPassiveTask."+ReservedWord.index+".createTime},#{collectPassiveTask."+ReservedWord.index+".updateTime},"
				+ " #{collectPassiveTask."+ReservedWord.index+".allotUserId},#{collectPassiveTask."+ReservedWord.index+".releaseFreezeTime},"
				+ " #{collectPassiveTask."+ReservedWord.index+".allotEndTime},#{collectPassiveTask."+ReservedWord.index+".ownerId},#{collectPassiveTask."+ReservedWord.index+".taskClazzId})";
		return sql;
	}
	
	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.collectUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CheckPackageUpdateEntity.class)
	public Object checkPackageUpdate(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase){
		return " select t.task_package_count taskPackageCount,s.c taskProcessCount,t.ID id,t.owner_id ownerId,"
				+ " t.PASSIVE_PACKAGE_ID passivePackageId,t.task_package_status status from collect_base_package t "
				+ "inner join (select count(0) c, task_package_id from collect_task_base where "
				+ "task_package_id=(select task_package_id from collect_task_base where id=#{collectTaskBase.id}) and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+" "
				+ " ) "
				+ ") s on t.ID=s.task_package_id";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updateTimeoutPackage(
			@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase,@SqlParameter("expireTime") Long expireTime) {
		return "update  Collect_task_base set update_time=#{collectTaskBase.updateTime} "
				+ " ,task_status= "+TASK_STATUS.TIME_OUT.getCode()+" "
				+ " where ( task_status="+TASK_STATUS.SAVE.getCode()+" or "
				+ " task_status="+TASK_STATUS.ALLOT.getCode()+" ) "
				+ " and allot_End_Time<#{expireTime}";
	}
	
}
