package com.gd.app.dao;

public interface AgentLocationDao {
	/**
	 * 根据ADCODE获取省市区
	 * @param adCode
	 * @return
	 */
	public Object getAgentLocationByAdcode(String adCode);
	/**
	 * 获取数据库时间
	 * @return
	 */
	public Object getSystemTime();
	/**
	 * 收集错误信息
	 * @param userName
	 * @param type
	 * @param version
	 * @param deviceInfo
	 * @param erCode
	 * @param erInfo
	 * @return
	 */
	public Object addAppErrorInfo(String userName,String type, String version,String deviceInfo, String erCode,String erInfo);

}
