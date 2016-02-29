package com.gd.app.collect_core;

import com.mapabc.service.RegionSearch;
import com.mapabc.shp.io.model.RegionBean;

public class Test5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RegionSearch search=RegionSearch.getInstance("/Java/shape_0508/s04all_region");
		RegionBean r=search.queryRegion(116.5546,39.8963);
		System.out.println(r);

	}

}
