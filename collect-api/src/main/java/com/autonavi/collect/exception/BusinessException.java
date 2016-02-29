package com.autonavi.collect.exception;

public class BusinessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5636326402890518248L;

	// error code
	private int errorCode = -1;

	// error message
	private String errorMessage = null;

	private BusinessExceptionEnum sqlExpEnum;

	public BusinessException(String message) {
		super(message);
	}
	public BusinessException(String message, Throwable cause) {
		super(message,cause);
	}

	/**
	 * 构造函数说明： 
	 * @param errorCode
	 * @param errorMessage
	 * @param sqlExpEnum
	 */
	public BusinessException(int errorCode, String errorMessage, BusinessExceptionEnum sqlExpEnum) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.sqlExpEnum = sqlExpEnum;
	}
	/**
	 * 新增构造函数-许亚明-2013-10-29
	 * @param errorCode
	 * @param errorMessage
	 */
	public BusinessException(int errorCode, String errorMessage) {
		this.sqlExpEnum=BusinessExceptionEnum.PARAM_IS_NULL;
		this.sqlExpEnum.setCode(errorCode+"");
		this.sqlExpEnum.setMessage(errorMessage);
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}

	public BusinessException(BusinessExceptionEnum sqlExpEnum) {
		super(sqlExpEnum.getMessage());
		this.sqlExpEnum = sqlExpEnum;
	}

	public BusinessException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setErrorCode(errorCode);
		this.setErrorMessage(message);
	}

	public BusinessExceptionEnum getSqlExpEnum() {
		return this.sqlExpEnum;
	}

	public void setSqlExpEnum(BusinessExceptionEnum sqlExpEnum) {
		this.sqlExpEnum = sqlExpEnum;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
