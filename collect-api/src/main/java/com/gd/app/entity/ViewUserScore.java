package com.gd.app.entity;

public class ViewUserScore {
	private String userName;
	private String score;
	private String ename;
	private String ecode;
	private String agentType;
	private String baseId;
	
	
	public String getEname() {
		return ename;
	}
	public void setEname(String ename) {
		this.ename = ename;
	}
	public String getEcode() {
		return ecode;
	}
	public void setEcode(String ecode) {
		this.ecode = ecode;
	}
	public String getBaseId() {
		return baseId;
	}
	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public String toString(){
		return this.ename+":"+this.ecode+":"+this.agentType;
	}
	
	

}
