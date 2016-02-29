package com.autonavi.collect.bean;



public class CollectTaskToken implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 359533547402801701L;
	// Fields

	private Long id;
	private Long baseId;
	private Long createTime;
	private Long updateTime;
	private String token;
	private Long userId;
	private String attachmentMd5;
	private String header;
	private Double tokenX;
	private Double tokenY;
	private Integer tokenStatus;
	private String ip;
	
	private Long ownerId;

	// Constructors

	/** default constructor */
	public CollectTaskToken() {
	}

	/** minimal constructor */
	public CollectTaskToken(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectTaskToken(Long id, Long baseId, Long createTime,
			Long updateTime, String token, Long userId, String attachmentMd5,
			String header, Double tokenX, Double tokenY, Integer tokenStatus,String ip) {
		this.id = id;
		this.baseId = baseId;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.token = token;
		this.userId = userId;
		this.attachmentMd5 = attachmentMd5;
		this.header = header;
		this.tokenX = tokenX;
		this.tokenY = tokenY;
		this.tokenStatus = tokenStatus;
		this.ip=ip;
	}

	// Property accessors
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBaseId() {
		return this.baseId;
	}

	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}

	public Long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAttachmentMd5() {
		return this.attachmentMd5;
	}

	public void setAttachmentMd5(String attachmentMd5) {
		this.attachmentMd5 = attachmentMd5;
	}

	public String getHeader() {
		return this.header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Double getTokenX() {
		return this.tokenX;
	}

	public void setTokenX(Double tokenX) {
		this.tokenX = tokenX;
	}

	public Double getTokenY() {
		return this.tokenY;
	}

	public void setTokenY(Double tokenY) {
		this.tokenY = tokenY;
	}

	public Integer getTokenStatus() {
		return this.tokenStatus;
	}

	public void setTokenStatus(Integer tokenStatus) {
		this.tokenStatus = tokenStatus;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	

}