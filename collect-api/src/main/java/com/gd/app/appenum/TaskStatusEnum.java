package com.gd.app.appenum;

public enum TaskStatusEnum {
	DEL("-1", "逻辑删除"),
	RECOMMEND("-3", "定向任务"),
	INIT("0", "任务初始状态"), 
	FINISHED("1", "任务已审核通过"),
	SAVING("2","任务被保存"),
	COMMITED("3","任务被提交");

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
	private TaskStatusEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	// 获取信息
	public static String getMessageByCode(String code) {
		for (TaskStatusEnum exp : TaskStatusEnum.values()) {
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

		for (TaskStatusEnum task : TaskStatusEnum.values()) {
			if (message.startsWith(task.getMessage())) {
				return task.getCode();
			}
		}
		return "";
	}
}
