package com.autonavi.collect.bean;

public class CollectShapeInfo implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1360974398827771213L;
	private Long id;
	private String shapeName;
	private String shapePath;
	private Integer status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getShapeName() {
		return shapeName;
	}
	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}
	public String getShapePath() {
		return shapePath;
	}
	public void setShapePath(String shapePath) {
		this.shapePath = shapePath;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
