package com.autonavi.collect.service;

import com.autonavi.collect.bean.CollectTaskToken;
import com.autonavi.collect.entity.CollectTokenCheckEntity;
import com.autonavi.collect.exception.BusinessException;

public interface TaskTokenService {
	/**
	 * 添加TOKEN
	 * @param token
	 * @return
	 */
	public CollectTokenCheckEntity addAgentTaskToken(CollectTaskToken token)throws BusinessException;
	/**
	 * 检测Token
	 * @param userId
	 * @param token
	 * @return
	 */
	public Boolean checkTaskToken(Long userId,String token,String baseId,String md5,Long ownerId,boolean isCheckMd5);
	/**
	 * 更新Token状态
	 * @param token
	 * @param status
	 */
	public void updateTaskTokenStatus(Long userId,String token,Integer status);
	/**
	 * 检测
	 * @param ownerId
	 * @return
	 */
	public Boolean checkTokenUseStatus(Long ownerId)throws Exception;

}
