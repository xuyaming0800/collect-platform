package com.autonavi.collect.bean;



public class CollectTaskImg implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -225728306862871206L;
	private Long id;
	private Long baseId;
	private String imgName;
	private Double collectX;
	private Double collectY;
	private Integer collectAdcode;
	private Double collectOffsetX;
	private Double collectOffsetY;
	private Double gpsAccuracy;
	private Integer gpsCount;
	private Long gpsTime;
	private Integer locationType;
	private Double position;
	private Double positionX;
	private Double positionY;
	private Double positionZ;
	private Long photoTime;
	private Integer imageIndex;
	private Integer imageStatus;
	private String imageH5Id;
	private Long taskClazzId;
	private Long imageBatchId;
	private String imageFlag;
	private Long tempBatchId;
	
	//非数据库属性
	private String level;
	

	

	// Constructors

	public Integer getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(Integer imageIndex) {
		this.imageIndex = imageIndex;
	}

	public Integer getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(Integer imageStatus) {
		this.imageStatus = imageStatus;
	}

	/** default constructor */
	public CollectTaskImg() {
	}

	/** minimal constructor */
	public CollectTaskImg(Long id) {
		this.id = id;
	}

	/** full constructor */
	public CollectTaskImg(Long id, Long baseId, String imgName,
			Double collectX, Double collectY, Integer collectAdcode,
			Double collectOffsetX, Double collectOffsetY, Double gpsAccuracy,
			Integer gpsCount, Long gpsTime, Integer locationType,
			Double position, Double positionX, Double positionY,
			Double positionZ) {
		this.id = id;
		this.baseId = baseId;
		this.imgName = imgName;
		this.collectX = collectX;
		this.collectY = collectY;
		this.collectAdcode = collectAdcode;
		this.collectOffsetX = collectOffsetX;
		this.collectOffsetY = collectOffsetY;
		this.gpsAccuracy = gpsAccuracy;
		this.gpsCount = gpsCount;
		this.gpsTime = gpsTime;
		this.locationType = locationType;
		this.position = position;
		this.positionX = positionX;
		this.positionY = positionY;
		this.positionZ = positionZ;
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

	public String getImgName() {
		return this.imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public Double getCollectX() {
		return this.collectX;
	}

	public void setCollectX(Double collectX) {
		this.collectX = collectX;
	}

	public Double getCollectY() {
		return this.collectY;
	}

	public void setCollectY(Double collectY) {
		this.collectY = collectY;
	}

	public Integer getCollectAdcode() {
		return this.collectAdcode;
	}

	public void setCollectAdcode(Integer collectAdcode) {
		this.collectAdcode = collectAdcode;
	}

	public Double getCollectOffsetX() {
		return this.collectOffsetX;
	}

	public void setCollectOffsetX(Double collectOffsetX) {
		this.collectOffsetX = collectOffsetX;
	}

	public Double getCollectOffsetY() {
		return this.collectOffsetY;
	}

	public void setCollectOffsetY(Double collectOffsetY) {
		this.collectOffsetY = collectOffsetY;
	}

	public Double getGpsAccuracy() {
		return this.gpsAccuracy;
	}

	public void setGpsAccuracy(Double gpsAccuracy) {
		this.gpsAccuracy = gpsAccuracy;
	}

	public Integer getGpsCount() {
		return this.gpsCount;
	}

	public void setGpsCount(Integer gpsCount) {
		this.gpsCount = gpsCount;
	}

	public Long getGpsTime() {
		return this.gpsTime;
	}

	public void setGpsTime(Long gpsTime) {
		this.gpsTime = gpsTime;
	}

	public Integer getLocationType() {
		return this.locationType;
	}

	public void setLocationType(Integer locationType) {
		this.locationType = locationType;
	}

	public Double getPosition() {
		return this.position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public Double getPositionX() {
		return this.positionX;
	}

	public void setPositionX(Double positionX) {
		this.positionX = positionX;
	}

	public Double getPositionY() {
		return this.positionY;
	}

	public void setPositionY(Double positionY) {
		this.positionY = positionY;
	}

	public Double getPositionZ() {
		return this.positionZ;
	}

	public void setPositionZ(Double positionZ) {
		this.positionZ = positionZ;
	}

	public Long getPhotoTime() {
		return photoTime;
	}

	public void setPhotoTime(Long photoTime) {
		this.photoTime = photoTime;
	}


	public String getImageH5Id() {
		return imageH5Id;
	}

	public void setImageH5Id(String imageH5Id) {
		this.imageH5Id = imageH5Id;
	}

	public Long getTaskClazzId() {
		return taskClazzId;
	}

	public void setTaskClazzId(Long taskClazzId) {
		this.taskClazzId = taskClazzId;
	}

	public String getImageFlag() {
		return imageFlag;
	}

	public void setImageFlag(String imageFlag) {
		this.imageFlag = imageFlag;
	}

	public Long getImageBatchId() {
		return imageBatchId;
	}

	public void setImageBatchId(Long imageBatchId) {
		this.imageBatchId = imageBatchId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Long getTempBatchId() {
		return tempBatchId;
	}

	public void setTempBatchId(Long tempBatchId) {
		this.tempBatchId = tempBatchId;
	}
	
	

}