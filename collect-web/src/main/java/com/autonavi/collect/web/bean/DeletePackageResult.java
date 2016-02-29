package com.autonavi.collect.web.bean;

import java.util.ArrayList;
import java.util.List;

public class DeletePackageResult {
	List<Long> successId=new ArrayList<Long>();
	List<Long> failedId=new ArrayList<Long>();
	public List<Long> getSuccessId() {
		return successId;
	}
	public void setSuccessId(List<Long> successId) {
		this.successId = successId;
	}
	public List<Long> getFailedId() {
		return failedId;
	}
	public void setFailedId(List<Long> failedId) {
		this.failedId = failedId;
	}

}
