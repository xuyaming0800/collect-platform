package com.autonavi.collect.entity;

/**
 * 带送审的采集信息封装
 * 
 * @author xuyaming
 *
 */
public class CollectToAuditEntity {
	private Long pid;
	private String pName;
	private String pDesc;
	private Integer pCount;
	private Double pay;
	private Long bid;
	private Integer status;
	private String bName;
	private String bCollectName;
	private Long submitTime;
	private Long userId;
	private Long gpsTime;
	private Long photoTime;
	private Double x;
	private Double y;
	private Double accuracy;
	private Long cid;
	private Integer levels;
	private Double position;
	private String imageName;
	private Integer mTime;
	private Integer fTime;
	private Long taskClazzId;
	private Integer imageIndex;
	private Long ownerId;
	
	
	
   

	public Long getTaskClazzId() {
		return taskClazzId;
	}

	public void setTaskClazzId(Long taskClazzId) {
		this.taskClazzId = taskClazzId;
	}

	public Integer getfTime() {
		return fTime;
	}

	public void setfTime(Integer fTime) {
		this.fTime = fTime;
	}

	public Integer getpCount() {
		return pCount;
	}

	public void setpCount(Integer pCount) {
		this.pCount = pCount;
	}

	public Double getPay() {
		return pay;
	}

	public void setPay(Double pay) {
		this.pay = pay;
	}

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}

	public Integer getmTime() {
		return mTime;
	}

	public void setmTime(Integer mTime) {
		this.mTime = mTime;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getpDesc() {
		return pDesc;
	}

	public void setpDesc(String pDesc) {
		this.pDesc = pDesc;
	}

	public Long getBid() {
		return bid;
	}

	public void setBid(Long bid) {
		this.bid = bid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getbName() {
		return bName;
	}

	public void setbName(String bName) {
		this.bName = bName;
	}

	public String getbCollectName() {
		return bCollectName;
	}

	public void setbCollectName(String bCollectName) {
		this.bCollectName = bCollectName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(Long gpsTime) {
		this.gpsTime = gpsTime;
	}

	public Long getPhotoTime() {
		return photoTime;
	}

	public void setPhotoTime(Long photoTime) {
		this.photoTime = photoTime;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Integer getLevels() {
		return levels;
	}

	public void setLevels(Integer levels) {
		this.levels = levels;
	}

	public Double getPosition() {
		return position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Integer getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(Integer imageIndex) {
		this.imageIndex = imageIndex;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	

}
