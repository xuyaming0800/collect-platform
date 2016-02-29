package com.autonavi.collect.exception;

public enum BusinessExceptionEnum {
	DB_ERROR("200", "数据库操作异常"),

	PARAM_IS_NULL("201", "参数为空"),

	QUERY_ERROR("202", "查询异常"),

	PARAM_X_RADIUS("300", "半径或经纬度格式不正确"),

	INSERT_AGENT_TASK_ERROR("101", "系统异常,请重新提交"),

	PRASE_IMAGE_ERROR("100", "解析上传图片异常"),

	IMAGE_TYPE_ERROR("125", "上传图片为非JPG格式"),

	PARAM_FORMAT_EXP("400", "请求参数格式错误"),

	PAGE_QUERY_ERROR("401", "系统内部异常"),

	USER_TASK_COMMITED("102", "任务不能重复提交!"),

	NOT_CSV_FILE("103", "上传的文件不是csv文件"),

	COORDINATE_ERROR("104", "x,y坐标值不正确"),

	ILLEGAL_WORDS("105", "含有非法关键字"),

	PRASE_UPLOAD_FILE_ERROR("106", "解析上传文件异常"),

	UPLOAD_FILE_HEADER_ERROR("107", "文件头格式错误"),

	UPLOAD_FILE_CONTENT_ERROR("107", "上传文件内容格式错误"),

	TASK_INSET_TO_DB_ERROR("108", "任务入库失败,稍后上传"),

	REPEAT_RECORDED("100032", "重复积分录入"),

	RECORD_FALIED("100033", "积分录入异常"),

	NETWORK_ERROR("109", "网络调用异常,稍后再试"),

	AUTO_TASK_NOT_SAVE("110", "自由门址不能离线锁定"),

	TASK_HAD_LOCKED("111", "任务已被其他用户离线锁定"),

	UNLOCK_TASK_ERROR("112", "任务释放异常"),

	TASK_HAD_COMMITED("113", "任务已被其他用户提交"),

	TASK_IS_EXISTS("114", "任务已存在"),

	UPDATE_TASK_PACKAGE_ERROR("115", "任务包更新失败"),

	PACKAGE_IS_REAUDITING("116", "任务已经在复审中"),

	PACKAGE_IS_INVALID("117", "任务包已失败"),

	PACKAGE_RE_AUDIT_ERROR("118", "任务复审失败"),

	PACKAGE_IS_COMMITED("119", "任务包已被他人复查提交"),

	PACKAGE_RE_CHECK_ERROR("120", "任务包复查更新失败"),

	IMAGE_PACKAGE_LOSTED("121", "图片包丢失"),

	PACKAGE_NOT_CHECKED("122", "任务包没有被验收"),

	PACKAGE_CONTAIN_ERROR_FLAG("123", "任务包含有标记错误的任务"),

	PACKAGE_CANNOT_RECHECKED("124", "任务包不能被退回"),

	MD5_VALIDATE_IMAGE_ERROR("402", "图片MD5校验错误"),

	FETCH_ADCODE_ERROR("403", "获取adCode异常"),

	TASK_VALIDATE_ERROR("405", "任务非法"),

	IMAGE_INFO_ERROR("406", "图片文件与图片描述信息未对应"), SCORE_INFO_NOT_FOUND("407",
			"积分或许金额信息未找到"),

	DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR("500", "被动任务-短时间内不要频繁提交数据"),

	DUPLICATE_ACTIVE_TASK_UPLOAD_ERROR("501", "主动任务-短时间内不要频繁提交数据"),

	REPEAT_PASSIVE_TASK_SAVE_ERROR("502", "被动任务-短时间内不要频繁保存数据"),

	DUPLICATE_ACTIVE_TASK_SAVE_ERROR("503", "主动任务-短时间内不要频繁保存数据"),

	ONLY_NUMERIC_ERROR("504", "任务名称不能为纯数字"),

