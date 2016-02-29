package com.autonavi.collect.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.collect.bean.CollectBasePackage;
import com.autonavi.collect.bean.CollectPassivePackage;
import com.autonavi.collect.business.CollectCore;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TASK_STATUS;
import com.autonavi.collect.dao.CollectBasePackageDao;
import com.autonavi.collect.dao.CollectPassivePackageDao;
import com.autonavi.collect.dao.CollectPassiveTaskDao;
import com.autonavi.collect.dao.CollectTaskAllotUserDao;
import com.autonavi.collect.dao.CollectTaskBaseDao;
import com.autonavi.collect.entity.CollectTaskSubmitEntity;
import com.autonavi.collect.entity.TaskClazzQueryEntity;
import com.autonavi.collect.entity.TaskPackageSubmitCheckEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
@Component
public class OtherTaskBusinessManager {
	@Autowired
	private CollectBasePackageDao collectBasePackageDao;
	@Autowired
	private CollectPassivePackageDao collectPassivePackageDao;
	@Autowired
	private CollectTaskAllotUserDao collectTaskAllotUserDao;
	@Autowired
	private SyncTaskBusinessMananger syncTaskBusinessMananger;
	@Autowired
	private CollectPassiveTaskDao collectPassiveTaskDao;
	@Autowired
	private CollectTaskBaseDao collectTaskBaseDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	
	
	@Autowired
	private TaskClazzTreeQuery taskClazzTreeQuery;
	
	public TaskClazzTreeQuery getTaskClazzTreeQuery() {
		return taskClazzTreeQuery;
	}
	
	@Component
	public class TaskClazzTreeQuery implements CollectCore<String, TaskClazzQueryEntity> {

		@Autowired
		public TaskClazzTreeQuery(OtherTaskBusinessManager CollectTaskSubmitEntity) {

		}
		@Override
		public String execute(TaskClazzQueryEntity entity)
				throws BusinessException {
			Jedis jedis=null;
			try {
				jedis=redisUtilComponent.getRedisInstance();
				return redisUtilComponent.getRedisStringCache(entity.getPrefix()+entity.getId().toString(),jedis);
			} finally {
				redisUtilComponent.returnRedis(jedis);
			}
			
		}
		
	}

}
