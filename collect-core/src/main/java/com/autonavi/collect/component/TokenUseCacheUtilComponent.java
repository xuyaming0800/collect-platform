package com.autonavi.collect.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import autonavi.online.framework.util.json.JsonBinder;

import com.autonavi.collect.bean.CollectUseToken;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.constant.CommonConstant.TOKEN_USE;
import com.autonavi.collect.dao.CollectUseTokenDao;
import com.autonavi.collect.exception.BusinessException;

@Component
public class TokenUseCacheUtilComponent {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private CollectUseTokenDao collectUseTokenDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init(){
		logger.info("初始化Token校验配置");
		try {
			List<CollectUseToken> list=(List<CollectUseToken>)collectUseTokenDao.getAllCollectUseToken(CommonConstant.getSingleDataSourceKey());
			if(list==null)list=new ArrayList<CollectUseToken>();
			JsonBinder binder=JsonBinder.buildNormalBinder(false);
			for(CollectUseToken use:list){
				redisUtilComponent.setRedisJsonCache(CommonConstant.TOKEN_USE_CACHE_PREFIX+use.getOwnerId(), use, binder, 0);
			}
			logger.info("缓存Token校验配置共"+list.size()+"条");
		} catch (BusinessException e) {
			logger.error(e.getMessage(),e);
		} finally{
			
		}
	}
	/**
	 * 从缓存获取
	 * @param ownerId
	 * @return
	 * @throws BusinessException
	 */
	public CollectUseToken getCollectUseTokenFromCache(Long ownerId)throws BusinessException{
		JsonBinder binder=JsonBinder.buildNormalBinder(false);
		return redisUtilComponent.getRedisJsonCache(CommonConstant.TOKEN_USE_CACHE_PREFIX+ownerId, CollectUseToken.class, binder);
	}
	/**
	 * 检测是否使用Token机制
	 * @param ownerId
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkUseTokenFromCache(Long ownerId)throws BusinessException{
		logger.info("ownerId is "+ownerId);
		CollectUseToken use=this.getCollectUseTokenFromCache(ownerId);
		logger.info("use is "+use);
		if(use==null){
			//未设置的情况下默认不使用
			return false;
		}
		if(use.getStatus().equals(TOKEN_USE.YES.getCode())){
			return true;
		}
		return false;
	}

}
