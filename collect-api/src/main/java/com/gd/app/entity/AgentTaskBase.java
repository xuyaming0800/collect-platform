package com.gd.app.entity;

/**
 * 后台用户任务实体
 * 
 * @author Administrator
 * 
 */
public class AgentTaskBase {
	private Long id;
	
	private Integer status;
	
	private String adCode;
	
	private String regDataname;
	
	private Long timestamps;
	
	private String deviceInfo;
	
	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	private Long scoreId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAdCode() {
		return adCode;
	}

	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}

	public String getRegDataname() {
		return regDataname;
	}

	public void setRegDataname(String regDataname) {
		this.regDataname = regDataname;
	}

	public Long getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(Long timestamps) {
		this.timestamps = timestamps;
	}


	public Long getScoreId() {
		return scoreId;
	}

	public void setScoreId(Long scoreId) {
		this.scoreId = scoreId;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	private String taskId;

	private String taskType;

	private String taskStatus;

	private String submitTime;
	
	private String endTime;

	private String dataName;

	private int score;

	private String userName;

	private String x;

	private String y;

	private String imageId;
	
	private String desc;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
