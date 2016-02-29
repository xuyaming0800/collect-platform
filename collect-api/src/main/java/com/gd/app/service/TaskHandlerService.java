package com.gd.app.service;

import com.gd.app.entity.AgentTaskToken;
import com.gd.app.entity.AgentTaskUploadEntity;
import com.gd.app.entity.TaskLockResultEntity;
import com.gd.app.exception.AppException;

public interface TaskHandlerService {
	/**
	 * 添加token
	 * @param token
	 * @return
	 */
	public AgentTaskToken addAgentTaskToken(AgentTaskToken token);
	/**
	 * 查询TokenId
	 * @param id
	 * @return
	 */
	public AgentTaskToken getAgentTaskToken(String id,String userName);
	/**
	 * 作废token
	 * @param tokenId
	 * @param userName
	 */
	public void deleteAgentTaskTokenForSave(String tokenId,String userName);
	/**
	 * 保存锁定任务
	 * 
	 * @param taskId
	 * @param userName
	 * @param dataName
	 * @param x
	 * @param y
	 * @param adCode
	 * @return
	 */
	public TaskLockResultEntity saveLockTask(String taskId, String userName,
			String dataName, String x, String y, String adCode,
			String deviceInfo,String scoreId,String tokenId);
	/**
	 * 保存锁定任务 定向任务
	 * @param taskId
	 * @param userName
	 * @param dataName
	 * @param x
	 * @param y
	 * @param adCode
	 * @param deviceInfo
	 * @param scoreId
	 * @return
	 */
	public abstract TaskLockResultEntity saveLockUserTask(String taskId,
			String userName, String dataName, String x, String y, String adCode,String deviceInfo,String scoreId,String tokenId);
	/**
	 * 更新TOKEN信息区分Save
	 * @param tokenId
	 * @param userName
	 */
	public void updateAgentTaskTokenForSave(String tokenId,String userName);
	/**
	 * 修改任务名称
	 * @param baseId
	 * @param adCode
	 * @param dataName
	 * @param userName
	 * @param scoreId
	 */
	public void modifyTask(String baseId, String adCode, String dataName,String userName,String scoreId,String tokenId);
	/**
	 * 检测批量提交的时候的批量总数是否超过限制
	 * @param userName
	 * @param pack
	 * @return
	 */
	public boolean checkCountIsOut(String userName, String pack);
	/**
	 * 检测用户单条提交的时候是否时间差过短
	 * @param userName
	 * @return
	 */
	public boolean checkUserIsTimeOut(String userName);
	/**
	 * 发现作弊的任务，释放该任务
	 * 
	 * @param baseId
	 * @param taskId
	 */
	public void relaseCheatTask(String baseId, String taskId,
			Boolean isUpdateSearchIndex,String userName,String recommend);
	/**
	 * 更新Token信息
	 * @param token
	 */
	public void updateAgentTaskToken(AgentTaskToken token);
	/**
	 * 提交任务-非定向任务
	 * @param agentTaskUpload
	 * @return
	 * @throws AppException
	 */
	public String saveSubmitUploadTask(AgentTaskUploadEntity agentTaskUpload,AgentTaskToken token)throws AppException;
	/**
	 * 设置弹出验证码标记 批量
	 * @param userName
	 * @param pack
	 */
	public void setCountFlag(String userName, String pack);
	/**
	 * 设置弹出验证码标记 单条
	 * @param userName
	 */
	public void setSubMitTaskFlag(String userName);
	/**
	 * 保存验证码--单条提交
	 * @param userName
	 */
	public void saveSecureCode(String userName,String code);
	/**
	 * 保存验证码--批量提交
	 * @param userName
	 * @param code
	 */
	public void saveSecureCodeForBatch(String userName,String code);
	/**
	 * 校验验证码-单条提交
	 * @param userName
	 * @return
	 */
	public boolean checkSecureCode(String userName,String code);
	/**
	 * 校验验证码-批量提交
	 * @param userName
	 * @param code
	 * @return
	 */
	public String checkSecureCodeForBatch(String userName,String code);
	/**
	 * 解锁任务删除任务
	 * @param taskId
	 * @param userName
	 * @param dataName
	 */
	public void updateTaskByUnLock(String taskId, String userName,
			String dataName);

}