	REPEAT_PASSIVE_TASK_RECEIVE_ERROR("505", "被动任务-短时间内不要频繁领取相同任务"), TASK_QUERY_MAX_SIZE_OVER(
			"506", "超过查询的每页最大任务数"), TASK_QUERY_STATUS_ERROR("507", "任务查询状态错误"), TASK_QUERY_ERROR(
			"508", "任务查询错误"), TASK_QUERY_MAX_RADIS_OVER("509", "超过最大周边范围"),

	REDIS_NOT_AVAILABLE_ERROR("600", "REDIS服务不可用"),

	PARAMETERS_IS_NULL_ERROR("601", "必须的参数为空"),

	ACCESS_TO_FREQUENTLY_ERROR("602", "访问过于频繁，请稍后再试"),

	REDIS_STORE_ERROR("603", "REDIS存储数据失败"), SEARCH_ENGINE_ERROR("604",
			"搜索排重引擎异常"), ANALYZ_SEARCH_RESULT_ERROR("605", "解析搜索排重返回的数据失败"),

	TASK_CANNOT_MODIFY("700", "任务已经提交不能再次编辑"), TASK_ALREADY_SAVE("701",
			"保存失败，您已保存过此任务"), TASK_CANNOT_SUBMIT("702", "任务名称与原有名称不符"), TASK_ALREADY_SUBMIT(
			"703", "任务已经提交"), TASK_ALREADY_NOTFOUND("704", "任务已经缺失提交"), TASK_CANNOT_RELEASE(
			"705", "任务不能释放"),

	PAGE_CSV_ERROR("800", "生成导出文件错误"), TASK_TOKEN_ERROR("801", "生成TOKEN异常"), TASK_TOKEN_IN_EXPIRE(
			"802", "TOKEN获取时间间隔过短"), TASK_TOKEN_INFO_NULL("803", "TOKEN信息为空"), TAIL_SAVE_ERROR(
			"810", "上传轨迹异常"),TASK_TOKEN_STATUS_ERROR("804", "TOKEN状态错误,Token不可用"),

	SECURECODE_GEN_ERROR("820", "验证码生产出现错误"), SECURECODE_NEED("822", "需要验证码"), SECURECODE_INVALID(
			"821", "验证码校验错误"),

	APPEAL_QUERY_ERROR("900", "用户申诉查询失败"), APPEAL_UPDATE_ERROR("901",
			"用户申诉提交失败"), APPEAL_TASK_ERROR("902", "申诉任务细节查询失败"), APPEAL_TASK_UPDATE_ERROR(
			"903", "申诉任务状态修改失败"),

	REDIS_EXCEPTION("904", "Redis出现问题"), TASK_ID_ERROR("905", "任务ID不存在或者不唯一"), ALLOT_USER_UNEQUAL(
			"906", "分配用户与实际采集用户不匹配"), COLLECT_USER_UNEQUAL("907",
			"实际采集用户与本次实际采集用户不匹配"), TASK_INSERT("908", ""), TASK_UPDATE("909",
			""), TASK_REPEAT("910", "任务必须唯一"), TASK_ALREADY_FINISH("911",
			"任务审核通过"), LOGIC_ERROR("912", "逻辑未覆盖"), TASK_NOT_EXIST("913",
			"任务不存在"), TASK_FREEZE("914", "任务已冻结"), TASK_MUST_HAVE_ID("915",
			"任务已经分配或者已经保存,必须提供任务主键"), TASK_BASE_ID_NOT_FOUND("916", "任务表主键未找到"), TASK_MUST_NOT_HAVE_ID(
			"917", "任务未分配或未保存,不能存在任务主键"), TASK_MUST_RECEIVE("918", "任务未领取"), TASK_CANNOT_RECEIVE(
			"919", "此任务不可领取"), TASK_PKG_CANNOT_SUBMIT("920", "此任务包不可提交"), TASK_PKG_TASK_MUST_SUBMIT(
			"921", "任务包下有任务没有提交"), USERID_IS_NULL("930", "非法UserId"), TASK_RECEIVE_ERROR(
			"931", "任务领取出现错误"), TASK_OUT_QUEUE_ERROR("932", "任务审核消息队列错误"), TASK_IN_QUEUE_ERROR(
			"933", "任务审核回传消息队列错误"), TASK_OR_PKG_TIME_OUT("934", "任务或者任务包超时"), TASK_OR_PKG_STATUS_ERROR(
			"935", "任务或者任务包状态错误"),TASK_DELETE_ERROR(
					"936", "任务删除错误"),TASK_ALREADY_FIRST_AUDIT("937",
							"任务已经审核"),TASK_NOT_FIRST_AUDIT("938",
									"任务不是初审状态"),TASK_NOT_FIRST_AUDIT_FAIL("939",
											"任务不是初审无效状态"),
	TASK_REAUDIT_FAIL("940","任务申诉失败"),
	TASK_IS_NOT_ALLOW_FOR_OWNER("941","任务的业主-品类错误"),

