package com.gd.app.entity;

import java.util.ArrayList;
import java.util.List;

public class WaterImgMessageEntity {
	private String taskId;
	private String userId;
	private List<String> imgUrls=new ArrayList<String>();

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
