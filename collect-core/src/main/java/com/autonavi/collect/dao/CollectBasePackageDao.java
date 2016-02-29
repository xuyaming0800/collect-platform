package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

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
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.entity.CollectBasePackageEntity;
import com.autonavi.collect.entity.CollectToAuditEntity;
import com.autonavi.collect.entity.OriginCoordinateEntity;

@Repository
public class CollectBasePackageDao {
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object lockBasePackageById(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		return "update Collect_Base_Package set id=id where id=#{collectBasePackage.id}";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "allotUserId,ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CollectBasePackage.class)
	
	public Object getCollectBasePackageById(
			@SqlParameter("id") Long id,@SqlParameter("allotUserId") Long allotUserId,@SqlParameter("ownerId") Long ownerId) {
		return "select id,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,CREATE_TIME createTime,UPDATE_TIME updateTime,"
				+ "ALLOT_END_TIME allotEndTime,verify_Maintain_Time verifyMaintainTime,allot_Maintain_Time allotMaintainTime,"
				+ "ALLOT_USER_ID allotUserId,COLLECT_USER_ID collectUserId,verify_Freeze_Time verifyFreezeTime,"
				+ "TASK_PACKAGE_CATE taskPackageCate,TASK_PACKAGE_VERIFY_STATUS taskPackageVerifyStatus,TASK_CLAZZ_ID taskClazzId,OWNER_ID ownerId "
				+ " from Collect_Base_Package where id=#{id}";
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "allotUserId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.bean, resultType = CollectBasePackage.class)
	@Deprecated
	public Object getCollectBasePackage(
			@SqlParameter("id") Long id,@SqlParameter("allotUserId") Long allotUserId) {
		return "select id,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_TYPE taskPackageType,CREATE_TIME createTime,UPDATE_TIME updateTime,"
				+ "ALLOT_END_TIME allotEndTime,"
				+ "ALLOT_USER_ID allotUserId,COLLECT_USER_ID collectUserId,"
				+ "TASK_PACKAGE_CATE taskPackageCate from Collect_Base_Package where id=#{id}";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectPassivePackage.allotUserId,collectPassivePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(
			@SqlParameter("collectPassivePackage") CollectPassivePackage collectPassivePackage) {
		return "insert into Collect_Base_Package(id,PASSIVE_PACKAGE_ID,TASK_PACKAGE_NAME,"
				+ "TASK_PACKAGE_DESC,TASK_PACKAGE_COUNT,TASK_PACKAGE_PAY,"
				+ "TASK_PACKAGE_STATUS,TASK_PACKAGE_TYPE,CREATE_TIME,UPDATE_TIME,"
				+ "ALLOT_END_TIME,verify_Maintain_Time,allot_Maintain_Time,verify_Freeze_Time,TASK_PACKAGE_CATE,allot_user_id,OWNER_ID,TASK_CLAZZ_ID)"
				+ " values(#{" + ReservedWord.snowflake
				+ "},#{collectPassivePackage.id},#{collectPassivePackage.taskPackageName},#{collectPassivePackage.taskPackageDesc},"
				+ " #{collectPassivePackage.taskPackageCount},#{collectPassivePackage.taskPackagePay},#{collectPassivePackage.taskPackageStatus},#{collectPassivePackage.taskPackageType},"
				+ " #{collectPassivePackage.createTime},#{collectPassivePackage.updateTime},#{collectPassivePackage.allotEndTime},#{collectPassivePackage.verifyMaintainTime},"
				+ " #{collectPassivePackage.allotMaintainTime},#{collectPassivePackage.verifyFreezeTime},#{collectPassivePackage.taskPackageCate},#{collectPassivePackage.allotUserId},"
				+ " #{collectPassivePackage.ownerId},#{collectPassivePackage.taskClazzId})";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Insert
	public Object insert(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		return "insert into Collect_Base_Package(id,PASSIVE_PACKAGE_ID,TASK_PACKAGE_NAME,"
				+ "TASK_PACKAGE_DESC,TASK_PACKAGE_COUNT,TASK_PACKAGE_PAY,"
				+ "TASK_PACKAGE_STATUS,TASK_PACKAGE_TYPE,CREATE_TIME,UPDATE_TIME,"
				+ "ALLOT_END_TIME,verify_Maintain_Time,allot_Maintain_Time,verify_Freeze_Time,TASK_PACKAGE_CATE,allot_user_id,collect_user_id,task_clazz_id,OWNER_ID,submit_time)"
				+ " values(#{" + ReservedWord.snowflake
				+ "},#{collectBasePackage.passivePackageId},#{collectBasePackage.taskPackageName},#{collectBasePackage.taskPackageDesc},"
				+ " #{collectBasePackage.taskPackageCount},#{collectBasePackage.taskPackagePay},#{collectBasePackage.taskPackageStatus},#{collectBasePackage.taskPackageType},"
				+ " #{collectBasePackage.createTime},#{collectBasePackage.updateTime},#{collectBasePackage.allotEndTime},#{collectBasePackage.verifyMaintainTime},"
				+ " #{collectBasePackage.allotMaintainTime},#{collectBasePackage.verifyFreezeTime},#{collectBasePackage.taskPackageCate},#{collectBasePackage.allotUserId},"
				+ " #{collectBasePackage.collectUserId},#{collectBasePackage.taskClazzId},#{collectBasePackage.ownerId},#{collectBasePackage.submitTime})";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=",collect_user_id=#{collectBasePackage.collectUserId} "
					+ ",submit_time=#{collectBasePackage.updateTime} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ temp
				+ " where id=#{collectBasePackage.id} "
				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateInitiativePackageStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=",collect_user_id=#{collectBasePackage.collectUserId} "
					+ ", submit_time=#{collectBasePackage.updateTime} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ ", task_clazz_id=#{collectBasePackage.taskClazzId} "
				+ ", task_package_name=#{collectBasePackage.taskPackageName} "
				+ ", task_package_pay=#{collectBasePackage.taskPackagePay} "
				+ temp
				+ " where id=#{collectBasePackage.id} "
				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateInitiativePackageStatusNew(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=",collect_user_id=#{collectBasePackage.collectUserId} "
					+ ", submit_time=#{collectBasePackage.updateTime} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ ", task_clazz_id=#{collectBasePackage.taskClazzId} "
				+ ", task_package_name=#{collectBasePackage.taskPackageName} "
				+ temp
				+ " where id=#{collectBasePackage.id} "
				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePassivePackageStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=",collect_user_id=#{collectBasePackage.collectUserId} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ temp
				+ " where id=#{collectBasePackage.id} "
				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.allotUserId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	@Deprecated
	public Object updatePackageTaskCount(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("taskCount") Integer taskCount) {
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_COUNT=#{taskCount} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ " where id=#{collectBasePackage.id} "
				+ " and TASK_PACKAGE_COUNT=#{collectBasePackage.taskPackageCount}";
		return sql;
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updateShardsPackage(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus,@SqlParameter("oldTaskStatus") Integer oldTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())){
			temp=",allot_end_time=#{collectBasePackage.allotEndTime} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ temp
				+ " where passive_package_id=#{collectBasePackage.passivePackageId} "
				+ " and TASK_PACKAGE_STATUS=#{oldTaskStatus} ";
		return sql;
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	@Deprecated
	public Object updateShardsPackageTaskCount(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskCount") Integer newTaskCount,@SqlParameter("oldTaskCount") Integer oldTaskCount) {
		String temp="";
		if(newTaskCount>CommonConstant.PACKAGE_EMPTY_TASK_COUNT){
			temp=",allot_end_time=#{collectBasePackage.allotEndTime} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_COUNT=#{newTaskCount} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ temp
				+ " where passive_package_id=#{collectBasePackage.passivePackageId} "
				+ " and TASK_PACKAGE_COUNT=#{oldTaskCount}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageName(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage
			){
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_NAME=#{collectBasePackage.taskPackageName} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", TASK_CLAZZ_ID=#{collectBasePackage.taskClazzId} "
//				+ ", task_Package_Pay=#{collectBasePackage.taskPackagePay} "
				+ ", TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} "
				+ " where id=#{collectBasePackage.id} ";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageNameNew(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage
			){
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_NAME=#{collectBasePackage.taskPackageName} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", TASK_CLAZZ_ID=#{collectBasePackage.taskClazzId} "
				+ ", task_Package_Pay=task_Package_Pay+#{collectBasePackage.taskPackagePay} "
				+ ", TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} "
				+ " where id=#{collectBasePackage.id} ";
		return sql;
	} 
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageStatusForCollect(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", collect_user_id=#{collectBasePackage.collectUserId} "
				+ " where id=#{collectBasePackage.id} ";
//				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageStatusForCollectNew(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		
		String temp="";
		if(collectBasePackage.getTaskPackagePay()!=null){
			if(collectBasePackage.getTaskPackageStatus().equals(TASK_STATUS.RECEIVE.getCode())){
				temp=",TASK_PACKAGE_PAY=#{collectBasePackage.taskPackagePay} ";
			}else if(collectBasePackage.getTaskPackageStatus().equals(TASK_STATUS.SAVE.getCode())){
				temp=",TASK_PACKAGE_PAY=TASK_PACKAGE_PAY+#{collectBasePackage.taskPackagePay} ";
			}
		}
		
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", collect_user_id=#{collectBasePackage.collectUserId} "
				+ temp
				+ " where id=#{collectBasePackage.id} ";
//				+ " and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
		return sql;
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageStatusForAudit(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(collectBasePackage.getTaskPackagePay()!=null){
			temp=",TASK_PACKAGE_PAY=#{collectBasePackage.taskPackagePay} ";
		}
		String sql= "update Collect_Base_Package set "
				+ "TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ",TASK_PACKAGE_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ temp
				+ " where id=#{collectBasePackage.id} ";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectBasePackageEntity.class)
	public Object getUserPassiveCollectBasePackageByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}
		else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
                    + " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId}) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.owner_id=#{collectBasePackage.ownerId} "
//					+ " group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select a.submit_time submitTime,a.id ID,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,TASK_PACKAGE_CATE taskPackageCate,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,a.CREATE_TIME createTime,a.UPDATE_TIME updateTime,"
				+ "a.ALLOT_END_TIME allotEndTime,TASK_PACKAGE_VERIFY_STATUS taskPackageVerifyStatus,b.TASK_SAMPLE_IMG imgUrl,b.ORIGINAL_X x,b.ORIGINAL_Y y, "
				+ "a.TASK_CLAZZ_ID collectClassId "
				+ " from Collect_Base_Package a "
				+ " inner join "
				+ " collect_base_original_coordinate b on a.PASSIVE_PACKAGE_ID is not null and a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID and b.COORDINATE_STATUS=0 and a.PASSIVE_PACKAGE_ID is not null  "
				+  temp +" order by a.id desc "
				+ " limit ${start},${limit}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.column, resultType = Long.class)
	public Object getUserPassiveCollectBasePackageCountByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}  )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId}) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.owner_id=#{collectBasePackage.ownerId} "
//					+ " group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select count(0) count "
				+ "from Collect_Base_Package a "
				+ " inner join "
				+ " collect_base_original_coordinate b on a.PASSIVE_PACKAGE_ID is not null and a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID and b.COORDINATE_STATUS=0 and a.PASSIVE_PACKAGE_ID is not null  "
				+  temp +" ";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectBasePackageEntity.class)
	public Object getUserInitiativeCollectBasePackageByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
                    + " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select a.submit_time submitTime,a.id ID,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,TASK_PACKAGE_CATE taskPackageCate,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,a.CREATE_TIME createTime,a.UPDATE_TIME updateTime,"
				+ "a.ALLOT_END_TIME allotEndTime,TASK_PACKAGE_VERIFY_STATUS taskPackageVerifyStatus,  "
				+ "a.TASK_CLAZZ_ID collectClassId "
				+ "from Collect_Base_Package a "
//				+ " inner join collect_task_clazz d on a.task_clazz_id=d.id and a.task_clazz_id is not null and a.PASSIVE_PACKAGE_ID is null "
				+  temp +" and a.PASSIVE_PACKAGE_ID is null order by a.id desc "
				+ " limit ${start},${limit}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.column, resultType = Long.class)
	public Object getUserInitiativeCollectBasePackageCountByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
                    + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select count(0) as counts "
				+ "from Collect_Base_Package a "
//				+ " inner join collect_task_clazz d on a.task_clazz_id=d.id and a.task_clazz_id is not null and a.PASSIVE_PACKAGE_ID is null "
				+  temp +" and a.PASSIVE_PACKAGE_ID is null order by a.id desc ";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectBasePackageEntity.class)
	public Object getUserCollectBasePackageByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
                    + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select a.submit_time submitTime,a.id ID,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,TASK_PACKAGE_CATE taskPackageCate,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,a.CREATE_TIME createTime,a.UPDATE_TIME updateTime,"
				+ "a.ALLOT_END_TIME allotEndTime,TASK_PACKAGE_VERIFY_STATUS taskPackageVerifyStatus,b.TASK_SAMPLE_IMG imgUrl,b.ORIGINAL_X x,b.ORIGINAL_Y y, "
				+ "a.TASK_CLAZZ_ID collectClassId "
				+ " from Collect_Base_Package a "
				+ " left join "
				+ " collect_base_original_coordinate b on a.PASSIVE_PACKAGE_ID is not null and a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID and b.COORDINATE_STATUS=0 "
				+ temp 
				+ " limit ${start},${limit}";
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.column, resultType = Long.class)
	public Object getUserCollectBasePackageCountByStatus(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		String temp="";
		if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			//temp=" and c.allot_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" and c.collect_user_id is null)) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.allot_user_id=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " and t.collect_user_id is null ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and a.collect_user_id is null and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
			        + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
			
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and (task_status=#{collectBasePackage.taskPackageStatus} or (task_status="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+")) ";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and (t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " or (t.TASK_STATUS= "
//					+ CommonConstant.TASK_STATUS.TIME_OUT.getCode() 
//					+ " ) and t.owner_id=#{collectBasePackage.ownerId} ) group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.owner_id=#{collectBasePackage.ownerId} "
//					+ " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} or a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.TIME_OUT.getCode()+" )";
                    + " and (a.TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus} )";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
//			temp="inner join "
//					+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//					+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//					+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			temp=" where "
					+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else if(collectBasePackage.getTaskPackageStatus().equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())){
			//temp=" and c.collect_user_id=#{collectBasePackage.collectUserId} and task_status=#{collectBasePackage.taskPackageStatus} and task_package_status=#{collectBasePackage.taskPackageStatus}";
			if(collectBasePackage.getTaskPackageVerifyStatus()==null){
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.owner_id=#{collectBasePackage.ownerId} ";
			}else{
//				temp="inner join "
//						+ "(select t.TASK_PACKAGE_ID from collect_task_base t where t.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and t.TASK_STATUS=#{collectBasePackage.taskPackageStatus} and t.VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} "
//						+ " and t.owner_id=#{collectBasePackage.ownerId} group by t.TASK_PACKAGE_ID) c "
//						+ " on a.id=c.TASK_PACKAGE_ID and a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.task_package_status=#{collectBasePackage.taskPackageStatus} and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
				temp=" where "
						+ " a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and (a.task_package_status=#{collectBasePackage.taskPackageStatus} or a.task_package_status="+TASK_STATUS.RE_AUDIT.getCode()+") and a.task_package_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
			}
		}else{
			temp="";
		}
		String sql= "select count(0) counts "
				+ "from Collect_Base_Package a "
				+ " left join "
				+ " collect_base_original_coordinate b on a.PASSIVE_PACKAGE_ID is not null and a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID and b.COORDINATE_STATUS=0 "
				+ temp;
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectBasePackage.collectUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,collectionType = CollectionType.column, resultType = Long.class)
	public Object getUserCollectBasePackageVerifyCount(
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("verifyStatus") Integer verifyStatus,Boolean isPassive) {
        String sql="";
		if(isPassive==null){
        	sql= "select count(1) "
    				+ "from Collect_Base_Package a "
    				+ " where a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and a.TASK_PACKAGE_VERIFY_STATUS=#{verifyStatus} and a.owner_id=#{collectBasePackage.ownerId} ";
        }else if(isPassive){
        	 sql= "select count(1) "
     				+ "from Collect_Base_Package a "
     				+ " where a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and a.TASK_PACKAGE_VERIFY_STATUS=#{verifyStatus} and a.PASSIVE_PACKAGE_ID is not null and a.owner_id=#{collectBasePackage.ownerId} ";
        }else{
        	 sql= "select count(1) "
     				+ "from Collect_Base_Package a "
     				+ " where a.ALLOT_USER_ID=#{collectBasePackage.collectUserId} and a.COLLECT_USER_ID=#{collectBasePackage.collectUserId} and a.TASK_PACKAGE_VERIFY_STATUS=#{verifyStatus} and a.PASSIVE_PACKAGE_ID is null and a.owner_id=#{collectBasePackage.ownerId} ";
        }
		return sql;
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectTaskBase.collectUserId", indexName = "USER_OWNER_ID_INDEX")
	@Select(queryCount=true,paging = @Paging(skip = "start", size = "limit"),collectionType = CollectionType.beanList, resultType = CollectBasePackageEntity.class)
	@Deprecated
	public Object getUserCollectBasePackageByStatus(
			@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase,@SqlParameter("start") Integer start,@SqlParameter("limit") Integer limit) {
		String sql= "select a.id ID,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,TASK_PACKAGE_CATE taskPackageCate,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,count(c.TASK_PAY) taskPackageCount,sum(c.TASK_PAY) taskPackagePay,"
				+ "c.task_status taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,a.CREATE_TIME createTime,a.UPDATE_TIME updateTime,"
				+ "a.ALLOT_END_TIME allotEndTime,c.verify_status taskPackageVerifyStatus,b.TASK_SAMPLE_IMG imgUrl,b.ORIGINAL_X x,b.ORIGINAL_Y y "
				+ "from Collect_Base_Package a inner join "
				+ " collect_task_base c on a.id=c.task_package_id inner join "
				+ " collect_base_original_coordinate b on a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID where b.COORDINATE_STATUS=0 and ";
		if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.RECEIVE.getCode())){
			sql=sql+"a.allot_user_id=#{collectTaskBase.collectUserId} and (task_status=#{collectTaskBase.taskStatus}) ";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SAVE.getCode())){
			sql=sql+"a.collect_user_id=#{collectTaskBase.collectUserId} and  (task_status=#{collectTaskBase.taskStatus}) ";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			sql=sql+"a.collect_user_id=#{collectTaskBase.collectUserId} and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and c.verify_status is null";
		}else if(collectTaskBase.getTaskStatus().equals(CommonConstant.TASK_STATUS.FINISH.getCode())){
			sql=sql+"a.collect_user_id=#{collectTaskBase.collectUserId} and (task_status=#{collectTaskBase.taskStatus} or task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode()+") and c.verify_status="+CommonConstant.TASK_VERIFY_STATUS.PASS.getCode() +" ";
		}else{
			sql=sql+" (a.collect_user_id=#{collectTaskBase.collectUserId} or a.allot_user_id=#{collectTaskBase.collectUserId}) and task_status=#{collectTaskBase.taskStatus}";
		}
		sql=sql+" group by c.task_package_id ";
		return sql;
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "collectUserId,ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectToAuditEntity.class)
	
