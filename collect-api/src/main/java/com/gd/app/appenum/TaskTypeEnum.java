package com.gd.app.appenum;

public enum TaskTypeEnum {
	DOOR_ADRESS("0", "门址"), 
	ROAD("1", "道路");

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
	private TaskTypeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	// 获取信息
	public static String getMessageByCode(String code) {
		for (TaskTypeEnum exp : TaskTypeEnum.values()) {
			if (exp.getCode().equals(code)) {
				return exp.getMessage();
			}
		}
		return "";
	}

	// 获取code
	public static String getCodeByMessage(String message) {
		if (message.equals("") || message == null)
			return "";

		for (TaskTypeEnum task : TaskTypeEnum.values()) {
			if (message.startsWith(task.getMessage())) {
				return task.getCode();
			}
		}
		return "";
	}
}
