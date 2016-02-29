package com.gd.app.entity;

/**
 * 
 * @author
 * @version $Id: Task.java, v 0.1 2013-4-9 下午10:44:04 JunHao Exp $
 */
public class Task {
	private long id;

	private String provinceName;

	private String provinceId;

	private String cityName;

	private String cityId;

	private String districtName;

	private String districtId;

	private String dataname;

	private String x;

	private String y;

	private int status;

	private int tasktype;
	
	private String submitStatus;
	
	private String dataName;
	
	private String taskType;
	
	private String addressType;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDataname() {
		return dataname;
	}

	public void setDataname(String dataname) {
		this.dataname = dataname;
	}

	public int getTasktype() {
		return tasktype;
	}

	public void setTasktype(int tasktype) {
		this.tasktype = tasktype;
	}

	public String getSubmitStatus() {
		return submitStatus;
	}

	public void setSubmitStatus(String submitStatus) {
		this.submitStatus = submitStatus;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

}
