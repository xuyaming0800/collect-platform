package com.autonavi.collect.bean;

public class CollectTaskClazzShape implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4511270341783995732L;
	private Long id;
	private Long taskClazzId;
	private Long shapeId;
	private Long ownerId;
	private Integer status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTaskClazzId() {
		return taskClazzId;
	}
	public void setTaskClazzId(Long taskClazzId) {
		this.taskClazzId = taskClazzId;
	}
	public Long getShapeId() {
		return shapeId;
	}
	public void setShapeId(Long shapeId) {
		this.shapeId = shapeId;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
