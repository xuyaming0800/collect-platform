package com.gd.app.collect_core;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geo.util.CoordUtils;

import autonavi.online.framework.sharding.uniqueid.support.IdWorkerFromSnowflake;


public class Test10 {

	public static void main(String[] args) throws Exception {
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		File file = new File("/Users/xuyaming/Downloads/dongdan.csv");
		File file_out = new File("/Users/xuyaming/Downloads/dongdan_out.sql");
		int workId=31;
		int dbid=31;
	    List<String> contents = FileUtils.readLines(file);
	    for (String line : contents) {
	    	String[] s=line.split(",");
	    	double[] xy=new double[2];
	    	double[] xy1=new double[2];
	    	CoordUtils.gps2bd(Double.valueOf(s[1]), Double.valueOf(s[2]), xy);
	    	CoordUtils.gps2gcj(Double.valueOf(s[1]), Double.valueOf(s[2]), xy1);
	    	
	    	long a=ids.nextId(workId, dbid);
			FileUtils.write(file_out,"insert into collect_passive_package values("+a+","+s[0]+"','"+s[0]+"',1,10,0,0,unix_timestamp(now()),unix_timestamp(now()),null,null,1446625786,'某小区采集',120,120,96,0,null,607750605007582896,3);","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
		    FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'"+s[0]+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1446625786,"+a+",null,0,607750605007582896,3);","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
		    FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+xy[0]+"','"+xy[1]+"',110108,'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
		    FileUtils.write(file_out,"\n","utf-8",true);
	    }
		
//		System.out.println(i);
		
		
//		for(int i=1;i<=500;i++){
//			long a=ids.nextId(workId, dbid);
//			 FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'小区"+i+"','街道"+i+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1446625786,'某小区采集',1200,1200,96,0,null,3);","utf-8", true);
//		     FileUtils.write(file_out,"\n","utf-8",true);
//		     FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'小区"+i+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1446625786,"+a+",null,0,3);","utf-8", true);
//		     FileUtils.write(file_out,"\n","utf-8",true);
//		     FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'116.30505','39.9821',110108,'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
//		     FileUtils.write(file_out,"\n","utf-8",true);
//		}
       
	}

}