	public Object selectCollectInfoForAudit(@SqlParameter("packageId") Long packageId,@SqlParameter("collectUserId") Long collectUserId,@SqlParameter("ownerId") Long ownerId){
		return "select a.id pid,a.TASK_PACKAGE_NAME pName,a.TASK_PACKAGE_DESC pDesc,a.TASK_PACKAGE_COUNT pCount,a.TASK_PACKAGE_PAY pay,"
				+ "b.id bid,b.TASK_STATUS status,b.DATA_NAME bName,b.COLLECT_DATA_NAME bCollectName,b.task_submit_time submitTime,"
				+ "b.COLLECT_USER_ID userId,c.GPS_TIME gpsTime,c.PHOTO_TIME photoTime,"
				+ "c.COLLECT_OFFSET_X x,c.COLLECT_OFFSET_Y y,c.GPS_ACCURACY accuracy,a.owner_id ownerId,"
				+ "c.id cid,c.GPS_COUNT levels,c.POSITION position,c.IMG_NAME imageName,a.VERIFY_MAINTAIN_TIME mTime,a.VERIFY_FREEZE_TIME fTime,b.task_clazz_id taskClazzId,c.image_index imageIndex "
				+ "from collect_base_package a "
				+ "left join collect_task_base b "
				+ "on a.id=b.TASK_PACKAGE_ID "
				+ "left join collect_task_img c "
				+ "on b.id=c.BASE_ID "
				+ "where c.image_status="+CommonConstant.TASK_IMG_STATUS.USE.getCode()+" and  a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.SUBMIT.getCode()+" and a.id=#{packageId}";
	}
	
