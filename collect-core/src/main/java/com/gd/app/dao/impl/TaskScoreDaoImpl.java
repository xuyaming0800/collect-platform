package com.gd.app.dao.impl;

import org.springframework.stereotype.Repository;

import autonavi.online.framework.sharding.entry.aspect.annotation.Author;
import autonavi.online.framework.sharding.entry.aspect.annotation.Select;
import autonavi.online.framework.sharding.entry.aspect.annotation.SingleDataSource;
import autonavi.online.framework.sharding.entry.entity.CollectionType;

import com.gd.app.dao.TaskScoreDao;
import com.gd.app.entity.ScoreLevel;
@Repository("taskScoreDao")
public class TaskScoreDaoImpl implements TaskScoreDao {

	@Override
	@Author("yaming.xu")
	@SingleDataSource(1)
	@Select(collectionType=CollectionType.beanList,resultType=ScoreLevel.class)
	public Object getAllScoreLevel() {
		return "select id,name,score,price,is_Passive isPassive,status,min_lost min,max_lost max from scorelevel";
	}

}
