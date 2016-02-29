package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.constant.CommonConstant;

@Repository
public class CollectPassivePackageDao {
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.bean, resultType = CollectPassivePackage.class)
	public Object getCollectPassivePackageById(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("id") Long id) {
		return "select id,TASK_PACKAGE_NAME taskPackageName,"
				+ "TASK_PACKAGE_DESC taskPackageDesc,TASK_PACKAGE_COUNT taskPackageCount,TASK_PACKAGE_PAY taskPackagePay,owner_id ownerId,"
				+ "TASK_PACKAGE_STATUS taskPackageStatus,TASK_PACKAGE_TYPE taskPackageType,CREATE_TIME createTime,UPDATE_TIME updateTime,"
				+ "ALLOT_END_TIME allotEndTime,verify_Maintain_Time verifyMaintainTime,TASK_PACKAGE_CATE taskPackageCate,ALLOT_USER_ID allotUserId, "
				+ "allot_Maintain_Time allotMaintainTime,verify_Freeze_Time verifyFreezeTime,task_clazz_id taskClazzId  from collect_passive_package where id=#{id}";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object lockPassivePackageById(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("id") Long id) {
		return "update collect_passive_package set id=id where id=#{id}";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updatePackageStatus(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.SUBMIT.getCode())){
			temp=",collect_user_id=#{collectBasePackage.collectUserId} ";
		}
		String temp1="";
		if(collectBasePackage.getAllotEndTime()!=null){
			temp1=",allot_end_time=#{collectBasePackage.allotEndTime} ";
		}
		return "update collect_passive_package "
				+ "set TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ temp
				+ temp1
				+ "where id=#{collectBasePackage.passivePackageId} "
				+ "and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	@Deprecated
	public Object updatePackageTaskCount(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("taskCount") Integer taskCount) {
		return "update collect_passive_package "
				+ "set TASK_PACKAGE_COUNT=#{taskCount} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", allot_user_id=#{collectBasePackage.allotUserId} "
				+ "where id=#{collectBasePackage.passivePackageId} "
				+ "and TASK_PACKAGE_COUNT=#{collectBasePackage.taskPackageCount}";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updatePackageStatusForCollect(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		return "update collect_passive_package "
				+ "set TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ ", collect_user_id=#{collectBasePackage.collectUserId} "
				+ "where id=#{collectBasePackage.passivePackageId} ";
//				+ "and TASK_PACKAGE_STATUS=#{collectBasePackage.taskPackageStatus}";
	}
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Update
	public Object updatePackageStatusForAudit(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newTaskStatus") Integer newTaskStatus) {
		String temp="";
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.ALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.UNALLOT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.TIME_OUT.getCode())){
			temp=", collect_user_id=null "
					+ ", allot_user_id=null "
					+ ",TASK_PACKAGE_VERIFY_STATUS=null ";
		}
		if(newTaskStatus.equals(CommonConstant.TASK_STATUS.FINISH.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.FIRST_AUDIT.getCode())
				||newTaskStatus.equals(CommonConstant.TASK_STATUS.RE_AUDIT.getCode())){
			temp=",TASK_PACKAGE_VERIFY_STATUS=#{collectBasePackage.taskPackageVerifyStatus} ";
		}
		return "update collect_passive_package "
				+ "set TASK_PACKAGE_STATUS=#{newTaskStatus} "
				+ ", update_time=#{collectBasePackage.updateTime} "
				+ temp
				+ "where id=#{collectBasePackage.passivePackageId} ";
	}

}
