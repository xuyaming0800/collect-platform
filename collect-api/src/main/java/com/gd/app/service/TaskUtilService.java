package com.gd.app.service;

import java.util.Map;

public interface TaskUtilService {
	/**
	 * 检测地址围栏
	 * @param corrX
	 * @param corrY
	 * @return
	 */
	public boolean checkLocationVaild(double corrX, double corrY);
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
	 * 生成验证码和输出图片
	 * @return
	 */
	public Map<String,Object> generateSecureCode();
	/**
	 * 获取系统时间
	 * @return
	 */
	public String querySystemDateTime();
	 /**
     * 收集应用错误信息
     * 
     * @param phone
     * @param userName
     * @param content
     */
    public void saveErrorInfo(String userName,String type, String version,String deviceInfo, String erCode,String erInfo);
    /**
     * 获取属性
     * @param key
     * @return
     */
    public String getProperty(String key);

}
