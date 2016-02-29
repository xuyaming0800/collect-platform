package com.autonavi.collect.entity;

import java.io.Serializable;

import com.autonavi.collect.exception.BusinessExceptionEnum;
/**
 * 任务提交返回类型
 * @author xuyaming
 *
 */
public class CollectTaskSaveReturnEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6580810251264880777L;
	private Integer status;
	private BusinessExceptionEnum sqlExpEnum;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public BusinessExceptionEnum getSqlExpEnum() {
		return sqlExpEnum;
	}
	public void setSqlExpEnum(BusinessExceptionEnum sqlExpEnum) {
		this.sqlExpEnum = sqlExpEnum;
	}
	
	
	

}
