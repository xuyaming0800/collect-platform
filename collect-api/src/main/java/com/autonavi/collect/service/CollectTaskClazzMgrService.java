package com.autonavi.collect.service;

import java.util.List;

import com.autonavi.collect.bean.CollectTaskClazz;
import com.autonavi.collect.exception.BusinessException;

public interface CollectTaskClazzMgrService {
	/**
	 * 根据父ID获取分类信息列表
	 * @param parentId
	 * @return
	 * @throws BusinessException
	 */
	public List<CollectTaskClazz> getCollectTaskCLazzListByParent(Long parentId)throws BusinessException;
	/**
	 * 根据ID获取分类信息
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz getCollectTaskCLazz(Long id)throws BusinessException;
	/**
	 * 更新分类信息
	 * @param collectTaskClazz
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz updateCollectTaskClazz(CollectTaskClazz collectTaskClazz)throws BusinessException;
	/**
	 * 新增分类信息
	 * @param collectTaskClazz
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz addCollectTaskClazz(CollectTaskClazz collectTaskClazz)throws BusinessException;
	/**
	 * 重置顺序
	 * @param id
	 * @param isUp
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz reIndexCollectTaskClazz(Long id,Boolean isUp)throws BusinessException;
	/**
	 * 删除分类信息
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz deleteCollectTaskClazz(Long id)throws BusinessException;
	/**
	 * 恢复删除的分类
	 * @param collectTaskClazz
	 * @return
	 * @throws BusinessException
	 */
	public CollectTaskClazz recoveryCollectTaskClazz(Long id)throws BusinessException;
	/**
	 * 刷新缓存
	 */
	public void refreshCollectTaskClazzCache();

}
