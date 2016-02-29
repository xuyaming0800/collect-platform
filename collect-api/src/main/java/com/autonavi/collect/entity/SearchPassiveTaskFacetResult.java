package com.autonavi.collect.entity;


/**
 * 区域查询结果聚合数据
 * @author chunsheng.zhang
 *
 */
public class SearchPassiveTaskFacetResult implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4925954859578448390L;
	
	private String adcode;
	
	/**
	 * 数量
	 */
	private Long count;

	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	

}
