package com.autonavi.collect.entity;

import com.autonavi.collect.bean.CollectTaskBase;

public class CollectTaskBaseEntity extends CollectTaskBase implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2398249385724111661L;

	private Object extras;
	public Object getExtras() {
		return extras;
	}
	public void setExtras(Object extras) {
		this.extras = extras;
	}
	
	
	

}
