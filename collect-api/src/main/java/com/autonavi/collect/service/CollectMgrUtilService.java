package com.autonavi.collect.service;

import com.autonavi.collect.exception.BusinessException;

public interface CollectMgrUtilService {
	/**
	 * 设置用户信息到虚拟Session
	 * @param user
	 */
	public <T> void setUserInfoToSession(String sessionKey,T user,int expire)throws BusinessException;
	/**
	 * 返回用户信息从虚拟session
	 * @param sessionKey
	 * @param clazz
	 * @return
	 */
	public <T> T getUserInfoFromSession(String sessionKey,Class<T> clazz,int expire)throws BusinessException;
	/**
	 * 释放Session
	 * @param sessionKey
	 * @param clazz
	 * @throws BusinessException
	 */
	public <T> void releaseUserInfoToSession(String sessionKey,Class<T> clazz)throws BusinessException;

}
