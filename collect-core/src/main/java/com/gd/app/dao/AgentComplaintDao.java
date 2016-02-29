package com.gd.app.dao;

public interface AgentComplaintDao {
	/**
	 * 新增用户反馈信息
	 * @param userName
	 * @param phone
	 * @param content
	 * @return
	 */
	public Object addAgentComplaint(String userName, String phone, String content);
	/**
	 *  新增用户反馈信息
	 * @param userName
	 * @param phone
	 * @param content
	 * @param deviceSys
	 * @param deviceModel
	 * @return
	 */
	public Object addAgentComplaint(String userName, String phone, String content,String deviceSys,String deviceModel);

}
