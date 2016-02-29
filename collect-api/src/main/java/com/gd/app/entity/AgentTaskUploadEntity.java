package com.gd.app.entity;

import java.util.Date;

/**
 * 用户数据上传实体
 * @author Administrator
 *
 */
public class AgentTaskUploadEntity implements ServletInfoBean{
	private static final long serialVersionUID = 9112280661802980758L;
	private String taskId;
    private String taskid;
    private String taskType;
    private String tasktype;
    private String x;
    private String y;
    private String pointType;
    private String pointLevel;
    private String imageId;
    private String enterPriseCode;
    private String code;
    private String userName;
    private String dataName;
    private String dataname;
    private Date   photoTime;
    private Date   gpsTime;
    private String addressType;
    private String adCode;
    private String terminaFlag;   //只有html5才会有此参数
    private String pointAccury;
    private String position;
    private String md5Validate;
    private String baseId;  
    private String coordtype; //判断是否对Gps坐标偏转  0:代表偏转后 1：代表未偏转
    private String tokenId;
	private String ename;
	private String ecode;
	private String agentType;
	private String deviceInfo;
	private String scoreId;
	private String regDataName;
	private String relatedLostId;
	private String comments;
	
	
	private String positionX; //方位角
	private String positionY;
	private String positionZ;
	
	private String offsetX;
	private String offsetY;
	
	
	
	private String errorMessage;
	private String recommend="0";
	
	private String isLocate="0";
	private String isBatch="1";
	private String charset="utf-8";
	private String pack;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(String offsetX) {
		this.offsetX = offsetX;
	}

	public String getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(String offsetY) {
		this.offsetY = offsetY;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRelatedLostId() {
		return relatedLostId;
	}

	public void setRelatedLostId(String relatedLostId) {
		this.relatedLostId = relatedLostId;
	}

	public String getRegDataName() {
		return regDataName;
	}

	public void setRegDataName(String regDataName) {
		this.regDataName = regDataName;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		this.enterPriseCode=code;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
		this.taskId = taskid;
	}

	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
		this.taskType = tasktype;
	}

	public void setDataname(String dataname) {
		this.dataname = dataname;
		this.dataName = dataname;
	}

	public String getTaskid() {
		return taskid;
	}

	public String getTasktype() {
		return tasktype;
	}

	public String getDataname() {
		return dataname;
	}

	public String getIsBatch() {
		return isBatch;
	}

	public void setIsBatch(String isBatch) {
		this.isBatch = isBatch;
	}

	public String getIsLocate() {
		return isLocate;
	}

	public void setIsLocate(String isLocate) {
		this.isLocate = isLocate;
	}

	

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getPositionX() {
		return positionX;
	}

	public void setPositionX(String positionX) {
		this.positionX = positionX;
	}

	public String getPositionY() {
		return positionY;
	}

	public void setPositionY(String positionY) {
		this.positionY = positionY;
	}

	public String getPositionZ() {
		return positionZ;
	}

	public void setPositionZ(String positionZ) {
		this.positionZ = positionZ;
	}

	public String getScoreId() {
		return scoreId;
	}

	public void setScoreId(String scoreId) {
		this.scoreId = scoreId;
	}

	public String getDeviceInfo() {
		return deviceInfo==null?"":deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getEcode() {
		return ecode;
	}

	public void setEcode(String ecode) {
		this.ecode = ecode;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

    public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getPointLevel() {
        return pointLevel;
    }

    public void setPointLevel(String pointLevel) {
        this.pointLevel = pointLevel;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEnterPriseCode() {
        return enterPriseCode;
    }

    public void setEnterPriseCode(String enterPriseCode) {
        this.enterPriseCode = enterPriseCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Date getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(Date photoTime) {
        this.photoTime = photoTime;
    }

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getTerminaFlag() {
        return terminaFlag;
    }

    public void setTerminaFlag(String terminaFlag) {
        this.terminaFlag = terminaFlag;
    }

    public String getPointAccury() {
        return pointAccury;
    }

    public void setPointAccury(String pointAccury) {
        this.pointAccury = pointAccury;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMd5Validate() {
        return md5Validate;
    }

    public void setMd5Validate(String md5Validate) {
        this.md5Validate = md5Validate;
    }

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public String getCoordtype() {
		return coordtype;
	}

	public void setCoordtype(String coordtype) {
		this.coordtype = coordtype;
	}
    
}
