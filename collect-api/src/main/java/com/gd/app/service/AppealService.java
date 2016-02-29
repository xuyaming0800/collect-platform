package com.gd.app.service;

import java.util.List;
import java.util.Map;

import com.gd.app.entity.Appeal;

public interface AppealService {
	/**
	 * 按条件查询申诉
	 * 
	 * @param appeal
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Appeal> queryAppeals(Appeal appeal, int pageIndex, int pageSize);
	/**
	 * 查询单条申诉
	 * 
	 * @param id
     * @param userName
	 * @return
	 */
	public Appeal queryAppeal(final Long id,String userName);
	/**
	 * 通过baseID查询单条申诉
	 * 
	 * @param baseID
	 * @param userName
	 * @return
	 */
	public Appeal queryAppealByBaseID(final Long baseID,String userName);
	/**
	 * 按条件查询申诉数量
	 * 
	 * @param appeal
	 * @return
	 */
	public int queryAppealsCount(Appeal appeal);
	/**
	 * 新增申诉
	 * 
	 * @param appeal
	 * @return
	 */
	public int saveAppeal(Appeal appeal);
	/**
	 * 修改申诉处理状态
	 * 
	 * @param appeal
	 * @return
	 */
	public int updateAppeal(Appeal appeal);
	/**
	 * 通过baseId查询任务
	 * 
	 * @param baseID
	 * @param userName
	 * @return
	 */
	public Map<String, String> queryAppealTask(long baseID,String userName);
	

}
