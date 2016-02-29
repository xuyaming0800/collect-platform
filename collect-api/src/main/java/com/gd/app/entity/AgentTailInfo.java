package com.gd.app.entity;

import java.util.Date;

/**
 * 
 * @author ziyu.wei
 * 
 * 轨迹信息
 *
 */
public class AgentTailInfo {
	
	private String userName; 
	private String x; 
	private String y; 
	private String imei; 
	private String altitude;
	private Date gpsTime; 
	private String speed; 
	private String direction; 
	private String pointCount; 
	private String pointAccuracy;
	private String offsetX;
	private String offsetY;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	public Date getGpsTime() {
		return gpsTime;
	}
	public void setGpsTime(Date gpsTime) {
		this.gpsTime = gpsTime;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getPointCount() {
		return pointCount;
	}
	public void setPointCount(String pointCount) {
		this.pointCount = pointCount;
	}
	public String getPointAccuracy() {
		return pointAccuracy;
	}
	public void setPointAccuracy(String pointAccuracy) {
		this.pointAccuracy = pointAccuracy;
	}
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
	

}
