package com.gd.app.exception;

public class SearchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// error code
	private int errorCode = -1;

	// error message
	private String errorMessage = null;

	private AppExceptionEnum sqlExpEnum;

	public SearchException() {
		super();
	}

	/**
	 * 构造函数说明： 
	 * @param errorCode
	 * @param errorMessage
	 * @param sqlExpEnum
	 */
	public SearchException(int errorCode, String errorMessage, AppExceptionEnum sqlExpEnum) {
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
	public SearchException(int errorCode, String errorMessage) {
		this.sqlExpEnum=AppExceptionEnum.PARAM_IS_NULL;
		this.sqlExpEnum.setCode(errorCode+"");
		this.sqlExpEnum.setMessage(errorMessage);
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}

	public SearchException(AppExceptionEnum sqlExpEnum) {
		super(sqlExpEnum.getMessage());
		this.sqlExpEnum = sqlExpEnum;
	}

	public SearchException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setErrorCode(errorCode);
		this.setErrorMessage(message);
	}

	public AppExceptionEnum getSqlExpEnum() {
		return this.sqlExpEnum;
	}

	public void setSqlExpEnum(AppExceptionEnum sqlExpEnum) {
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
