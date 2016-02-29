package com.autonavi.collect.dao;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.aspect.annotation.SqlParameter;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.autonavi.collect.bean.CollectDicScoreDetail;



@Repository
public class CollectTaskScoreDao {
	
	
	@Author("yaming.xu")
	@SingleDataSource(keyName="dsKey")
	@Select(collectionType = CollectionType.beanList, resultType = CollectDicScoreDetail.class)
	public Object getAllScoreDetail(@SqlParameter("dsKey")Integer dsKey) {
		return "select id,value,task_Type taskType,score_Type scoreType,status from collect_dic_score_detail ";
	}

}
