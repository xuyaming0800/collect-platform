package com.gd.app.exception;

public enum AppExceptionEnum
{
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

  DUPLICATE_PASSIVE_TASK_UPLOAD_ERROR("500", "被动任务-短时间内不要频繁提交数据"), 

  DUPLICATE_ACTIVE_TASK_UPLOAD_ERROR("501", "主动任务-短时间内不要频繁提交数据"), 

  DUPLICATE_PASSIVE_TASK_SAVE_ERROR("502", "被动任务-短时间内不要频繁保存数据"), 

  DUPLICATE_ACTIVE_TASK_SAVE_ERROR("503", "主动任务-短时间内不要频繁保存数据"), 

  ONLY_NUMERIC_ERROR("504", "任务名称不能为纯数字"), 

  REDIS_NOT_AVAILABLE_ERROR("600", "REDIS服务不可用"), 

  PARAMETERS_IS_NULL_ERROR("601", "必须的参数为空"), 

  ACCESS_TO_FREQUENTLY_ERROR("602", "访问过于频繁，请稍后再试"), 

  REDIS_STORE_ERROR("603", "REDIS存储数据失败"), 
  SEARCH_ENGINE_ERROR("604","搜索排重引擎异常"),
  ANALYZ_SEARCH_RESULT_ERROR("605","解析搜索排重返回的数据失败"),
  
  TASK_CANNOT_MODIFY("700", "任务已经提交不能再次编辑"), 
  TASK_CANNOT_SAVE("701", "保存失败，您已保存过此任务"), 
  TASK_CANNOT_SUBMIT("702", "任务名称与原有名称不符"), 
  TASK_HAVE_SUBMIT("703", "任务已经提交"), 

  PAGE_CSV_ERROR("800", "生成导出文件错误"), 
  TASK_TOKEN_ERROR("801", "生成TOKEN异常"),
  TASK_TOKEN_IN_EXPIRE("802", "TOKEN获取时间间隔过短"),
  TAIL_SAVE_ERROR("810","上传轨迹异常"),
  
  SECURECODE_GEN_ERROR("820","验证码生产出现错误"),
  SECURECODE_NEED("822","需要验证码"),
  SECURECODE_INVALID("821","验证码校验错误"),
  
  APPEAL_QUERY_ERROR("900","用户申诉查询失败"),
  APPEAL_UPDATE_ERROR("901","用户申诉提交失败"),
  APPEAL_TASK_ERROR("902","申诉任务细节查询失败"),
  APPEAL_TASK_UPDATE_ERROR("903","申诉任务状态修改失败");
  
  private String code;
  private String message;

  public String getCode() { return this.code; }

  public void setCode(String code)
  {
    this.code = code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  private AppExceptionEnum(String code, String message)
  {
    this.code = code;
    this.message = message;
  }

  public static String getMessage(String code)
  {
    for (AppExceptionEnum exp : values()) {
      if (exp.getCode().equals(code)) {
        return exp.getMessage();
      }
    }
    return null;
  }
}