package com.gd.app.dao;

import com.gd.app.entity.AgentTaskBase;
import com.gd.app.entity.AgentTaskMz;
import com.gd.app.entity.AgentTaskUploadEntity;
import com.gd.app.entity.ViewUserScore;

public interface AgentTaskBaseDao {
	public long getPrimaryKey();
	/**
	 * 根据门址表主键获取任务信息
	 * @param id
	 * @param userName
	 * @return
	 */
	public Object getAgentInfoByBaseId(String id,String userName);
	/**
	 * 根据门址表主键获取任务信息
	 * @param id
	 * @param userName
	 * @return
	 */
	public Object getAgentTaskBaseByBaseId(String id,String userName);
	/**
	 * 获取高德母库信息
	 * @param subAdCode
	 * @param dataName
	 * @return
	 */
	public Object getAutoNaviDBByName(String subAdCode, String dataName);
	/**
	 * 获取审核回传信息
	 * @param subAdCode
	 * @param dataName
	 * @return
	 */
	public Object getAuditBackDBByName(String subAdCode, String dataName);
	/**
	 * 获取被动任务信息
	 * @param id
	 * @return
	 */
	public Object getTaskPassiveById(String id);
	/**
	 * 新增门址任务
	 * @param base
	 * @return
	 */
	public Object addAgentTaskBase(AgentTaskBase base);
	public Object addAgentTaskBase(AgentTaskUploadEntity base);
	/**
	 * 新增门址详细信息
	 * @param base
	 * @return
	 */
	public Object addAgentTaskMz(AgentTaskMz mz);
	public Object addAgentTaskMz(AgentTaskUploadEntity mz);
	/**
	 * 更新被动任务
	 * @param status
	 * @param id
	 * @return
	 */
	public Object updateTaskPassive(int status,Long id,Long timestamps,String userName);
	/**
	 * 提交被动任务
	 * @param status
	 * @param id
	 * @param userName
	 * @param addressType
	 * @return
	 */
	public Object updateTaskPassive(int status,Long id,String userName);
	/**
	 * 更新taskALL状态
	 * @param status
	 * @param id
	 * @param timestamps
	 * @param userName
	 * @return
	 */
	public Object updateTaskAll(int status,Long id,String userName);
	/**
	 * 更新定向任务状态
	 * @param status
	 * @param id
	 * @param timestamps
	 * @return
	 */
	public Object updateAllotTask(int status,Long id,Long timestamps);
	/**
	 * 移走被动任务
	 * @param id
	 * @return
	 */
	public Object moveTaskAll(Long id,String userName);
	/**
	 * 被动任务绑定用户
	 * @param id
	 * @param userName
	 * @return
	 */
	public Object updateTaskAllUserInfo(Long id,String userName);
	/**
	 * 获取定向任务 单一数据源
	 * @param id
	 * @return
	 */
	public Object getAllotTaskById(String id);
	/**
	 * 修改保存任务的名称
	 * @param base
	 * @return
	 */
	public Object modifyAgentTaskName(AgentTaskBase base);
	/**
	 * 更新任务的状态
	 * @param base
	 * @return
	 */
	public Object updateAgentTaskBaseStatus(AgentTaskBase base);
	/**
	 * 更新任务信息
	 * @param base
	 * @return
	 */
	public Object updateAgentTaskBase(AgentTaskUploadEntity base);
	/**
	 * 更新任务详细信息
	 * @param mz
	 * @return
	 */
	public Object updateAgentTaskMz(AgentTaskUploadEntity mz);
	/**
	 * 或者用户保存的被动任务详细信息
	 * @param taskId
	 * @return
	 */
	public Object getUserSavePassiveTaskByTaskId(String taskId,String userName);
	/**
	 * 获取被动任务行锁
	 * @param tokenId
	 * @return
	 */
	public Object getPassiveTaskLineLock(String id);
	/**
	 * 获取运营支撑的数据
	 * @param view
	 * @return
	 */
	public Object getViewUserScore(String userName);
	/**
	 * 更新任务的运营支撑信息
	 * @param view
	 * @return
	 */
	public Object updateAgentTaskBase(ViewUserScore view);
	/**
	 * 查询任务数量 各个状态
	 * @param userName
	 * @return
	 */
	public Object queryBaseStatusCount(String userName);
	/**
	 * 查询用户任务数量
	 * @param userName
	 * @param isAll
	 * @return
	 */
	public Object queryUserTaskCount(String userName,int status,boolean isAll);
	/**
	 * 分页查询用户任务
	 * @param userName
	 * @param status
	 * @param start
	 * @param limit
	 * @param isAll
	 * @return
	 */
	public Object queryUserTask(String userName,int status,int start,int limit,boolean isAll);
	/**
	 * 根据TASK_ID查询任务
	 * @param userName
	 * @param dataName
	 * @param taskId
	 * @return
	 */
	public Object getUserTaskByTaskId(String userName,String dataName,String taskId);
	/**
	 * 逻辑删除任务
	 * @param userName
	 * @param id
	 * @return
	 */
	public Object deleteTaskById(String userName,String id);
	/**
	 * 释放被动任务
	 * @param taskId
	 * @return
	 */
	public Object initPassiveTask(String taskId,boolean isAllot);
	/**
	 * 释放定向任务
	 * @param taskId
	 * @return
	 */
	public Object initAllotTask(String taskId,String userName);
	/**
	 * 根据ADCODE和关键字获取被动任务数量
	 * @param adCode
	 * @param dataName
	 * @return
	 */
	public Object getPassiveTaskCountByAdcode(String adCode,String dataName);
	/**
	 * 根据ADCODE范围和关键字获取被动任务数量
	 * @param minAdcode
	 * @param maxAdcode
	 * @param dataName
	 * @return
	 */
	public Object getPassiveTaskCountByAdcodeScope(int minAdcode, int maxAdcode, String dataName);
	/**
	 * 根据ADCODE和关键字获取被动任务
	 * @param adCode
	 * @param dataName
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object getPassiveTaskByAdcode(String adCode,String dataName,int start,int limit);
	/**
	 * 根据ADCODE范围和关键字获取被动任务
	 * @param minAdcode
	 * @param maxAdcode
	 * @param dataName
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object getPassiveTaskByAdcodeScope(int minAdcode, int maxAdcode, String dataName,int start,int limit);
	/**
	 * 获取附近任务-计数
	 * @param taskType
	 * @param xStart
	 * @param xEnd
	 * @param yStart
	 * @param yEnd
	 * @param dataName
	 * @return
	 */
	public Object getNearPassiveTaskCount(int taskType,double xStart,double xEnd,double yStart,double yEnd,String dataName);
	/**
	 * 获取附近任务
	 * @param taskType
	 * @param xStart
	 * @param xEnd
	 * @param yStart
	 * @param yEnd
	 * @param dataName
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object getNearPassiveTask(int taskType,double xStart,double xEnd,double yStart,double yEnd,String dataName,int start,int limit);
	/**
	 * 获取定向任务数量
	 * @param dataName
	 * @param userName
	 * @return
	 */
	public Object getAllotPassiveCountByUser(String dataName,String userName);
	/**
	 * 获取定向任务
	 * @param dataName
	 * @param userName
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object getAllotPassiveByUser(String dataName,String userName,int start,int limit);
	/**
	 * 获取定向任务-按道路排序
	 * @param dataName
	 * @param userName
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object getAllotPassiveByUserAndRoad(String dataName,String userName,int start,int limit);
	/**
	 * 根据ADCODE聚合被动任务数量
	 * @param minAdcode
	 * @param maxAdcode
	 * @return
	 */
	public Object polyPassiveTaskByAdcode(int minAdcode,int maxAdcode);
	/**
	 * 我征服的城市查询
	 * @param userName
	 * @return
	 */
	public Object queryCityCountByUser(String userName);
	/**
	 * 被动任务反馈
	 * @param userName
	 * @param taskId
	 * @param dataName
	 * @param errorCode
	 * @param comment
	 * @param deviceInfo
	 * @return
	 */
	public Object addPassiveTaskBack(String userName,String taskId,String dataName,String errorCode,String comment,String deviceInfo);
	
	
	
	
	
	

}
