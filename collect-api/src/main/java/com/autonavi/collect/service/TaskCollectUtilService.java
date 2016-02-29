package com.autonavi.collect.service;

import java.util.List;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.exception.BusinessException;


public interface TaskCollectUtilService {
	/**
	 * 偏移坐标
	 * @param x
	 * @param y
	 * @return
	 */
	public String[] offsetXY(String x,String y);
	/**
	 * 计算adcode
	 * @param corrX
	 * @param corrY
	 * @return
	 */
	public String fetchAdcode(double corrX, double corrY);
	/**
	 * 获取用户缓存
	 * @param userName
	 * @return
	 */
	public Long getUserIdCache(String userName);
	/**
	 * 获取用户缓存
	 * @param userId
	 * @return
	 */
	public String getUserNameCache(Long userId);
	/**
	 * adcode转换为名称
	 * @param adcode
	 * @return
	 */
	public String fetchAdcodeToName(String adcode);
	/**
	 * 坐标转换
	 * @param fromSys
	 * @param toSys
	 * @param x
	 * @param y
	 * @return
	 */
	public double[] transferCoordinate(Integer fromSys,Integer toSys,double x,double y);
	/**
	 * 发送打水印消息
	 * @param imagePath
	 */
	public void notifyImageWather(Long userId,Long taskId,List<String> imagePath)throws BusinessException;
	/**
	 * 获取全系统唯一主键
	 * @param imagePath
	 */
	public Long getPlatFromUniqueId()throws BusinessException;
	/**
	 * 检测任务附加属性是否是第一次保存
	 * @param baseId
	 * @param level
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkTaskLevelExist(Long baseId,String level)throws BusinessException;
	/**
	 * 检测附加属性批次是否正确
	 * @param baseId
	 * @param level
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkTaskLevelBatchInfo(Long baseId,String level,String value)throws BusinessException;
	/**
	 * 检测附加属性批次是否正确
	 * @param baseId
	 * @param level
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkTaskLevelBatchInfo(Long baseId,String level,String value,String clazzId)throws BusinessException;
	/**
	 * 检测附加属性批次是否正确
	 * @param baseId
	 * @param level
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkTaskLevelBatchInfo(Long baseId,String level,String value,String clazzId,String money)throws BusinessException;
	/**
	 * 获取分类信息对应的名称
	 * @param collectClazzId
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz getCollectTaskClazzByIdFromCache(Long collectClazzId)throws BusinessException;
	/**
	 * 
	 * @param baseId
	 * @param level
	 * @param clazzId
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkTaskClazzInfo(Long baseId, String level,String clazzId) throws BusinessException;
	/**
	 * 获取BatchID并缓存
	 * @return
	 * @throws BusinessException
	 */
	public Long getPlatFromBatchId(Long userId)throws BusinessException;
	
	/**
	 * 检查BATCHID
	 * @return
	 * @throws BusinessException
	 */
	public Boolean checkPlatFromBatchId(Long batchId,Long userId)throws BusinessException;
	/**
	 * 清理BATCHID缓存
	 */
	public void clearPlatFromBatchId(Long batchId,Long userId)throws BusinessException;

}
