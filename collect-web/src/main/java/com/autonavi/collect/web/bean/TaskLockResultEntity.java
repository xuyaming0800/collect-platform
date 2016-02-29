package com.autonavi.collect.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.autonavi.collect.entity.TaskExtraInfoEntity;


public class TaskLockResultEntity implements Serializable {
	private static final long serialVersionUID = -532698892329174729L;

	private String taskId;

    private String userName;

    private String submitTime;

    private String endTime;
    
    private String dataName;
    
    private String collectDataName;
    
    private String baseId;
    
    private String adCode;
    
	private String status;
	
	private String x;
	
	private String y;
	
	private String addressType;
	
	private String canAppeal;
	
	private String basePackageId;
	
	private String tokenId;
	
	private List<TaskExtraInfoEntity> extras=new ArrayList<TaskExtraInfoEntity>();
	
	
	
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

	

	public static void main(String[] args) {
	}

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public String getCanAppeal() {
		return canAppeal;
	}

	public void setCanAppeal(String canAppeal) {
		this.canAppeal = canAppeal;
	}

	public String getBasePackageId() {
		return basePackageId;
	}

	public void setBasePackageId(String basePackageId) {
		this.basePackageId = basePackageId;
	}

	public String getCollectDataName() {
		return collectDataName;
	}

	public void setCollectDataName(String collectDataName) {
		this.collectDataName = collectDataName;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public List<TaskExtraInfoEntity> getExtras() {
		return extras;
	}

	public void setExtras(List<TaskExtraInfoEntity> extras) {
		this.extras = extras;
	}

	
	
	

}
