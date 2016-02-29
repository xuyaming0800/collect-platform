package com.gd.app.entity;

import java.io.Serializable;

public class TaskAppEntity implements Serializable {
	private static final long serialVersionUID = 3470616885105446935L;
	private long   id;
    private String dataname;
    private int    tasktype;
    private String submitStatus;
    private String poi;
    private String prename;
    private String score;
    private long total;
    private String scoreId;
    
    
        
    
    
    
    
    


    public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getScoreId() {
		return scoreId;
	}

	public void setScoreId(String scoreId) {
		this.scoreId = scoreId;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		//this.poi = poi;
		//暂时屏蔽poi
		this.poi="";
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataname() {
        return dataname;
    }

    public void setDataname(String dataname) {
        this.dataname = dataname;
    }

    public int getTasktype() {
        return tasktype;
    }

    public void setTasktype(int tasktype) {
        this.tasktype = tasktype;
    }

    public String getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(String submitStatus) {
        this.submitStatus = submitStatus;
    }
}