	TASK_IS_NOT_INITIATIVE("942", "任务不是主动任务"), TASK_MUST_SAVE("943",
			"任务必须是已经保存状态"),
	TASK_IS_NOT_PASSIVE("944", "任务不是被动任务"),
	TASK_RELEASE_ERROR("945", "任务释放错误"),
	TASK_ALREADY_TIME_OUT("946", "任务已经超时"),
	
	TASK_CLAZZ_IS_CHANGE("947","任务类别已经发生变化"),
	
	TASK_STEP_IS_SAVED("948","任务步骤已经新建保存成功，请返回查看步骤列表"),

	SEARCH_READ_PARAM_ERROR("2000", "索引读取参数异常"), SEARCH_QUERY_ERROR("2001",
			"索引查询异常"), ACTIVE_TASK_AROUND_SEARCH_ERROR("2002", "主动任务周边搜索异常"),

	TASK_PKG_IMPORT_SIZE_ERROR("3000", "批量导入的任务包为空或者批量过大"), TASK_DETAIL_MPORT_SIZE_ERROR(
			"3001", "批量导入的任务步骤为空或者批量过大"),

	QUERY_TASK_CLAZZ_ERROR("4000", "查询任务类别列表错误"),
	TASK_CLAZZ_NOT_FOUND("4001", "任务类别未找到"),
	TASK_CLAZZ_MODIFY_LOCKED("4002", "任务类别正在被修改"),
	TASK_CLAZZ_IS_INVALID("4003", "任务类别已经被置为无效"),
	TASK_CLAZZ_IS_VALID("4005", "任务类别已经被置为有效"),
	
	TASK_AUDIT_STATUS_ERROR("5005","审核回传任务状态异常"),
	USER_INSERT_LOCKED("5006","用户插入已经锁定"),
	
	
	CACHE_TYPE_NOT_SUPPORT("6001","缓存类型不支持"),
	BATCHID_CACHE_IS_LOCKED("6002","批次号码已经被缓存和锁定"),
	BATCHID_CACHE_IS_NOT_FOUND("6003","批次号码缓存不存在或者已经超时"),
	BATCHID_GET_IS_LOCKED("6004","不要过快获取批次号"),
	
	
	
	MONGONDB_INSERT_ERROR("7001","插入MONGONDB异常"),
	MONGONDB_QUERY_ERROR("7002","查询MONGONDB异常"),
	MONGONDB_UPDATE_ERROR("7003","更新MONGONDB异常"),
	MONGONDB_DELETE_ERROR("7004","删除MONGONDB异常"),
	;

	private String code;
	private String message;

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private BusinessExceptionEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public static String getMessage(String code) {
		for (BusinessExceptionEnum exp : values()) {
			if (exp.getCode().equals(code)) {
				return exp.getMessage();
			}
		}
		return null;
	}
}
