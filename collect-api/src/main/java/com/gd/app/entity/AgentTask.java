package com.gd.app.entity;

/**
 * 代理商任务实体
 * 
 * @author Administrator
 * 
 */
public class AgentTask {

	private String taskid;

	private String tasktype;

	private String taskstatus;

	private String submittime;

	private String dataname;

	private String score;
	
	private int total;
	
	private String desc;
	
	private int canAppeal;
	
	private String baseid;

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getTasktype() {
		return tasktype;
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}

	public String getTaskstatus() {
		return taskstatus;
	}

	public void setTaskstatus(String taskstatus) {
		this.taskstatus = taskstatus;
	}

	public String getSubmittime() {
		return submittime;
	}

	public void setSubmittime(String submittime) {
		this.submittime = submittime;
	}

	public String getDataname() {
		return dataname;
	}

	public void setDataname(String dataname) {
		this.dataname = dataname;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

	public int getCanAppeal() {
		return canAppeal;
	}

	public void setCanAppeal(int canAppeal) {
		this.canAppeal = canAppeal;
	}

	public String getBaseid() {
		return baseid;
	}

	public void setBaseid(String baseid) {
		this.baseid = baseid;
	}
}
