package com.gd.app.dao;

import com.gd.app.entity.AgentTaskToken;

public interface AgentTaskTokenDao {
    /**
     * 添加token信息
     * @param m
     * @return
     */
	public Object addAgentTaskToken(AgentTaskToken m);
	/**
	 * 根据TokenId获取token信息
	 * @param tokenId
	 * @return
	 */
	public Object getAgentTaskTokenByTokenId(String tokenId,String userName);
	/**
	 * 删除token
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	public Object deleteAgentTaskToken(String tokenId,String userName);
	/**
	 * 转移token
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	public Object moveAgentTaskToken(String tokenId,String userName);
	/**
	 * 更新token的fieldone
	 * @param tokenId
	 * @param userName
	 * @return
	 */
	public Object updateAgentTaskTokenFieldOne(String tokenId,String userName);
	/**
	 * 更新Token信息
	 * @param m
	 * @return
	 */
	public Object updateAgentTaskToken(AgentTaskToken m);

}
