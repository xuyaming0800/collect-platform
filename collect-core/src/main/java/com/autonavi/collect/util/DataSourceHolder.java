package com.autonavi.collect.util;

import java.util.ArrayList;
import java.util.List;

import autonavi.online.framework.sharding.dao.DynamicDataSource;

public class DataSourceHolder extends DynamicDataSource {
	private List<Integer> dsKey=new ArrayList<Integer>();
	
	public List<Integer> getDsKey() {
		dsKey.clear();
		for(Integer key:DynamicDataSource.targetDataSources.keySet()){
			dsKey.add(key);
		}
		return dsKey;
	}

}