package com.autonavi.collect.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autonavi.online.framework.property.PropertiesConfig;
import autonavi.online.framework.property.PropertiesConfigUtil;

import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.component.RedisUtilComponent;
import com.autonavi.collect.component.TokenUseCacheUtilComponent;
import com.autonavi.collect.constant.CommonConstant;
import com.autonavi.collect.entity.CollectTokenCheckEntity;
import com.autonavi.collect.exception.BusinessException;
import com.autonavi.collect.exception.BusinessExceptionEnum;
import com.autonavi.collect.exception.BusinessRunException;
import com.autonavi.collect.manager.TaskTokenBusinessManager;
import com.autonavi.collect.service.TaskTokenService;

@Service
public class TaskTokenServiceImpl implements TaskTokenService {
	private Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	private TaskTokenBusinessManager taskTokenBusinessManager;
	
	@Autowired
	private TokenUseCacheUtilComponent tokenUseCacheUtilComponent;
//	@Autowired
//	public JedisPool jedisPool;
	@Autowired
	private RedisUtilComponent redisUtilComponent;

	private PropertiesConfig propertiesConfig;

	public TaskTokenServiceImpl() throws Exception {
		this.propertiesConfig = PropertiesConfigUtil.getPropertiesConfigInstance();
	}

	@Override
	public CollectTokenCheckEntity addAgentTaskToken(CollectTaskToken token)throws BusinessException {
		logger.info("获取TOKEN值");
		try {
			int expire = new Integer(this.propertiesConfig.getProperty(CommonConstant.PROP_TOKEN_GET_MIN_SPLIT).toString());
			redisUtilComponent.lockIdByRedis(CommonConstant.TOKEN_GET_CACHE_PREFIX,
					token.getUserId().toString(), BusinessExceptionEnum.TASK_TOKEN_IN_EXPIRE, expire);
			CollectTokenCheckEntity entity=taskTokenBusinessManager.getCheckTokenMd5().execute(token);
			if(entity.getIsNew()){
				entity.setToken(taskTokenBusinessManager.getApplyTaskToken().execute(token));
				return entity;
			}else{
				return entity;
			}
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public Boolean checkTaskToken(Long userId, String token, String baseId,String md5,Long ownerId,
			boolean isCheckMd5) {
		try {
			CollectTaskToken _token=new CollectTaskToken();
			_token.setUserId(userId);
			_token.setToken(token);
			_token.setOwnerId(ownerId);
			if(baseId!=null)
			_token.setBaseId(new Long(baseId));
			if(isCheckMd5&&md5!=null&&!md5.equals("")){
				_token.setAttachmentMd5(md5);
			}
			return taskTokenBusinessManager.getCheckTaskToken().execute(_token);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
	}

	@Override
	public void updateTaskTokenStatus(Long userId,String token, Integer status) {
		try {
			CollectTaskToken _token=new CollectTaskToken();
			_token.setUserId(userId);
			_token.setToken(token);
			_token.setTokenStatus(status);
			taskTokenBusinessManager.getUpdateTaskTokenStatus().execute(_token);
		} catch (BusinessException e) {
			throw new BusinessRunException(e);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new BusinessRunException(BusinessExceptionEnum.PAGE_QUERY_ERROR);
		}
		
	}

	@Override
	public Boolean checkTokenUseStatus(Long ownerId) throws Exception {
		return tokenUseCacheUtilComponent.checkUseTokenFromCache(ownerId);
	}

}
