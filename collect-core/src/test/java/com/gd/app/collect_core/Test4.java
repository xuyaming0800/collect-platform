package com.gd.app.collect_core;

import java.io.File;

import org.apache.commons.io.FileUtils;

import autonavi.online.framework.sharding.uniqueid.support.IdWorkerFromSnowflake;


public class Test4 {

	public static void main(String[] args) throws Exception {
		IdWorkerFromSnowflake ids=new IdWorkerFromSnowflake();
		File file_out = new File("/Users/xuyaming/Downloads/out_test_2.sql");
		int workId=31;
		int dbid=31;
		double startX=116.257665;
		double endX=116.347665;
		double startY=40.067725;
		double endY=40.007725;
		int i=2000;
		for(double x=startX;x<=endX;x=x+0.003){
			for(double y=startY;y>=endY;y=y-0.003){
				i++;
				long a=ids.nextId(workId, dbid);
				 FileUtils.write(file_out,"insert into collect_passive_package values("+a+",'小区"+i+"','街道"+i+"',1,10,0,"+i+",unix_timestamp(now()),unix_timestamp(now()),null,null,1446625786,'经济环境拍拍',1200,1200,96,0,null,607750605007582896,3);","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			     FileUtils.write(file_out,"insert into collect_passive_task values("+ids.nextId(workId, dbid)+",'小区"+i+"',0,unix_timestamp(now()),unix_timestamp(now()),null,null,'',null,null,null,1446625786,"+a+",null,0,607750605007582896,3);","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			     FileUtils.write(file_out,"insert into collect_original_coordinate values("+ids.nextId(workId, dbid)+",null,'"+x+"','"+y+"',110108,'2015_01_01_157_test_1.jpg',0,"+a+");","utf-8", true);
			     FileUtils.write(file_out,"\n","utf-8",true);
			}
			
		    
		}
		System.out.println(i);
		
		
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