	@Author("yaming.xu")
	@Shard(indexColumn = "collectUserId,ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectToAuditEntity.class)
	
	public Object selectCollectInfoForReAudit(@SqlParameter("packageId") Long packageId,@SqlParameter("collectUserId") Long collectUserId,@SqlParameter("ownerId") Long ownerId){
		return "select a.id pid,a.TASK_PACKAGE_NAME pName,a.TASK_PACKAGE_DESC pDesc,a.TASK_PACKAGE_COUNT pCount,a.TASK_PACKAGE_PAY pay,"
				+ "b.id bid,b.TASK_STATUS status,b.DATA_NAME bName,b.COLLECT_DATA_NAME bCollectName,b.task_submit_time submitTime,"
				+ "b.COLLECT_USER_ID userId,c.GPS_TIME gpsTime,c.PHOTO_TIME photoTime,"
				+ "c.COLLECT_OFFSET_X x,c.COLLECT_OFFSET_Y y,c.GPS_ACCURACY accuracy,a.owner_id ownerId,"
				+ "c.id cid,c.GPS_COUNT levels,c.POSITION position,c.IMG_NAME imageName,a.VERIFY_MAINTAIN_TIME mTime,a.VERIFY_FREEZE_TIME fTime,b.task_clazz_id taskClazzId,c.image_index imageIndex "
				+ "from collect_base_package a "
				+ "left join collect_task_base b "
				+ "on a.id=b.TASK_PACKAGE_ID "
				+ "left join collect_task_img c "
				+ "on b.id=c.BASE_ID "
				+ "where a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.RE_AUDIT.getCode()+" and a.id=#{packageId}";
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "collectUserId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = CollectToAuditEntity.class)
	@Deprecated
	public Object selectTaskInfoForAudit(@SqlParameter("baseId") Long baseId,@SqlParameter("collectUserId") Long collectUserId){
		return "select a.id pid,a.TASK_PACKAGE_NAME pName,a.TASK_PACKAGE_DESC pDesc,b.TASK_PAY pay,"
				+ "b.id bid,b.TASK_STATUS status,b.DATA_NAME bName,b.COLLECT_DATA_NAME bCollectName,b.task_submit_time submitTime,"
				+ "b.COLLECT_USER_ID userId,c.GPS_TIME gpsTime,c.PHOTO_TIME photoTime,"
				+ "c.COLLECT_OFFSET_X x,c.COLLECT_OFFSET_Y y,c.GPS_ACCURACY accuracy,"
				+ "c.id cid,c.GPS_COUNT levels,c.POSITION position,c.IMG_NAME imageName,b.VERIFY_MAINTAIN_TIME mTime,b.VERIFY_FREEZE_TIME fTime "
				+ "from collect_base_package a "
				+ "inner join (select id,TASK_STATUS,TASK_PAY,DATA_NAME,COLLECT_DATA_NAME,task_submit_time, COLLECT_USER_ID,TASK_PACKAGE_ID,VERIFY_MAINTAIN_TIME,VERIFY_FREEZE_TIME from collect_task_base where id=#{baseId})b "
				+ "on a.id=b.TASK_PACKAGE_ID "
				+ "left join collect_task_img c "
				+ "on b.id=c.BASE_ID "
				+ "where b.task_status="+CommonConstant.TASK_STATUS.SUBMIT.getCode()+" or b.task_status="+CommonConstant.TASK_STATUS.NOT_FOUND.getCode();
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "collectUserId,ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = OriginCoordinateEntity.class)
	
	public Object selectOriginCoordinateForAudit(@SqlParameter("packageId") Long packageId,@SqlParameter("collectUserId") Long collectUserId,@SqlParameter("ownerId") Long ownerId){
		return "select a.id pid,b.ORIGINAL_X x,b.ORIGINAL_Y y,b.TASK_SAMPLE_IMG imageName,b.COORDINATE_STATUS status from collect_base_package a "
				+ "left join collect_base_original_coordinate b " 
				+ "on a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID "
				+ "where a.id=#{packageId} and a.TASK_PACKAGE_STATUS="+CommonConstant.TASK_STATUS.SUBMIT.getCode();
	}
	@Author("yaming.xu")
	@Shard(indexColumn = "collectUserId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.beanList, resultType = OriginCoordinateEntity.class)
	@Deprecated
	public Object selectCoordinateForAudit(@SqlParameter("packageId") Long packageId,@SqlParameter("collectUserId") Long collectUserId){
		return "select a.id pid,b.ORIGINAL_X x,b.ORIGINAL_Y y,b.TASK_SAMPLE_IMG imageName,b.COORDINATE_STATUS status from collect_base_package a "
				+ "left join collect_base_original_coordinate b " 
				+ "on a.PASSIVE_PACKAGE_ID=b.PACKAGE_ID "
				+ "where a.id=#{packageId} ";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updateTimeoutPackage(
			@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,
			@SqlParameter("expireTime") Long expireTime) {
		return "update  Collect_Base_Package set update_time=#{collectBasePackage.updateTime} "
				+ " ,task_package_status= "+TASK_STATUS.TIME_OUT.getCode()+" "
				+ " where ( task_package_status="+TASK_STATUS.SAVE.getCode()+" or "
				+ " task_package_status="+TASK_STATUS.ALLOT.getCode()+" ) "
				+ " and allot_End_Time<#{expireTime}";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectBasePackage.class)
	public Object selectTimeoutPackage(
			@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		return "select id,PASSIVE_PACKAGE_ID passivePackageId,TASK_PACKAGE_NAME taskPackageName,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,CREATE_TIME createTime,UPDATE_TIME updateTime,"
				+ "ALLOT_END_TIME allotEndTime,verify_Maintain_Time verifyMaintainTime,allot_Maintain_Time allotMaintainTime,"
				+ "ALLOT_USER_ID allotUserId,COLLECT_USER_ID collectUserId,verify_Freeze_Time verifyFreezeTime,"
				+ "TASK_PACKAGE_CATE taskPackageCate,TASK_PACKAGE_VERIFY_STATUS taskPackageVerifyStatus,TASK_CLAZZ_ID taskClazzId,OWNER_ID ownerId "
				+ " from Collect_Base_Package where PASSIVE_PACKAGE_ID is not null and update_time=#{collectBasePackage.updateTime} and task_package_status="+TASK_STATUS.TIME_OUT.getCode()+" ";
	}
	

}
