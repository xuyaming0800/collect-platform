package com.autonavi.collect.component;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.autonavi.collect.bean.BossUserTable;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.USER_CACHE_TYPE;
import com.autonavi.collect.dao.BossUserDao;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;

@Component
public class CollectUserCacheComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private BossUserDao bossUserDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	private PropertiesConfig pc = null;
	
	private CollectUserCacheComponent()throws Exception{
		if (pc == null)
			pc = PropertiesConfigUtil.getPropertiesConfigInstance();
	}
	
	@SuppressWarnings({ "unchecked"})
	public void initAll()throws BusinessException {
		//初始化用户缓存 --设置成会超时
		Jedis jedis = null;
		List<BossUserTable> users=(List<BossUserTable>)bossUserDao.getAllBossUsers(CommonConstant.getSingleDataSourceKey());
		try {
			jedis=redisUtilComponent.getRedisInstance();
			for(BossUserTable user:users){
				redisUtilComponent.setRedisStringCache(jedis, CommonConstant.USER_CACHE_PREFIX+user.getUserName(),  user.getId().toString(), 0);
				redisUtilComponent.setRedisStringCache(jedis, CommonConstant.USER_ID_CACHE_PREFIX+ user.getId(),  user.getUserName(), 0);
			}
			logger.info("缓存用户共"+users.size()+"条");
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}
	public void addUserIdCache(String userName,Long id,Integer cacheType)throws BusinessException{
		Jedis jedis = null;
		try {
			if(cacheType.equals(USER_CACHE_TYPE.REDIS.getCode())){
				Integer exprie=Integer.valueOf(pc.getProperty(CommonConstant.PROP_USER_CACHE_EXPIRE).toString());
				jedis=redisUtilComponent.getRedisInstance();
				redisUtilComponent.setRedisStringCache(jedis, CommonConstant.USER_CACHE_PREFIX+userName,  id.toString(), exprie);
				redisUtilComponent.setRedisStringCache(jedis, CommonConstant.USER_ID_CACHE_PREFIX+ id.toString(),  userName, exprie);
			}else{
				throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
			}
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}
	public String getUserIdFromCache(String userName,Integer cacheType)throws BusinessException{
		Jedis jedis = null;
		try {
			if(cacheType.equals(USER_CACHE_TYPE.REDIS.getCode())){
				jedis=redisUtilComponent.getRedisInstance();
				return redisUtilComponent.getRedisStringCache(CommonConstant.USER_CACHE_PREFIX+userName,jedis);
			}else{
				throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
			}
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}
	public String getUserNameFromCache(Long id,Integer cacheType)throws BusinessException{
		Jedis jedis = null;
		try {
			if(cacheType.equals(USER_CACHE_TYPE.REDIS.getCode())){
				jedis=redisUtilComponent.getRedisInstance();
				return redisUtilComponent.getRedisStringCache(CommonConstant.USER_ID_CACHE_PREFIX+ id.toString(),jedis);
			}else{
				throw new BusinessException(BusinessExceptionEnum.CACHE_TYPE_NOT_SUPPORT);
			}
			
		} finally {
			redisUtilComponent.returnRedis(jedis);
		}
	}
	
//	public String getUserIdFromCache(String userName,Jedis jedis)throws BusinessException{
//		return redisUtilComponent.getRedisStringCache(CommonConstant.USER_CACHE_PREFIX+userName,jedis);
//	}
//	public String getUserNameFromCache(Long id,Jedis jedis)throws BusinessException{
//		return redisUtilComponent.getRedisStringCache(CommonConstant.USER_ID_CACHE_PREFIX+ id.toString(),jedis);
//	}

}
