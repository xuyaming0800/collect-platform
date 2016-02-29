package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectUseToken;

@Repository
public class CollectUseTokenDao {
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectUseToken.class)
	public Object getAllCollectUseToken(
			@SqlParameter("dsKey") Integer dsKey) {
		return " select id,OWNER_Id ownerId,status from COLLECT_USE_TOKEN ";
	}

}
