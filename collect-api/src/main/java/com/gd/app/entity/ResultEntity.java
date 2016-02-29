package com.gd.app.entity;

public class ResultEntity {

	private String code;

	private String desc;

	private Object resultData;
	
	private String totalCount;
	
	private String enterPriseName;
	
	private String totalScore;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Object getResultData() {
		return resultData;
	}

	public void setResultData(Object resultData) {
		this.resultData = resultData;
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


}
