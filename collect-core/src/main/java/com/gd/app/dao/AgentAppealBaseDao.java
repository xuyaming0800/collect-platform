package com.gd.app.dao;

import com.gd.app.entity.Appeal;


/**
 * 用户申诉dao类
 * @author wenfeng.gao
 *
 */
public interface AgentAppealBaseDao {
	
	public long getPrimaryKey();
	
	/**
	 * 按条件查询申诉
	 * 
	 * @param appeal
	 * @param start
	 * @param limit
	 * @return
	 */
	public Object queryAppeals(Appeal appeal,int start,int limit);
	/**
	 * 查询单条申诉
	 * 
	 * @param id
	 * @param userName
	 * @return
	 */
	public Object queryAppeal(final Long id,String userName);
	/**
	 * 通过baseID查询单条申诉
	 * 
	 * @param baseID
     * @param userName
	 * @return
	 */
	public Object queryAppealByBaseID(final Long baseId,String userName);
	/**
	 * 按条件查询申诉数量
	 * 
	 * @param appeal
	 * @return
	 */
	public Object queryAppealsCount(Appeal appeal);
	/**
	 * 新增申诉
	 * 
	 * @param appeal
	 * @return
	 */
	public Object saveAppeal(Appeal appeal);
	/**
	 * 修改申诉处理状态
	 * 
	 * @param appeal
	 * @return
	 */
	public Object updateAppeal(Appeal appeal);
	
	/**
	 * 修改能否申诉状态
	 * @param baseId
	 * @param userName
	 * @return
	 */
	public Object updateAppealTask(Long baseId,String userName);
	
	/**
	 * 通过baseId查询任务
	 * @param baseID
	 * @param userName
	 * @return
	 */
	public Object queryAppealTask(Long baseId,String userName);

}
