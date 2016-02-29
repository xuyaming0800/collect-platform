package com.autonavi.collect.bean;
/**
 * 超时任务包批次表
 * @author xuyaming
 *
 */
public class CollectPackageTimeoutBatch implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7831963223189923911L;
	private Long id;
	private Integer dsKey;
	private Long updateTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getDsKey() {
		return dsKey;
	}
	public void setDsKey(Integer dsKey) {
		this.dsKey = dsKey;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	
	

}
