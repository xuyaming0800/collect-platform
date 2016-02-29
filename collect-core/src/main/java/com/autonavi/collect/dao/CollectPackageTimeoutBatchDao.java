package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.dao.constant.ReservedWord;
import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Delete;
import autonavi.online.framework.sharding.entry.aspect.annotation.Insert;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectPackageTimeoutBatch;

@Repository
public class CollectPackageTimeoutBatchDao {
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Insert
	public Object insert(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectPackageTimeoutBatch") CollectPackageTimeoutBatch collectPackageTimeoutBatch){
		return "insert collect_package_timeout_batch(id,ds_key,update_time) "
				+ "values(#{"+ReservedWord.snowflake+"},#{collectPackageTimeoutBatch.dsKey},#{collectPackageTimeoutBatch.updateTime})";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectPackageTimeoutBatch.class)
	public Object select(@SqlParameter("dataSourceKey") Integer dataSourceKey){
		return "select id,ds_key dsKey,update_time updateTime from  collect_package_timeout_batch ";
	}
	
	@Author("yaming.xu")
	@SingleDataSource(keyName = "dataSourceKey")
	@Delete
	public Object delete(@SqlParameter("dataSourceKey") Integer dataSourceKey,
			@SqlParameter("collectPackageTimeoutBatch") CollectPackageTimeoutBatch collectPackageTimeoutBatch){
		return "delete from collect_package_timeout_batch where id=#{collectPackageTimeoutBatch.id}";
	}

}
