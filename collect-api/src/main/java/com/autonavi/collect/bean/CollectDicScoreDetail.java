package com.autonavi.collect.bean;


public class CollectDicScoreDetail implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -4560413102318703773L;
	private Long id;
	private String value;
	private Integer taskType;
	private Integer scoreType;
	private Integer status;

	// Constructors

	/** default constructor */
	public CollectDicScoreDetail() {
	}

	/** minimal constructor */
	public CollectDicScoreDetail(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectDicScoreDetail(Long id, String value, Integer taskType,
			Integer scoreType, Integer status) {
		this.id = id;
		this.value = value;
		this.taskType = taskType;
		this.scoreType = scoreType;
		this.status = status;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getTaskType() {
		return this.taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getScoreType() {
		return this.scoreType;
	}

	public void setScoreType(Integer scoreType) {
		this.scoreType = scoreType;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}