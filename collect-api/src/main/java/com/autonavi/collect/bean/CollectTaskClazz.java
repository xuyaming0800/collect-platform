package com.autonavi.collect.bean;


public class CollectTaskClazz implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -885883913592984481L;
	private Long id;
	private Long parentId=null;
	private Long initClazzId=null;
	private Integer taskType=null;
	private Integer clazzType=null;
	private String clazzName;
	private Double clazzPay=0.0D;
	private Integer clazzPayType;
	private Integer clazzImgCount=null;
	private Integer clazzNearImgCount=null;
	private Integer clazzFarImgCount=null;
	private String clazzDesc;
	private Integer clazzStatus=null;
	private Integer clazzDistance=null;
	private Integer clazzIndex=null;
	private Long ownerId=null;

	// Constructors

	/** default constructor */
	public CollectTaskClazz() {
	}

	/** full constructor */
	public CollectTaskClazz(Long parentId, Integer taskType, Integer clazzType,
			String clazzName, Integer clazzImgCount, String clazzDesc,
			Integer clazzStatus) {
		this.parentId = parentId;
		this.taskType = taskType;
		this.clazzType = clazzType;
		this.clazzName = clazzName;
		this.clazzImgCount = clazzImgCount;
		this.clazzDesc = clazzDesc;
		this.clazzStatus = clazzStatus;
	}

	// Property accessors
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getTaskType() {
		return this.taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}


	public Integer getClazzType() {
		return this.clazzType;
	}

	public void setClazzType(Integer clazzType) {
		this.clazzType = clazzType;
	}

	public String getClazzName() {
		return this.clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public Integer getClazzImgCount() {
		return this.clazzImgCount;
	}

	public void setClazzImgCount(Integer clazzImgCount) {
		this.clazzImgCount = clazzImgCount;
	}

	public String getClazzDesc() {
		return this.clazzDesc;
	}

	public void setClazzDesc(String clazzDesc) {
		this.clazzDesc = clazzDesc;
	}

	public Integer getClazzStatus() {
		return this.clazzStatus;
	}

	public void setClazzStatus(Integer clazzStatus) {
		this.clazzStatus = clazzStatus;
	}

	public Double getClazzPay() {
		return clazzPay;
	}

	public void setClazzPay(Double clazzPay) {
		this.clazzPay = clazzPay;
	}

	public Integer getClazzNearImgCount() {
		return clazzNearImgCount;
	}

	public void setClazzNearImgCount(Integer clazzNearImgCount) {
		this.clazzNearImgCount = clazzNearImgCount;
	}

	public Integer getClazzFarImgCount() {
		return clazzFarImgCount;
	}

	public void setClazzFarImgCount(Integer clazzFarImgCount) {
		this.clazzFarImgCount = clazzFarImgCount;
	}

	public Integer getClazzDistance() {
		return clazzDistance;
	}

	public void setClazzDistance(Integer clazzDistance) {
		this.clazzDistance = clazzDistance;
	}

	public Integer getClazzIndex() {
		return clazzIndex;
	}

	public void setClazzIndex(Integer clazzIndex) {
		this.clazzIndex = clazzIndex;
	}

	public Integer getClazzPayType() {
		return clazzPayType;
	}

	public void setClazzPayType(Integer clazzPayType) {
		this.clazzPayType = clazzPayType;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getInitClazzId() {
		return initClazzId;
	}

	public void setInitClazzId(Long initClazzId) {
		this.initClazzId = initClazzId;
	}
	
}