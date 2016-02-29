package com.gd.app.entity;

import java.io.Serializable;

public class DistrictQueryEntity implements Serializable {
	
	private static final long serialVersionUID = 1808797007093380125L;

	private String districtid;

	private String districtname;

	private int total;

	public String getDistrictid() {
		return districtid;
	}

	public void setDistrictid(String districtid) {
		this.districtid = districtid;
	}

	public String getDistrictname() {
		return districtname;
	}

	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
