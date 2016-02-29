package com.gd.app.appenum;



public enum AuditStatusEnum {
	AUDIT_BEFORE("0", "待审核"), 
	AUDIT_PASS("1", "有效"), 
	AUDIT_NO("2", "无效"),
	SAVING("3","保存中");

	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// 构造方法
	private AuditStatusEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	// 获取信息
	public static String getMessageByCode(String code) {
		for (AuditStatusEnum exp : AuditStatusEnum.values()) {
			if (exp.getCode().equals(code)) {
				return exp.getMessage();
			}
		}
		return "";
	}

	// 获取code
	public static String getCodeByMessage(String message) {
		if (message.equals("") || message == null)
			return "0";

		for (AuditStatusEnum task : AuditStatusEnum.values()) {
			if (message.equals(task.getMessage())) {
				return task.getCode();
			}
		}
		return "0";
	}
}
