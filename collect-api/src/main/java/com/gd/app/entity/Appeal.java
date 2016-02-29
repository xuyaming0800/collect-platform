package com.gd.app.entity;

/**
 * 审核申诉
 * 
 * @author jiayi.zhang
 * 
 */
public class Appeal {
	private Long id;
	private String name;
	private String content;
	private String type;
	private Integer status;
	private String remark;
	private String reason;
	private String submitTime;
	private String userName;
	private Long baseId;
	private String url;
	private String beginTime;
	private String endTime;
	
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getBaseId() {
		return baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:").append(this.id);
		sb.append("dataName:").append(this.name).append(",");
		sb.append("content:").append(this.content).append(",");
		sb.append("type:").append(this.type).append(",");
		sb.append("status:").append(this.status).append(",");
		sb.append("reason:").append(this.reason).append(",");
		sb.append("submitTime:").append(this.submitTime).append(",");
		sb.append("userName:").append(this.userName).append(",");
		sb.append("baseId:").append(this.baseId).append(",");
		sb.append("url:").append(this.getUrl());
		return sb.toString();
	}

}
