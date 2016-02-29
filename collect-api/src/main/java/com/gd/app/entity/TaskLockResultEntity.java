package com.gd.app.entity;

import java.io.Serializable;


public class TaskLockResultEntity implements Serializable {
	private static final long serialVersionUID = -532698892329174729L;

	private String taskId;

    private String userName;

    private String submitTime;

    private String endTime;
    
    private String dataName;
    
    private long baseId;
    
    private String adCode;
    
	private String status;
	
	private String x;
	
	private String y;
	
	private String addressType;
	
	private int canAppeal;
	
	
	
	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
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


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdCode() {
		return adCode;
	}

	public void setAdCode(String adCode) {
		this.adCode = adCode;
	}

	public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append("[TaskLockResultEntity]:");
        sb.append("userName:"+userName);
        sb.append(",");
        sb.append("taskId="+taskId);
        sb.append(",");
        sb.append("submitTime="+submitTime);
        sb.append(",");
        sb.append("endTime="+endTime);
        sb.append(",");
        sb.append("baseId="+baseId);
        return sb.toString();
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

	public long getBaseId() {
		return baseId;
	}

	public void setBaseId(long baseId) {
		this.baseId = baseId;
	}
	
	public int getCanAppeal() {
		return canAppeal;
	}

	public void setCanAppeal(int canAppeal) {
		this.canAppeal = canAppeal;
	}

	public static void main(String[] args) {
	}

}
