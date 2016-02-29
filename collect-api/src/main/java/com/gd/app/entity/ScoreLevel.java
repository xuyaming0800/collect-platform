package com.gd.app.entity;

import java.io.Serializable;

public class ScoreLevel implements Serializable {
	private static final long serialVersionUID = 396267029026431771L;
	private Long id;
	private String name;
	private String score;
	private String price;
	private String isPassive;
	private String status;
	private Long min;
	private Long max;
	
	
	public Long getMin() {
		return min;
	}
	public void setMin(Long min) {
		this.min = min;
	}
	public Long getMax() {
		return max;
	}
	public void setMax(Long max) {
		this.max = max;
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
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getIsPassive() {
		return isPassive;
	}
	public void setIsPassive(String isPassive) {
		this.isPassive = isPassive;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
