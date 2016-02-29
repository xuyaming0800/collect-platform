package com.autonavi.collect.dao;

import org.springframework.stereotype.Component;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.Shard;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.aspect.annotation.Update;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectTaskBase;
import com.autonavi.collect.constant.CommonConstant;

@Component
public class CollectTaskAllotUserDao {

	@Author("ang.ji")
	@Shard(indexColumn = "collectTaskBase.allotUserId,collectTaskBase.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object select(@SqlParameter("collectTaskBase") CollectTaskBase collectTaskBase) {
		return "select id from collect_allot_base_user "
				+ "where base_id=#{collectTaskBase.id} "
				+ "and allot_user_id=#{collectTaskBase.allotUserId}";
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object selectByBasePackage(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("status") Integer status) {
		return "select id from collect_allot_base_user "
				+ "where PACKAGE_ID=#{collectBasePackage.passivePackageId} "
				+ "and allot_user_id=#{collectBasePackage.allotUserId} "
				+ "and status=#{status}";
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updatePackageId(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newPackageId") Long newPackageId ) {
		return "update collect_allot_base_user "
				+ "set PACKAGE_ID=#{newPackageId} "
				+ "where PACKAGE_ID=#{collectBasePackage.passivePackageId} ";
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Update
	public Object updateStatus(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage,@SqlParameter("newStatus") Integer newStatus ) {
		return "update collect_allot_base_user "
				+ "set status=#{newStatus} "
				+ "where PACKAGE_ID=#{collectBasePackage.passivePackageId} ";
	}
	@Author("ang.ji")
	@Shard(indexColumn = "collectBasePackage.allotUserId,collectBasePackage.ownerId", indexName = "USER_OWNER_ID_INDEX")
	@Select(collectionType = CollectionType.column, resultType = Long.class)
	public Object selectFirstAllotInfoByPid(@SqlParameter("collectBasePackage") CollectBasePackage collectBasePackage) {
		return "select count(0) from collect_allot_base_user "
				+ " where PACKAGE_ID=#{collectBasePackage.passivePackageId} "
				+ " and status="+CommonConstant.ALLOT_USER_STATUS.VALID.getCode();
	}
}
