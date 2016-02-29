package cn.dataup.collect.util.bean;

import java.util.ArrayList;
import java.util.List;

public class DateEntity {
	private String id;
	private String typeName;
	private String name;
	private String submitTime;
	private List<DataImgEntiy> imgs=new ArrayList<DataImgEntiy>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public List<DataImgEntiy> getImgs() {
		return imgs;
	}
	public void setImgs(List<DataImgEntiy> imgs) {
		this.imgs = imgs;
	}
	
	

}
