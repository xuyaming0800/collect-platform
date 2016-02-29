package com.autonavi.collect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.service.CollectMgrUtilService;
@Service
public class CollectMgrUtilServiceImpl implements CollectMgrUtilService {
	@Autowired
	private RedisUtilComponent redisUtilComponent;

	@Override
	public <T> void setUserInfoToSession(String sessionKey,T user,int expire)throws BusinessException {
		Jedis jedis=null;
		try {
			jedis=redisUtilComponent.getRedisInstance();
			redisUtilComponent.setRedisJsonCache(jedis, sessionKey, user,expire);
		} finally{
			redisUtilComponent.returnRedis(jedis);
		}
	}

	@Override
	public <T> T getUserInfoFromSession(String sessionKey, Class<T> clazz,int expire)throws BusinessException {
		Jedis jedis=null;
		try {
			jedis=redisUtilComponent.getRedisInstance();
			T object=redisUtilComponent.getRedisJsonCache(jedis, sessionKey, clazz);
			if(object!=null){
				redisUtilComponent.setRedisJsonCache(jedis, sessionKey, object,expire);
			}
			return object;
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}

	@Override
	public <T> void releaseUserInfoToSession(String sessionKey, Class<T> clazz)
			throws BusinessException {
		Jedis jedis=null;
		try {
			jedis=redisUtilComponent.getRedisInstance();
			T object=redisUtilComponent.getRedisJsonCache(jedis, sessionKey, clazz);
			if(object!=null){
				redisUtilComponent.releaseRedisCache(jedis, sessionKey);
			}
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}

}
