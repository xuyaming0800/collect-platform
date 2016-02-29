package com.autonavi.collect.web.bean;

public class ResultEntity {
	

	private ResultDesc status;

	private Object result;
	
	private String totalCount;
	
	private String enterPriseName;
	
	private String totalScore;
	
	private String verifyPassCount;
	
	private String verifyFailCount;
	
	private Boolean isNewToken;
	
	

	public ResultDesc getStatus() {
		return status;
	}

	public void setStatus(ResultDesc status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object resultData) {
		this.result = resultData;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

    public String getEnterPriseName() {
        return enterPriseName;
    }

    public void setEnterPriseName(String enterPriseName) {
        this.enterPriseName = enterPriseName;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

	public String getVerifyPassCount() {
		return verifyPassCount;
	}

	public void setVerifyPassCount(String verifyPassCount) {
		this.verifyPassCount = verifyPassCount;
	}

	public String getVerifyFailCount() {
		return verifyFailCount;
	}

	public void setVerifyFailCount(String verifyFailCount) {
		this.verifyFailCount = verifyFailCount;
	}

	public Boolean getIsNewToken() {
		return isNewToken;
	}

	public void setIsNewToken(Boolean isNewToken) {
		this.isNewToken = isNewToken;
	}
	
    
    


}
